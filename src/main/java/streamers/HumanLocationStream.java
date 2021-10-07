package streamers;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import helper.SparqlFunctions;
import model.ODPair;
import model.PersonMovementTime;
import model.Scheduler;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class HumanLocationStream extends RdfStream implements Runnable {

    private static InfModel infModel;
    private final static String foafPrefix = "http://xmlns.com/foaf/0.1/";
    private final static String rdfPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final static String sbeoPrefix = "https://w3id.org/sbeo#";
    private final static String sosaPrefix = "http://www.w3.org/ns/sosa/";
    private final static String exPrefix = "https://w3id.org/sbeo/example/officescenario#";
    private final static Resource motionStateStanding = ResourceFactory.createResource(exPrefix + "Standing");
    private final static Resource motionStateWalking = ResourceFactory.createResource(exPrefix + "Walking");
    private final static  Resource activityStatusEvacuating = ResourceFactory.createResource(exPrefix + "Evacuating");
    private Property motionState;
    private Property locatedIn;
    private Property atTime;


    private final int timeStep;
    private final String streamIRI;
    private boolean keepRunning = true;
    private long initialTime;

    private static Resource originInstance ;
    private static Resource destinationInstance ;
    private static Resource personInstance;






    public HumanLocationStream(final String iri, int timeStep, InfModel model, long initialTime) {
        super(iri);
        this.streamIRI = iri;
        this.timeStep = timeStep;
        this.infModel = model;
        this.initialTime = System.currentTimeMillis();
    }

    public void pleaseStop() {
        keepRunning = false;
    }

    @Override
    public void run() {

        int count = 1;

        motionState = infModel.getProperty(sbeoPrefix + "hasMotionState");
        locatedIn = infModel.getProperty(sbeoPrefix + "locatedIn");
        atTime = infModel.getProperty(sbeoPrefix+ "atTime");

        List<ODPair> odPairList = new ArrayList<>();
        Scheduler sch = new Scheduler();
        List<String> personNeedToMoveODQueryResult = null;
        List<PersonMovementTime> personWhoFinished;
        OutputStream s;
        long dT;

        try {
            getCostOfAllODPairs(infModel, odPairList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (keepRunning){

            System.out.println("Time Step No: " +  count );
            try {
                dT = System.currentTimeMillis() - initialTime;

                personNeedToMoveODQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel,  "data/Queries/sparql/PersonWhoNeedToMove.txt");
                if(!personNeedToMoveODQueryResult.isEmpty()) {
                    for(int i=0; i < personNeedToMoveODQueryResult.size()-3; i+=4){

                        String p = personNeedToMoveODQueryResult.get(i);
                        String[] tokens = personNeedToMoveODQueryResult.get(i+1).split("\"");
                        String id = tokens[1] + "^^http://www.w3.org/2001/XMLSchema#integer";
                        String origin = personNeedToMoveODQueryResult.get(i+2);
                        String destination = personNeedToMoveODQueryResult.get(i+3);
                        Long tReq = 0L;
                        for(ODPair t: odPairList){
                            if (t.getOrigin().equals(origin) && t.getDestination().equals(destination)){
                                tReq = t.getValue() * 1000;
                                break;
                            }
                        }
                        sch.addMovingPerson(p, tReq, 0, origin, destination, id);
                    }
                }

                updateModelBeforePersonMoves(infModel, sch.getMovingPersons());


//                    s = new FileOutputStream("data/output/4.txt");
//                    RDFDataMgr.write(s, infModel, RDFFormat.TURTLE_PRETTY);
                personWhoFinished = sch.update(dT, sch.getMovingPersons());

                if(!personWhoFinished.isEmpty()) {
                    updateModelWhenPersonFinishedMoving(infModel, personWhoFinished);
                    s = new FileOutputStream("data/output/5.txt");
                    RDFDataMgr.write(s, infModel, RDFFormat.TURTLE_PRETTY);
                    int test = 0;
                }

                this.initialTime = System.currentTimeMillis();
                count++;
                Thread.sleep(timeStep);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateModelWhenPersonFinishedMoving( InfModel infModel, List<PersonMovementTime> list) throws FileNotFoundException {
        RdfQuadruple q;


        for (PersonMovementTime p : list) {
            String observationCounter = String.valueOf(System.currentTimeMillis());
            personInstance = infModel.getResource(p.getPerson());
            destinationInstance = infModel.getResource(p.getDestination());
            infModel.remove(personInstance, motionState, motionStateWalking);
            infModel.add(personInstance, motionState, motionStateStanding);
            infModel.add(personInstance, locatedIn, destinationInstance);

//            System.out.println(p.getPerson() + " "+ p.getOrigin() + " " + p.getDestination());

            q = new RdfQuadruple(exPrefix + "ObsLocation"+observationCounter, rdfPrefix + "type", sosaPrefix+ "Observation", System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(exPrefix + "ObsLocation"+observationCounter, sosaPrefix + "observedProperty", exPrefix+ "HumanDetection", System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(exPrefix + "ObsLocation"+observationCounter, sosaPrefix+ "hasSimpleResult", p.getId()+"^^xsd:int", System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(exPrefix + "ObsLocation"+observationCounter, sbeoPrefix+ "atTime", observationCounter , System.currentTimeMillis());
            this.put(q);

            q = new RdfQuadruple(exPrefix + "ObsLocation"+observationCounter, sosaPrefix+ "madeBySensor", p.getDestination()+"_HumanDetection_Sensor" , System.currentTimeMillis());
            this.put(q);
        }

    }

    private void updateModelBeforePersonMoves( InfModel infModel, List<PersonMovementTime> list) {
        RdfQuadruple q;
        for (PersonMovementTime t : list) {
            personInstance = infModel.getResource(t.getPerson());
            originInstance = infModel.getResource(t.getOrigin());
            infModel.remove(personInstance, motionState, motionStateStanding);
            infModel.remove(personInstance, locatedIn, originInstance);
            infModel.add(personInstance, motionState, motionStateWalking);

            q = new RdfQuadruple(t.getPerson(), sbeoPrefix + "hasMotionState", exPrefix + "Walking", System.currentTimeMillis());
            this.put(q);
        }
    }

    private List<ODPair> getCostOfAllODPairs( InfModel infModel, List<ODPair> list) throws Exception {
        List<String> odPairQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/FindO-DPairs.txt");
        ODPair odp;

        for(int i=0; i < odPairQueryResult.size()-2; i+=3) {
            odp = new ODPair(odPairQueryResult.get(i), odPairQueryResult.get(i+1), odPairQueryResult.get(i+2));
            list.add(odp);
            odp = new ODPair(odPairQueryResult.get(i+1), odPairQueryResult.get(i), odPairQueryResult.get(i+2));
            list.add(odp);
        }
        return list;
    }

    private static String getCurrentTimeStamp() {
        Date date = new java.util.Date();
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
    }



}
