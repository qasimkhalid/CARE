package helper;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import model.CareeInfModel;
import model.graph.ODPair;
import model.Space;
import operations.AutomatedOperations;

import java.util.List;

public class HelpingVariables {

    public final static String prefixes = "PREFIX f: <http://larkc.eu/csparql/sparql/jena/ext#> "
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

    public final static String foafPrefix = "http://xmlns.com/foaf/0.1/";
    public final static String rdfPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public final static String sbeoPrefix = "https://w3id.org/sbeo#";
    public final static String sosaPrefix = "http://www.w3.org/ns/sosa/";
    public final static String exPrefix = "https://w3id.org/sbeo/example/officescenario#";
    public final static String oloPrefix = "http://purl.org/ontology/olo/core#";

    public final static String baseIRI = "https://w3id.org/sbeo";
    public final static String kbIRI = "https://w3id.org/sbeo/example/officescenario";

    public final static Resource motionStateStanding = ResourceFactory.createResource(exPrefix + "Standing");
    public final static Resource motionStateResting = ResourceFactory.createResource(exPrefix + "Resting");
    public final static Resource motionStateWalking = ResourceFactory.createResource(exPrefix + "Walking");
    public final static Resource activityStatusEvacuating = ResourceFactory.createResource(exPrefix + "Evacuating");
    public final static Resource unavailableInstance = CareeInfModel.Instance().getResource(sbeoPrefix + "UnAvailable");
    public final static Resource AvailableInstance = CareeInfModel.Instance().getResource(sbeoPrefix + "Available");

    public final static Resource personClass = CareeInfModel.Instance().getInfModel().getResource(HelpingVariables.foafPrefix + "Person");
    public final static Resource NonMotorisedWheelchairPersonClass = CareeInfModel.Instance().getInfModel().getResource(HelpingVariables.sbeoPrefix + "NonMotorisedWheelchairPerson");
    public final static Resource MotorisedWheelchairPersonClass = CareeInfModel.Instance().getInfModel().getResource(HelpingVariables.sbeoPrefix + "MotorisedWheelchairPerson");
    public final static Resource HearingImpairedPersonClass = CareeInfModel.Instance().getInfModel().getResource(HelpingVariables.sbeoPrefix + "HearingImpairedPerson");
    public final static Resource ExitRouteClass = CareeInfModel.Instance().getInfModel().getResource(HelpingVariables.sbeoPrefix + "ExitRoute");
    public final static Resource SlotClass = CareeInfModel.Instance().getInfModel().getResource(HelpingVariables.oloPrefix + "Slot");

    public final static Property atTime = CareeInfModel.Instance().getProperty(sbeoPrefix + "atTime");
    public final static Property locatedIn= CareeInfModel.Instance().getProperty(sbeoPrefix + "locatedIn");
    public final static Property motionState= CareeInfModel.Instance().getProperty(sbeoPrefix + "hasMotionState");
    public final static Property activityStatus = CareeInfModel.Instance().getInfModel().getProperty(sbeoPrefix + "hasActivityStatus");
    public final static Property safetyValue = CareeInfModel.Instance().getInfModel().getProperty(sbeoPrefix + "hasSafetyValue");
    public final static Property excludedFor = CareeInfModel.Instance().getInfModel().getProperty(sbeoPrefix + "excludedFor");
    public final static Property hasAvailabilityStatus = CareeInfModel.Instance().getProperty(sbeoPrefix + "hasAvailabilityStatus");
    public final static Property id = CareeInfModel.Instance().getInfModel().getProperty(sbeoPrefix + "id");
    public final static Property slot = CareeInfModel.Instance().getInfModel().getProperty(oloPrefix + "slot");
    public final static Property item = CareeInfModel.Instance().getInfModel().getProperty(oloPrefix + "item");
    public final static Property index = CareeInfModel.Instance().getInfModel().getProperty(oloPrefix + "index");
    public final static Property lengthOlo = CareeInfModel.Instance().getInfModel().getProperty(oloPrefix + "length");
    public final static Property assignedRoute = CareeInfModel.Instance().getInfModel().getProperty(sbeoPrefix + "assignedRoute");
    public final static Property rdfType = CareeInfModel.Instance().getInfModel().getProperty(HelpingVariables.rdfPrefix + "type");

    public static List<ODPair> odPairList = AutomatedOperations.getCostOfAllODPairs(CareeInfModel.Instance().getInfModel());
    public static List<Space> spaceInfoList =AutomatedOperations.getSpaceInfo(CareeInfModel.Instance().getInfModel(), odPairList);

}
