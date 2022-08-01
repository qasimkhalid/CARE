/*
CAREE: Context-AwaRe Emergency Evacuation Software

 Example: sbeo evaluation
*/

import eu.larkc.csparql.common.utils.CsparqlUtils;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import helper.AutomatedOperations;
import helper.HelpingVariables;
import helper.Output;
import model.CareeInfModel;
import streamers.HumanLocationStreamer;
import streamers.SpaceSensorsStreamer;

import java.io.StringWriter;


public class CAREE {

    private final static long initialTime = System.currentTimeMillis();

    public static void main( String[] args ) throws Exception {

        //Initialization C-SPARQL engine with timestamp function.
        CsparqlEngineImpl engine = new CsparqlEngineImpl();
        engine.initialize(true);

        AutomatedOperations.setPeopleInBuilding(CareeInfModel.Instance().getInfModel(), 10, false, 2);

        //C-SPARQL queries.

//        String streamQueryEdge = "REGISTER QUERY EachEdge AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/Queries/csparql/_m/edge_m.txt");

        String streamQueryNode = "REGISTER QUERY EachNode AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/Queries/csparql/_m/node_m.txt");

        String streamQueryPersonAtNode = "REGISTER QUERY PersonAtNode AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/queries/csparql/_m/locationOfEachPerson_m.txt");

//        String streamQueryEdgeExcludedForPerson = "REGISTER QUERY EdgeExcludedForPerson AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/queries/csparql/_m/edges_excluded_for_persons_m.txt");

        String streamQueryEdgePlusExcludedForPerson = "REGISTER QUERY EdgeExcludedForPerson AS " + HelpingVariables.prefixes + CsparqlUtils.fileToString("data/queries/csparql/_m/edge_plus_excluded_persons_m.txt");

        //Inserting a static data (schema + data + inferred) in the C-SPARQL as a base model.
        StringWriter staticModel = new StringWriter();
        CareeInfModel.Instance().getInfModel().write(staticModel, "Turtle");
        String result = staticModel.toString();
        engine.putStaticNamedModel(HelpingVariables.sbeoPrefix, result);

        //Creating the instances of data stream.
        HumanLocationStreamer hlStream = new HumanLocationStreamer(HelpingVariables.kbIRI, 2000, true, 1f);
        SpaceSensorsStreamer ssStream = new SpaceSensorsStreamer(HelpingVariables.kbIRI, 2000);

        //Injecting the stream in the C-SPARQL engine.
        engine.registerStream(hlStream);
        engine.registerStream(ssStream);

        //Binding the streams with new threads.
        Thread hlStreamThread = new Thread(hlStream);
        Thread ssStreamThread = new Thread(ssStream);

        //Creating the instances of the listener and registering the C-SPARQL query.
//        CsparqlQueryResultProxy edge= engine.registerQuery(streamQueryEdge, false);
//        CsparqlQueryResultProxy edgeExcludedForPerson= engine.registerQuery(streamQueryEdgeExcludedForPerson, false);

        CsparqlQueryResultProxy node= engine.registerQuery(streamQueryNode, false);
        CsparqlQueryResultProxy personAtNode= engine.registerQuery(streamQueryPersonAtNode, false);
        CsparqlQueryResultProxy edgePlusExcludedForPerson= engine.registerQuery(streamQueryEdgePlusExcludedForPerson, false);

        //Adding the observers to the instances of the listeners
//        edge.addObserver(new Output("data/output/_m/edges.txt", "streamQueryEdge"));
//        edgeExcludedForPerson.addObserver(new Output("data/output/_m/edges_not_apt_for_evacuation.txt", "streamQueryEdgeExcludedForPerson"));

        node.addObserver(new Output("data/output/_m/nodes.txt", "streamQueryNode"));
        personAtNode.addObserver(new Output("data/output/_m/location_of_each_person.txt", "streamQueryPersonAtNode"));
        edgePlusExcludedForPerson.addObserver(new Output("data/output/_m/edges_details_plus_excluded_persons.txt", "streamQueryEdgePlusExcludedForPerson"));

        //Starting all threads of streamers
        System.out.println("About to start the streaming threads...");
        hlStreamThread.start();
        ssStreamThread.start();


        System.out.println("Threads about to go to sleep for long time...");
        try {
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("About to stop the thread and unregistering the streams");
        hlStream.stop();
        ssStream.stop();
        engine.unregisterStream(hlStream.getIRI());
        engine.unregisterStream(ssStream.getIRI());

        System.out.println("About to exit");
        System.exit(0);

    }

}
