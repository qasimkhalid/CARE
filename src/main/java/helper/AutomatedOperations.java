package helper;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import model.Location;
import model.Sensor;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AutomatedOperations {
    private final static long initialTime = System.currentTimeMillis();

    private final static String foafPrefix = "http://xmlns.com/foaf/0.1/";
    private final static String rdfPrefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final static String sbeoPrefix = "https://w3id.org/sbeo#";
    private final static String exPrefix = "https://w3id.org/sbeo/example/officescenario#";
    private final static String sosaPrefix = "http://www.w3.org/ns/sosa/";

    private static Resource personInstance;
    private static final Resource motionStateStanding = ResourceFactory.createResource(exPrefix + "Standing");
    private static final Resource motionStateWalking = ResourceFactory.createResource(exPrefix + "Walking");
    private static final Resource activityStatusEvacuating = ResourceFactory.createResource(exPrefix + "Evacuating");



    public static void setPeopleInBuilding( InfModel infModel, int peopleCount, boolean allPersonMove) throws Exception {
        int randomPersonsCount;


        if(allPersonMove){
            randomPersonsCount = peopleCount;
        } else {
            randomPersonsCount = MathOperations.getRandomNumberInRange(peopleCount,1);
        }

        Resource personClass = infModel.getResource(foafPrefix + "Person");

        Property rdfType = infModel.getProperty(rdfPrefix + "type");
        Property id = infModel.getProperty(sbeoPrefix + "id");
        Property motionState = infModel.getProperty(sbeoPrefix + "hasMotionState");
        Property locatedIn = infModel.getProperty(sbeoPrefix + "locatedIn");
        Property atTime = infModel.getProperty(sbeoPrefix + "atTime");
        Property activityStatus = infModel.getProperty(sbeoPrefix + "hasActivityStatus");

        List<String> availableSpaces = getAvailableSpaces(infModel);

        for(int i=1 ; i <= randomPersonsCount; i++){
            personInstance = ResourceFactory.createResource( exPrefix+ "Person" +i);
            infModel.add(personInstance, rdfType, personClass);
            infModel.add(personInstance, motionState, motionStateStanding);
            infModel.addLiteral(personInstance, id, i);

            if(allPersonMove){
                infModel.add(personInstance, activityStatus, activityStatusEvacuating);
            } else if (MathOperations.getRandomBoolean()) {
                infModel.add(personInstance, activityStatus, activityStatusEvacuating);
            }

            //Choosing a random space as a person location
            String rs = availableSpaces.get(MathOperations.getRandomNumber(availableSpaces.size()));
            Resource spaceInstance = ResourceFactory.createResource(rs);
            infModel.add(personInstance, locatedIn, spaceInstance);
            infModel.addLiteral(personInstance, atTime, initialTime);
        }
    }

    private static List<String> getAvailableSpaces( InfModel infModel ) throws Exception {
        return SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/FindAllAvailableSpacesInBuilding.txt");
    }
}
