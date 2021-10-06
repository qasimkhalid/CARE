/**
CAREE: Context-AwaRe Emergency Evacuation Software

 Example: sbeo evaluation

*/

//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import eu.larkc.csparql.core.engine.RDFStreamFormatter;
//
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import eu.larkc.csparql.common.utils.CsparqlUtils;


import model.ODPair;
import model.PersonMovementTime;
import model.Scheduler;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import streamers.HumanLocationStream;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

//import static com.hp.hpl.jena.ontology.OntModelSpec.OWL_MEM_RULE_INF;

public class CAREE {

    private final static String outputFileAddress = "data/output/outputAll";
    private final static String ontologyStatic = "https://raw.githubusercontent.com/qasimkhalid/SBEO/master/sbeo.owl";
    private final static String baseIRI = "https://w3id.org/sbeo";
    private final static String kbIRI = "https://w3id.org/sbeo/example/officescenario";
    private final static long initialTime = System.currentTimeMillis();

    private final static String foafPrefix = "http://xmlns.com/foaf/0.1/";
    private final static String rdfPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final static String sbeoPrefix = "https://w3id.org/sbeo#";
    private final static String exPrefix = "https://w3id.org/sbeo/example/officescenario#";



    private static Property rdfType;
    private static Property id;
    private static Property motionState;
    private static Property locatedIn;
    private static Property activityStatus;
    private static Property atTime;

    private static final Resource motionStateStanding = ResourceFactory.createResource(exPrefix + "Standing");
    private static final Resource motionStateWalking = ResourceFactory.createResource(exPrefix + "Walking");
    private static final Resource activityStatusEvacuating = ResourceFactory.createResource(exPrefix + "Evacuating");
    private static Resource personClass;
    private static Resource personInstance;
    private static Resource spaceInstance;

    public static void main( String[] args ) throws Exception {

//        CsparqlEngineImpl engine = new CsparqlEngineImpl();
//        engine.initialize(true);


        OntModel baseModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
        baseModel.read(ontologyStatic, baseIRI, "TURTLE");

        InputStream in = new FileInputStream("G:\\.shortcut-targets-by-id\\1DQfFtktu-cWZCdp7V2zH4sCOhw49RekS\\Qasim-Shared\\sbeo_paper_evaluation_example_modeling\\data\\kb\\initial_scenario.owl");

        OntModel kbModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
        kbModel.read(in, kbIRI, "TURTLE");
        Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();
        reasoner = reasoner.bindSchema(baseModel);

        InfModel infModel = ModelFactory.createInfModel(reasoner, kbModel);

//        OutputStream out = new FileOutputStream(outputFileAddress);
//        RDFDataMgr.write(out, infModel, RDFFormat.TURTLE);

        setPeopleInBuilding(infModel, 10);

        long dT = System.currentTimeMillis() - initialTime;


        Map<List<String>, Long> odPairMap = new HashMap<>();
        List<ODPair> odPairList = new ArrayList<>();;
        getCostOfAllODPairs(infModel, odPairMap, odPairList);

        List<String> personNeedToMoveODQueryResult = getSPARQLQueryResult(infModel, "resource", "data/Queries/PersonWhoNeedToMove.txt");

        List<String> person = new ArrayList<>();
        List<String> personOD;
        Map<String, List<String>> personODMap = new HashMap<>();

        for(int i=0; i < personNeedToMoveODQueryResult.size()-2; i+=3){
            personOD = new ArrayList<>();
            personOD.add(personNeedToMoveODQueryResult.get(i+1));
            personOD.add(personNeedToMoveODQueryResult.get(i+2));
            personODMap.put(personNeedToMoveODQueryResult.get(i), personOD);
            person.add(personNeedToMoveODQueryResult.get(i));
        }

        updateModelBeforePersonMoves(infModel, person);



        Scheduler s = new Scheduler();


        for(String k : personODMap.keySet()){

            List<String> odPair = personODMap.get(k);
            Long tReq = odPairMap.get(odPair);
            if(tReq != null){
                s.addMovingPerson(k, tReq, 0, odPair.get(0), odPair.get(1));
            } else{
                System.out.println("O-D pair not found");
            }
        }

        List<PersonMovementTime> personWhoFinished = s.update(dT, s.getMovingPersons());

        updateModelWhenPersonFinishedMoving(infModel, personWhoFinished);

        OutputStream out = new FileOutputStream(outputFileAddress);
        RDFDataMgr.write(out, infModel, RDFFormat.TURTLE);


        int y = 0;




//        Resource person = infModel.getResource(foafPrefix + "Person");


//        String queryString = CsparqlUtils.fileToString("data/Queries/humanDetectionInSpacesUsingTheirID.txt");
//        Query query = QueryFactory.create(queryString);
//        QueryExecution qExec = QueryExecutionFactory.create(query, infModel);
//        ResultSetFormatter.out(qExec.execSelect());


//        StmtIterator it = infModel.listStatements(null, rdfType, person);
//        while(it.hasNext()){
//            System.out.println(it.next());
//        }


//        OutputStream out = new FileOutputStream(outputFileAddress);
//        RDFDataMgr.write(out, infModel, RDFFormat.TURTLE);



//        if (infModel.contains( null, null, person)) {

//            person = infModel.getResource(foafPrefix+"Person");
//            StmtIterator it = infModel.listStatements(null, rdfType, person);
//            while(it.hasNext()){
//                System.out.println(it.next());
//            }

//            person.addProperty(id, "5");

//        person = infModel.getResource(exPrefix+"Person"+i);
//        StmtIterator it = person.listProperties();
//        while(it.hasNext()){
//            System.out.println(it.next());
//        }



//        }













        // Prefixes
        String prefixes = "PREFIX f: <http://larkc.eu/csparql/sparql/jena/ext#> "
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "PREFIX saref: <https://w3id.org/saref#> "
                + "PREFIX sbeo: <https://w3id.org/sbeo#> "
                + "PREFIX seas: <https://w3id.org/seas/> "
                + "PREFIX sosa: <http://www.w3.org/ns/sosa/> "
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " ;

        //*****//*****//*****//*****////*****//*****//*****//*****//
        //Following is the list of Queries to get Domain Data Knowledge Using Raw Events
        //*****//*****//*****//*****////*****//*****//*****//*****//

//        //Query: Getting the Location of Each Person:
//        String locationEachPersonQuery = "REGISTER STREAM EachPersonLocation AS " + prefixes + CsparqlUtils.fileToString("data/Queries/RawEvents/locationEachPerson");
//
//        //Query: Checking the rise in Temperature (if any):
//        String temperatureRiseForFireCheckQuery = "REGISTER Stream TemperatureRiseForFireCheck AS " + prefixes + CsparqlUtils.fileToString("data/Queries/DomainDataEvents/Space/temperatureRiseForFireCheck");
//
//        //Query: Checking the Smoke (if any):
//        String smokePresenceCheckQuery = "REGISTER Stream SmokePresenceCheck AS " + prefixes + CsparqlUtils.fileToString("data/Queries/DomainDataEvents/Space/smokePresenceCheck");
//
//        //Query: Vulnerable People Movement Detection (if any):
//        String VulnerablePeopleMovementDetectionCheckQuery = "REGISTER STREAM VulnerablePeopleMovementDetection AS " + prefixes + CsparqlUtils.fileToString("data/Queries/DomainDataEvents/Person/VulnerablePeopleMovementDetection");
//
//        //Query: Person Moving Without Authorisation (if any):
//        String NonVulnerablePeopleMovementDetectionCheckQuery = "REGISTER STREAM NonVulnerablePeopleMovementDetection AS " + prefixes + CsparqlUtils.fileToString("data/Queries/DomainDataEvents/Person/NonVulnerablePeopleMovementDetection");
//
//        //Query: Vulnerable Person Moved With Check (if any):
//        String EvacuationStatusCompletedCheckQuery = "REGISTER STREAM EvacuationStatusComplete AS " + prefixes + CsparqlUtils.fileToString("data/Queries/DomainDataEvents/Person/EvacuationStatusComplete");

        //*****//*****//*****//*****////*****//*****//*****//*****//
        //Following is the list of Queries to get Information about Hazardous Events Using Domain Data Events
        //*****//*****//*****//*****////*****//*****//*****//*****//

        //Query: Fire Detection (if any):
//        String fireCheckQuery = "REGISTER STREAM FireCheck AS " + prefixes + CsparqlUtils.fileToString("data/Queries/HazardousEvents/fireCheck");




        /*
         * Loading data in the form of streams
         */
        HumanLocationStream hlStream = new HumanLocationStream(kbIRI, 1000, infModel);

        /*
         * Registration of streams in the CSPARQL engine
         */

//        engine.registerStream(hlStream);

        /*
         * Thread of data streams
         */

        Thread hlStreamThread = new Thread(hlStream);


        /*
         * Using of static knowledge; data from SBEvac Ontology
         */
//        engine.putStaticNamedModel(baseIRI, ontologyStatic);


        /*
         *  Registration of SPARQL Queries on streams.
         */
//        CsparqlQueryResultProxy eachPersonLocation = engine.registerQuery(locationEachPersonQuery, false);
//        CsparqlQueryResultProxy temperatureRiseForFireCheck = engine.registerQuery(temperatureRiseForFireCheckQuery, false);
//        CsparqlQueryResultProxy smokePresenceCheck = engine.registerQuery(smokePresenceCheckQuery, false);
//        CsparqlQueryResultProxy EvacuationStatusCompleted = engine.registerQuery(EvacuationStatusCompletedCheckQuery, false);
//        CsparqlQueryResultProxy EvacuationStatusCompleted = engine.registerQuery(EvacuationStatusCompletedCheckQuery, false);


        /*
         * Create new stream formatter to create new RDF stream from the results of a query
         */
//        RDFStreamFormatter SmokeDetectionStreamFormatter = new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbeo/SmokeEvents/SmokeDetected/");
//        RDFStreamFormatter TemperatureRiseStreamFormatter = new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbeo/TemperatureEvents/TemperatureRisen/");
//        RDFStreamFormatter LocationEachPersonStreamFormatter = new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbeo/LocationEvents/LocationUpdate/");
//        RDFStreamFormatter VulnerablePeopleMovementDetectionStreamFormatter = new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbeo/LocationEvents/VulnerablePeopleMovementDetection/");
//        RDFStreamFormatter NonVulnerablePeopleMovementDetectionStreamFormatter = new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbeo/LocationEvents/NonVulnerablePeopleMovementDetection/");
//        RDFStreamFormatter VulnerablePersonMovedWithStreamFormatter = new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbeo/LocationEvents/VulnerablePersonMovedWith/");

        /*
         *  Register the new streams in the engine.
         */
//        engine.registerStream(TemperatureRiseStreamFormatter);
//        engine.registerStream(SmokeDetectionStreamFormatter);
//        engine.registerStream(LocationEachPersonStreamFormatter);
//        engine.registerStream(VulnerablePeopleMovementDetectionStreamFormatter);
//        engine.registerStream(NonVulnerablePeopleMovementDetectionStreamFormatter);
//        engine.registerStream(VulnerablePersonMovedWithStreamFormatter);



        /*
         * Register the query to consume the results(as a stream) of the multiple registered queries
         */
//        CsparqlQueryResultProxy FireCheck = engine.registerQuery(fireCheckQuery, false);
//        CsparqlQueryResultProxy VulnerablePeopleMovementDetection = engine.registerQuery(VulnerablePeopleMovementDetectionCheckQuery, false);
//        CsparqlQueryResultProxy NonVulnerablePeopleMovementDetection = engine.registerQuery(NonVulnerablePeopleMovementDetectionCheckQuery, false);

        /*
         *  Add the observers to queries to get the results of the queries instantaneously
         */
//        temperatureRiseForFireCheck.addObserver(TemperatureRiseStreamFormatter);
//        smokePresenceCheck.addObserver(SmokeDetectionStreamFormatter);
//        FireCheck.addObserver(new Output("FireCheck", outputFileAddress));
//        eachPersonLocation.addObserver(new Output("EachPersonLocationUpdate", outputFileAddress));
//        eachPersonLocation.addObserver(LocationEachPersonStreamFormatter);
//        VulnerablePeopleMovementDetection.addObserver(new Output("VulnerablePeopleMovement", outputFileAddress));
//        NonVulnerablePeopleMovementDetection.addObserver(new Output("NonVulnerablePeopleMovement", outputFileAddress));
//        EvacuationStatusCompleted.addObserver(new Output("EvacuationStatusCompleted", outputFileAddress));


        /*
         * Start streaming data
         */
        hlStreamThread.start();


        try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        /*
         * clean up (i.e., unregister query and stream)
         */

        hlStream.pleaseStop();
//        engine.unregisterStream(hlStream.getIRI());

        System.exit(0);


    }

    private static void updateModelWhenPersonFinishedMoving( InfModel infModel, List<PersonMovementTime> list) {
        for (PersonMovementTime p : list) {
            personInstance = infModel.getResource(p.getPerson());
            locatedIn = infModel.getProperty(sbeoPrefix + "locatedIn");
            motionState = infModel.getProperty(sbeoPrefix + "hasMotionState");
            atTime = infModel.getProperty(sbeoPrefix+ "atTime");
            infModel.remove(personInstance, motionState, motionStateWalking);
            infModel.add(personInstance, motionState, motionStateStanding);
            infModel.add(personInstance, locatedIn, p.getDestination());
            infModel.remove(infModel.getRequiredProperty(personInstance, atTime));
            infModel.addLiteral(personInstance, atTime, System.currentTimeMillis());
        }
    }

    private static void updateModelBeforePersonMoves( InfModel infModel, List<String> list) {
            for (String p : list) {
                personInstance = infModel.getResource(p);
                locatedIn = infModel.getProperty(sbeoPrefix + "locatedIn");
                motionState = infModel.getProperty(sbeoPrefix + "hasMotionState");
                infModel.remove(personInstance, motionState, motionStateStanding);
                infModel.add(personInstance, motionState, motionStateWalking);
                infModel.remove(infModel.getRequiredProperty(personInstance, locatedIn));
            }
    }

    private static void getCostOfAllODPairs( InfModel infModel, Map<List<String>, Long> odPairMap,  List<ODPair> odPairList) throws Exception {
        List<String> odPairQueryResult = getSPARQLQueryResult(infModel, "literal", "data/Queries/FindO-DPairs.txt");
        List<String> originDestination;
        ODPair odp;

        for(int i=0; i < odPairQueryResult.size()-2; i+=3) {
            originDestination = new ArrayList<>();
            originDestination.add(odPairQueryResult.get(i));
            originDestination.add(odPairQueryResult.get(i+1));
            odp = new ODPair(odPairQueryResult.get(i), odPairQueryResult.get(i+1), odPairQueryResult.get(i+2), originDestination);
            odPairList.add(odp);
            odPairMap.put(originDestination, odp.getValue());

            originDestination = new ArrayList<>();
            originDestination.add(odPairQueryResult.get(i+1));
            originDestination.add(odPairQueryResult.get(i));
            odp = new ODPair(odPairQueryResult.get(i+1), odPairQueryResult.get(i), odPairQueryResult.get(i+2), originDestination);
            odPairList.add(odp);
            odPairMap.put(originDestination, odp.getValue());
        }
    }

    private static List<String> getSPARQLQueryResult( InfModel infModel, String objType, String path ) throws Exception {
        String queryString = CsparqlUtils.fileToString(path);
        Query query = QueryFactory.create(queryString);
        QueryExecution qExec = QueryExecutionFactory.create(query, infModel);
        List<QuerySolution> resultRaw = ResultSetFormatter.toList(qExec.execSelect());
        List<String> result = new ArrayList<>();
        if (resultRaw.size() != 0) {
            for (QuerySolution a : resultRaw) {
                String str = a.toString();
//                String[] tokens = (objType.equals("literal")) ?  str.split("\\(|= <|> \\)|\\?val =| ") : str.split("\\(|= <|> \\)");
                String[] tokens = str.split("\\(|= <|> \\)|\\?val =");
                for (int i = 2; i < tokens.length; i += 3) {
                        result.add(tokens[i]);
                }
            }
        }
        return result;
    }


    private static List<QuerySolution> getSelectQueryResult( InfModel infModel, String path ) throws Exception {
        String queryString = CsparqlUtils.fileToString(path);
        Query query = QueryFactory.create(queryString);
        QueryExecution qExec = QueryExecutionFactory.create(query, infModel);
        return ResultSetFormatter.toList(qExec.execSelect());
    }

    private static void setPeopleInBuilding( InfModel infModel, int peopleCount ) throws Exception {
        int randomPersonsCount = randomNumberSelector(peopleCount,1);
        personClass = infModel.getResource(foafPrefix + "Person");

        rdfType = infModel.getProperty(rdfPrefix+ "type");
        id = infModel.getProperty(sbeoPrefix+ "id");
        motionState = infModel.getProperty(sbeoPrefix+ "hasMotionState");
        locatedIn = infModel.getProperty(sbeoPrefix+ "locatedIn");
        atTime = infModel.getProperty(sbeoPrefix+ "atTime");
        activityStatus = infModel.getProperty(sbeoPrefix+ "hasActivityStatus");

        List<String> availableSpaces = getSPARQLQueryResult(infModel,  "resource", "data/Queries/FindAllAvailableSpacesInBuilding.txt");
        for(int i=1 ; i <= randomPersonsCount; i++){
            personInstance = ResourceFactory.createResource( exPrefix+ "Person" +i);

            infModel.add(personInstance, rdfType,  personClass);
            infModel.add(personInstance, motionState, motionStateStanding);
            infModel.addLiteral(personInstance, id, i);
            Random random = new Random();
            if(random.nextBoolean()){
                infModel.add(personInstance, activityStatus, activityStatusEvacuating);
            }

            String rs = availableSpaces.get(random.nextInt(availableSpaces.size()));
            spaceInstance = ResourceFactory.createResource(rs);
            infModel.add(personInstance, locatedIn, spaceInstance);
            infModel.addLiteral(personInstance, atTime, initialTime);
        }
    }



    private static String getCurrentTimeStamp() {
        Date date = new java.util.Date();
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
    }

    public static int randomNumberSelector(int max, int min) {
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }




}
