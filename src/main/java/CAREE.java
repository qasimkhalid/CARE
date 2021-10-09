/*
CAREE: Context-AwaRe Emergency Evacuation Software

 Example: sbeo evaluation
*/

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import eu.larkc.csparql.common.utils.CsparqlUtils;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import eu.larkc.csparql.core.engine.ConsoleFormatter;
import helper.MathOperations;
import helper.SparqlFunctions;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import streamers.HumanLocationStream;

import java.io.*;
import java.util.List;
import java.util.Random;

public class CAREE {

    public volatile static InfModel infModel;
    private final static String outputFileAddress = "data/output/outputAll";
    private final static String ontologyStatic = "https://raw.githubusercontent.com/qasimkhalid/SBEO/master/sbeo.owl";
    private final static String baseIRI = "https://w3id.org/sbeo";
    private final static String kbIRI = "https://w3id.org/sbeo/example/officescenario";
    private final static long initialTime = System.currentTimeMillis();


    private static Property motionState;
    private static Property locatedIn;
    private static Property atTime;
    private final static String foafPrefix = "http://xmlns.com/foaf/0.1/";
    private final static String rdfPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final static String sbeoPrefix = "https://w3id.org/sbeo#";
    private final static String exPrefix = "https://w3id.org/sbeo/example/officescenario#";
    private static final Resource motionStateStanding = ResourceFactory.createResource(exPrefix + "Standing");
    private static final Resource motionStateWalking = ResourceFactory.createResource(exPrefix + "Walking");
    private static final Resource activityStatusEvacuating = ResourceFactory.createResource(exPrefix + "Evacuating");
    private static Resource personInstance;

    public static void main( String[] args ) throws Exception {

        CsparqlEngineImpl engine = new CsparqlEngineImpl();
        engine.initialize(true);


        OntModel baseModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
        baseModel.read(ontologyStatic, baseIRI, "TURTLE");

//        InputStream in = new FileInputStream("G:\\.shortcut-targets-by-id\\1DQfFtktu-cWZCdp7V2zH4sCOhw49RekS\\Qasim-Shared\\sbeo_paper_evaluation_example_modeling\\data\\kb\\initial_scenario.owl");
//
        InputStream in = new FileInputStream("data/kb/initial_scenario.owl");

        OntModel kbModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
        kbModel.read(in, kbIRI, "TURTLE");
        Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();
        reasoner = reasoner.bindSchema(baseModel);

        infModel = ModelFactory.createInfModel(reasoner, kbModel);

//        OutputStream s = new FileOutputStream("data/output/1.txt");
//        RDFDataMgr.write(s, infModel, RDFFormat.TURTLE_PRETTY);

        setPeopleInBuilding(infModel, 10, true);

        String prefixes = "PREFIX f: <http://larkc.eu/csparql/sparql/jena/ext#> "
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "PREFIX sbeo: <https://w3id.org/sbeo#> "
                + "PREFIX seas: <https://w3id.org/seas/> "
                + "PREFIX sosa: <http://www.w3.org/ns/sosa/> "
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "PREFIX ex: <https://w3id.org/sbeo/example/officescenario#>";

        String StreamQueryLocation = "REGISTER QUERY EachPersonLocation AS " + prefixes + CsparqlUtils.fileToString("data/Queries/csparql/humanDetectionInSpacesUsingTheirID.txt");

        StringWriter staticModel = new StringWriter();
        infModel.write(staticModel, "Turtle");
        String result = staticModel.toString();
        engine.putStaticNamedModel(sbeoPrefix, result);

//        s = new FileOutputStream("data/output/2.txt");
//        RDFDataMgr.write(s, infModel, RDFFormat.TURTLE_PRETTY);

        HumanLocationStream hlStream = new HumanLocationStream(kbIRI, 1000, infModel);


        engine.registerStream(hlStream);

        Thread hlStreamThread = new Thread(hlStream);


        CsparqlQueryResultProxy PersonLocation = engine.registerQuery(StreamQueryLocation, false);

//        EvacuationStatusCompleted.addObserver(new Output(outputFileAddress));
        PersonLocation.addObserver(new ConsoleFormatter());


        System.out.println("About to start the streaming thread...");
        hlStreamThread.start();

        System.out.println("First thread about to go to sleep for long time...");
        try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("About to stop the thread and unregistering the stream");
        hlStream.pleaseStop();
        engine.unregisterStream(hlStream.getIRI());

        System.out.println("About to exit");
        System.exit(0);


    }

    private static void setPeopleInBuilding( InfModel infModel, int peopleCount, boolean allPersonMove) throws Exception {
        int randomPersonsCount;

        if(allPersonMove){
            randomPersonsCount = peopleCount;
        } else {
            randomPersonsCount = MathOperations.randomNumberSelector(peopleCount,1);
        }

        Resource personClass = infModel.getResource(foafPrefix + "Person");

        Property rdfType = infModel.getProperty(rdfPrefix + "type");
        Property id = infModel.getProperty(sbeoPrefix + "id");
        motionState = infModel.getProperty(sbeoPrefix+ "hasMotionState");
        locatedIn = infModel.getProperty(sbeoPrefix+ "locatedIn");
        atTime = infModel.getProperty(sbeoPrefix+ "atTime");
        Property activityStatus = infModel.getProperty(sbeoPrefix + "hasActivityStatus");

        List<String> availableSpaces = SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/FindAllAvailableSpacesInBuilding.txt");

        for(int i=1 ; i <= randomPersonsCount; i++){
            personInstance = ResourceFactory.createResource( exPrefix+ "Person" +i);
            infModel.add(personInstance, rdfType, personClass);
            infModel.add(personInstance, motionState, motionStateStanding);
            infModel.addLiteral(personInstance, id, i);

            Random random = new Random();
            if(allPersonMove){
                infModel.add(personInstance, activityStatus, activityStatusEvacuating);
            } else if (random.nextBoolean()) {
                infModel.add(personInstance, activityStatus, activityStatusEvacuating);
            }


            String rs = availableSpaces.get(random.nextInt(availableSpaces.size()));
            Resource spaceInstance = ResourceFactory.createResource(rs);
            infModel.add(personInstance, locatedIn, spaceInstance);
            infModel.addLiteral(personInstance, atTime, initialTime);
        }
    }

}
