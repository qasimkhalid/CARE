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
import helper.AutomatedOperations;
import helper.Output;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import streamers.HumanLocationStream;
import streamers.SpaceSensorsStreamer;

import java.io.*;

public class CAREE {

//    public volatile static InfModel infModel;
    public static InfModel infModel;
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

    public static synchronized void main( String[] args ) throws Exception {

        //Intialization C-SPARQL engine with timestamp function.
        CsparqlEngineImpl engine = new CsparqlEngineImpl();
        engine.initialize(true);

        //read the latest version of schema from the git repository.
        OntModel baseModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
        baseModel.read(ontologyStatic, baseIRI, "TURTLE");

        //read the data (includes the information about the building, such as distance, location of sensors) file based on the schema as an input .
//        InputStream in = new FileInputStream("G:\\.shortcut-targets-by-id\\1DQfFtktu-cWZCdp7V2zH4sCOhw49RekS\\Qasim-Shared\\sbeo_paper_evaluation_example_modeling\\data\\kb\\initial_scenario.owl");
        InputStream in = new FileInputStream("data/kb/initial_scenario.owl");

        //binding the schema with data using a light reasoner(just transitive and symmetric inferences).
        OntModel kbModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
        kbModel.read(in, kbIRI, "TURTLE");
        Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();
        reasoner = reasoner.bindSchema(baseModel);
        infModel = ModelFactory.createInfModel(reasoner, kbModel);

//        OutputStream s = new FileOutputStream("data/output/1.txt");
//        RDFDataMgr.write(s, infModel, RDFFormat.TURTLE_PRETTY);

        AutomatedOperations.setPeopleInBuilding(infModel, 10, true);

        //prefixed for the C-SPARQL Query.
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

        //C-SPARQL query for to get the location of each person using their ID.
        String streamQueryLocation = "REGISTER QUERY EachPersonLocation AS " + prefixes + CsparqlUtils.fileToString("data/Queries/csparql/humanDetectionInSpacesUsingTheirID.txt");

        String streamQueryTemperatureSensors = "REGISTER QUERY ValueOfEachSensorInstalledInTheBuilding AS " + prefixes + CsparqlUtils.fileToString("data/Queries/csparql/TemperatureSensorValues.txt");

        String streamQuerySmokeSensors = "REGISTER QUERY ValueOfEachSensorInstalledInTheBuilding AS " + prefixes + CsparqlUtils.fileToString("data/Queries/csparql/SmokeSensorValues.txt");

        String streamQueryHumiditySensors = "REGISTER QUERY ValueOfEachSensorInstalledInTheBuilding AS " + prefixes + CsparqlUtils.fileToString("data/Queries/csparql/HumiditySensorValues.txt");

        String streamQuerySpaceAccessibilitySensors = "REGISTER QUERY ValueOfEachSensorInstalledInTheBuilding AS " + prefixes + CsparqlUtils.fileToString("data/Queries/csparql/SpaceAccessibilitySensorValues.txt");

        //inserting a static data (schema + data + inferred) in the C-SPARQL as a base model.
        StringWriter staticModel = new StringWriter();
        infModel.write(staticModel, "Turtle");
        String result = staticModel.toString();
        engine.putStaticNamedModel(sbeoPrefix, result);

//        s = new FileOutputStream("data/output/2.txt");
//        RDFDataMgr.write(s, infModel, RDFFormat.TURTLE_PRETTY);

        // creating an instance of Random human location data stream.
        HumanLocationStream hlStream = new HumanLocationStream(kbIRI, 1000, infModel, false, 2f);
        SpaceSensorsStreamer ssStream = new SpaceSensorsStreamer(kbIRI, 1000, infModel);

        //Injecting the stream in the C-SPARQL engine.
        engine.registerStream(hlStream);
        engine.registerStream(ssStream);

        //Binding the stream with a new thread.
        Thread hlStreamThread = new Thread(hlStream);
        Thread ssStreamThread = new Thread(ssStream);

        //Creating an instance of the listener and registering the C-SPARQL query.
        CsparqlQueryResultProxy personLocation = engine.registerQuery(streamQueryLocation, false);
        CsparqlQueryResultProxy temperatureSensorValues = engine.registerQuery(streamQueryTemperatureSensors, false);
        CsparqlQueryResultProxy smokeSensorValues = engine.registerQuery(streamQuerySmokeSensors, false);
        CsparqlQueryResultProxy humiditySensorValues = engine.registerQuery(streamQueryHumiditySensors, false);
        CsparqlQueryResultProxy spaceAccessibilitySensorValues = engine.registerQuery(streamQuerySpaceAccessibilitySensors, false);

        //Adding an observer to the instance of the listener
//        EvacuationStatusCompleted.addObserver(new helper.Output(outputFileAddress));
        personLocation.addObserver(new Output("data/output/HumanLocationsBySpace.txt"));
        temperatureSensorValues.addObserver(new Output("data/output/TemperatureSensorValueAfterEachTimeStep.txt"));
        smokeSensorValues.addObserver(new Output("data/output/SmokeSensorValueAfterEachTimeStep.txt"));
        humiditySensorValues.addObserver(new Output("data/output/HumiditySensorValueAfterEachTimeStep.txt"));
        spaceAccessibilitySensorValues.addObserver(new Output("data/output/SpaceAccessiblitySensorValueAfterEachTimeStep.txt"));

        //Starting all threads of streamers
        System.out.println("About to start the streaming threads...");
        hlStreamThread.start();
        ssStreamThread.start();


        System.out.println("First thread about to go to sleep for long time...");
        try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("About to stop the thread and unregistering the stream");
        hlStream.stop();
        ssStream.stop();
        engine.unregisterStream(hlStream.getIRI());
        engine.unregisterStream(ssStream.getIRI());

        System.out.println("About to exit");
        System.exit(0);


    }



}
