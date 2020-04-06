//CAREE: Context-AwaRe Emergency Evacuation



package main.java;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;
import eu.larkc.csparql.core.engine.RDFStreamFormatter;
import main.java.qasim.data.Output;

import eu.larkc.csparql.common.utils.CsparqlUtils;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;

import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.qasim.data.streamers.LocationSensorStream;
import main.java.qasim.data.streamers.TemperatureSensorStream;
import main.java.qasim.data.streamers.SmokeSensorStream;

import java.io.*;

//import static com.hp.hpl.jena.ontology.OntModelSpec.OWL_MEM_RULE_INF;

public class CAREE {

        private static Logger logger = LoggerFactory.getLogger(CAREE.class);

    public static void main(String[] args) {

        try{

            /*Configure log4j logger for the csparql engine
            */
            PropertyConfigurator.configure("log4j_configuration/csparql_readyToGoPack_log4j.properties");

            //Stop logging the debug level to gain a better view on the console
            org.apache.log4j.Logger.getRootLogger().setLevel(Level.ERROR);

            //Create csparql engine instance
            CsparqlEngineImpl engine = new CsparqlEngineImpl();

            /*Initialize the engine instance
            *The initialization creates the static engine (SPARQL) and the stream engine (CEP)
            */
            engine.initialize(true);

            // Output Files Cleaning
            FileOutputStream writer = new FileOutputStream("outputFiles/EachPersonLocationUpdate");
            FileOutputStream writer1 = new FileOutputStream("outputFiles/FireCheck");
            FileOutputStream writer2 = new FileOutputStream("outputFiles/VulnerablePeopleMovement");
            FileOutputStream writer3 = new FileOutputStream("outputFiles/NonVulnerablePeopleMovement");
            FileOutputStream writer4 = new FileOutputStream("outputFiles/EvacuationStatusCompleted");

            // Input Files location (Data from Sensors)
            final String streamFileLocationSensors= "Data_CSPARQL_Qasim/Dataset/usermovementsScenario1.stream";
            final String streamFileTemperatureSensors= "Data_CSPARQL_Qasim/Dataset/TempScenario1.stream";
            final String streamFileSmokeSensors= "Data_CSPARQL_Qasim/Dataset/SmokeScenario1.stream";
            final String ontologyStatic = "Data_CSPARQL_Qasim/Ontology/SBEvac_Ontology_1.6_Scenario_1_v0.2_A-Box(Turtle).owl";


            InputStream in = FileManager.get().open(ontologyStatic);
            if (in == null) {
                throw new IllegalArgumentException( "File: " + ontologyStatic + " not found");
            }



            OntModel baseModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
            baseModel.read(in,null, "TURTLE" );

            Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
            InfModel baseModelInferred = ModelFactory.createInfModel(reasoner, baseModel);



            // Stream IRI
            String iri1 = "http://www.ia.urjc.es/ontologies/building/sbevac/LocationEvents/";
            String iri2 = "http://www.ia.urjc.es/ontologies/building/sbevac/TemperatureEvents/";
            String iri3 = "http://www.ia.urjc.es/ontologies/building/sbevac/SmokeEvents/";

            // Prefixes
            String prefixes =   "PREFIX f: <http://larkc.eu/csparql/sparql/jena/ext#> "
                                + "PREFIX loc: <http://www.ia.urjc.es/ontologies/building/sbevac/LocationEvents/> "
                                + "PREFIX temp: <http://www.ia.urjc.es/ontologies/building/sbevac/TemperatureEvents/> "
                                + "PREFIX smok: <http://www.ia.urjc.es/ontologies/building/sbevac/SmokeEvents/> "
                                + "PREFIX : <http://www.ia.urjc.es/ontologies/building/sbevac#> "
                                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
                                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                                + "PREFIX saref: <https://w3id.org/saref#> " ;

            //*****//*****//*****//*****////*****//*****//*****//*****//
            //Following is the list of Queries to get Domain Data Knowledge Using Raw Events
            //*****//*****//*****//*****////*****//*****//*****//*****//

            //Query: Getting the Location of Each Person:
            String locationEachPersonQuery = "REGISTER STREAM EachPersonLocation AS " + prefixes + CsparqlUtils.fileToString("Data_CSPARQL_Qasim/Queries/RawEvents/locationEachPerson");

            //Query: Checking the rise in Temperature (if any):
            String temperatureRiseForFireCheckQuery = "REGISTER Stream TemperatureRiseForFireCheck AS " + prefixes + CsparqlUtils.fileToString("Data_CSPARQL_Qasim/Queries/DomainDataEvents/Space/temperatureRiseForFireCheck");

            //Query: Checking the Smoke (if any):
            String smokePresenceCheckQuery =  "REGISTER Stream SmokePresenceCheck AS " + prefixes + CsparqlUtils.fileToString("Data_CSPARQL_Qasim/Queries/DomainDataEvents/Space/smokePresenceCheck");

            //Query: Vulnerable People Movement Detection (if any):
            String VulnerablePeopleMovementDetectionCheckQuery = "REGISTER STREAM VulnerablePeopleMovementDetection AS " + prefixes + CsparqlUtils.fileToString("Data_CSPARQL_Qasim/Queries/DomainDataEvents/Person/VulnerablePeopleMovementDetection");

            //Query: Person Moving Without Authorisation (if any):
            String NonVulnerablePeopleMovementDetectionCheckQuery = "REGISTER STREAM NonVulnerablePeopleMovementDetection AS " + prefixes + CsparqlUtils.fileToString("Data_CSPARQL_Qasim/Queries/DomainDataEvents/Person/NonVulnerablePeopleMovementDetection");

            //Query: Vulnerable Person Moved With Check (if any):
            String EvacuationStatusCompletedCheckQuery = "REGISTER STREAM EvacuationStatusComplete AS " + prefixes + CsparqlUtils.fileToString("Data_CSPARQL_Qasim/Queries/DomainDataEvents/Person/EvacuationStatusComplete");

            //*****//*****//*****//*****////*****//*****//*****//*****//
            //Following is the list of Queries to get Information about Hazardous Events Using Domain Data Events
            //*****//*****//*****//*****////*****//*****//*****//*****//

            //Query: Fire Detection (if any):
            String fireCheckQuery = "REGISTER STREAM FireCheck AS " + prefixes + CsparqlUtils.fileToString("Data_CSPARQL_Qasim/Queries/HazardousEvents/fireCheck");




            /*
             * Loading data in the form of streams
             */
            LocationSensorStream L1 = new LocationSensorStream(iri1,1000, streamFileLocationSensors);
            TemperatureSensorStream T1 = new TemperatureSensorStream(iri2,1000, streamFileTemperatureSensors);
            SmokeSensorStream S1 = new SmokeSensorStream(iri3,1000, streamFileSmokeSensors);

            /*
             * Registration of streams in the CSPARQL engine
             */
            engine.registerStream(L1);
            engine.registerStream(T1);
            engine.registerStream(S1);
//            engine.registerStream(fb);

            /*
             * Thread of data streams
             */

            Thread L1Thread = new Thread(L1);
            Thread T1Thread = new Thread(T1);
            Thread S1Thread = new Thread(S1);


            /*
             * Using of static knowledge; data from SBEvac Ontology
             */
            engine.putStaticNamedModel("http://www.ia.urjc.es/ontologies/building/sbevac_scenario", CsparqlUtils.serializeRDFFile(ontologyStatic));


            /*
             *  Registration of SPARQL Queries on streams.
             */
            CsparqlQueryResultProxy eachPersonLocation = engine.registerQuery(locationEachPersonQuery, false);
            CsparqlQueryResultProxy temperatureRiseForFireCheck = engine.registerQuery(temperatureRiseForFireCheckQuery, false);
            CsparqlQueryResultProxy smokePresenceCheck = engine.registerQuery(smokePresenceCheckQuery, false);
            CsparqlQueryResultProxy EvacuationStatusCompleted = engine.registerQuery(EvacuationStatusCompletedCheckQuery, false);


              /*
               * Create new stream formatter to create new RDF stream from the results of a query
               */
            RDFStreamFormatter SmokeDetectionStreamFormatter = new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbevac/SmokeEvents/SmokeDetected/");
            RDFStreamFormatter  TemperatureRiseStreamFormatter= new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbevac/TemperatureEvents/TemperatureRisen/");
            RDFStreamFormatter  LocationEachPersonStreamFormatter= new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbevac/LocationEvents/LocationUpdate/");
            RDFStreamFormatter  VulnerablePeopleMovementDetectionStreamFormatter= new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbevac/LocationEvents/VulnerablePeopleMovementDetection/");
            RDFStreamFormatter  NonVulnerablePeopleMovementDetectionStreamFormatter= new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbevac/LocationEvents/NonVulnerablePeopleMovementDetection/");
            RDFStreamFormatter  VulnerablePersonMovedWithStreamFormatter= new RDFStreamFormatter("http://www.ia.urjc.es/ontologies/building/sbevac/LocationEvents/VulnerablePersonMovedWith/");

            /*
             *  Register the new streams in the engine.
             */
            engine.registerStream(TemperatureRiseStreamFormatter);
            engine.registerStream(SmokeDetectionStreamFormatter);
            engine.registerStream(LocationEachPersonStreamFormatter);
            engine.registerStream(VulnerablePeopleMovementDetectionStreamFormatter);
            engine.registerStream(NonVulnerablePeopleMovementDetectionStreamFormatter);
            engine.registerStream(VulnerablePersonMovedWithStreamFormatter);



            /*
             * Register the query to consume the results(as a stream) of the multiple registered queries
             */
            CsparqlQueryResultProxy FireCheck = engine.registerQuery(fireCheckQuery, false);
            CsparqlQueryResultProxy VulnerablePeopleMovementDetection = engine.registerQuery(VulnerablePeopleMovementDetectionCheckQuery, false);
            CsparqlQueryResultProxy NonVulnerablePeopleMovementDetection = engine.registerQuery(NonVulnerablePeopleMovementDetectionCheckQuery, false);

            /*
             *  Add the observers to queries to get the results of the queries instantaneously
             */
            temperatureRiseForFireCheck.addObserver(TemperatureRiseStreamFormatter);
            smokePresenceCheck.addObserver(SmokeDetectionStreamFormatter);
            FireCheck.addObserver(new Output("FireCheck", "outputFiles/FireCheck"));

            eachPersonLocation.addObserver(new Output("EachPersonLocationUpdate", "outputFiles/EachPersonLocationUpdate"));
            eachPersonLocation.addObserver(LocationEachPersonStreamFormatter);
            VulnerablePeopleMovementDetection.addObserver(new Output("VulnerablePeopleMovement", "outputFiles/VulnerablePeopleMovement"));
            NonVulnerablePeopleMovementDetection.addObserver(new Output("NonVulnerablePeopleMovement", "outputFiles/NonVulnerablePeopleMovement"));

            EvacuationStatusCompleted.addObserver(new Output("EvacuationStatusCompleted", "outputFiles/EvacuationStatusCompleted"));


            /*
             * Start streaming data
             */
            L1Thread.start();
            T1Thread.start();
            S1Thread.start();

            try {
                Thread.sleep(200000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            /*
             * clean up (i.e., unregister query and stream)
             */
            S1.pleaseStop();
            engine.unregisterStream(S1.getIRI());

            T1.pleaseStop();
            engine.unregisterStream(T1.getIRI());

            L1.pleaseStop();
            engine.unregisterStream(L1.getIRI());


            System.exit(0);


        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }





    }

}
