/*
CAREE: Context-AwaRe Emergency Evacuation Software

 Example: sbeo evaluation
*/
import java.io.*;

import eu.larkc.csparql.common.utils.CsparqlUtils;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;

import helper.AutomatedOperations;
import helper.HelpingVariables;
import helper.Output;
import model.CareeInfModel;
import streamers.HumanLocationStreamer;
import streamers.SpaceSensorsStreamer;


public class CAREE {

    private final static long initialTime = System.currentTimeMillis();


    public static void main( String[] args ) throws Exception {

        //Initialization C-SPARQL engine with timestamp function.
        CsparqlEngineImpl engine = new CsparqlEngineImpl();
        engine.initialize(true);

        AutomatedOperations.setPeopleInBuilding(CareeInfModel.Instance().getInfModel(), 10, true);

        //C-SPARQL query for to get the location of each person using their ID.
        String streamQueryLocation = "REGISTER QUERY EachPersonLocation AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/Queries/csparql/humanDetectionInSpacesUsingTheirID.txt");

        String streamQueryTemperatureSensors = "REGISTER QUERY ValueOfEachSensorInstalledInTheBuilding AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/Queries/csparql/TemperatureSensorValues.txt");

        String streamQuerySmokeSensors = "REGISTER QUERY ValueOfEachSensorInstalledInTheBuilding AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/Queries/csparql/SmokeSensorValues.txt");

        String streamQueryHumiditySensors = "REGISTER QUERY ValueOfEachSensorInstalledInTheBuilding AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/Queries/csparql/HumiditySensorValues.txt");

        String streamQuerySpaceAccessibilitySensors = "REGISTER QUERY ValueOfEachSensorInstalledInTheBuilding AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/Queries/csparql/SpaceAccessibilitySensorValues.txt");

        //inserting a static data (schema + data + inferred) in the C-SPARQL as a base model.
        StringWriter staticModel = new StringWriter();
        CareeInfModel.Instance().getInfModel().write(staticModel, "Turtle");
        String result = staticModel.toString();
        engine.putStaticNamedModel(HelpingVariables.sbeoPrefix, result);

        // creating an instance of Random human location data stream.
        HumanLocationStreamer hlStream = new HumanLocationStreamer(HelpingVariables.kbIRI, 1000, true, 1f);
        SpaceSensorsStreamer ssStream = new SpaceSensorsStreamer(HelpingVariables.kbIRI, 1000);

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
