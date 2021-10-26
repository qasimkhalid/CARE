/*
CAREE: Context-AwaRe Emergency Evacuation Software

 Example: sbeo evaluation
*/
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import eu.larkc.csparql.common.utils.CsparqlUtils;
import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;

import helper.AutomatedOperations;
import helper.HelpingVariables;
import helper.Output;
import model.CareeInfModel;
import model.ODPair;
import model.Space;
import streamers.HumanLocationStreamer;
import streamers.SpaceSensorsStreamer;


public class CAREE {

    private final static long initialTime = System.currentTimeMillis();

    public static void main( String[] args ) throws Exception {

        //Initialization C-SPARQL engine with timestamp function.
        CsparqlEngineImpl engine = new CsparqlEngineImpl();
        engine.initialize(true);

        AutomatedOperations.setPeopleInBuilding(CareeInfModel.Instance().getInfModel(), 10, false, 2);

        //C-SPARQL query for to get the location of each person using their ID.

        String streamQueryEdge = "REGISTER QUERY EachEdge AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/Queries/csparql/_m/edge_m.txt");

        String streamQueryNode = "REGISTER QUERY EachNode AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/Queries/csparql/_m/node_m.txt");

        String streamQueryPersonAtNode = "REGISTER QUERY PersonAtNode AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/queries/csparql/_m/locationOfEachPerson_m.txt");

        String streamQueryEdgeExcludedForPerson = "REGISTER QUERY EdgeExcludedForPerson AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/queries/csparql/_m/edges_excluded_for_persons_m.txt");

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
        CsparqlQueryResultProxy edge= engine.registerQuery(streamQueryEdge, false);
        CsparqlQueryResultProxy node= engine.registerQuery(streamQueryNode, false);
        CsparqlQueryResultProxy personAtNode= engine.registerQuery(streamQueryPersonAtNode, false);
        CsparqlQueryResultProxy edgeExcludedForPerson= engine.registerQuery(streamQueryEdgeExcludedForPerson, false);

        //Adding an observer to the instance of the listener
        edge.addObserver(new Output("data/output/_m/1.txt"));
        node.addObserver(new Output("data/output/_m/2.txt"));
        personAtNode.addObserver(new Output("data/output/_m/3.txt"));
        edgeExcludedForPerson.addObserver(new Output("data/output/_m/4.txt"));

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
