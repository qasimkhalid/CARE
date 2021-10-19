package helper;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import model.CareeInfModel;
import model.ODPair;
import model.PersonMovementTime;
import model.Space;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class AutomatedOperations {

    public static List<Space> getSpaceInfo( InfModel infModel ) throws Exception {
        List<String> spaceInfoQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/GetSpaceInfo.txt");
        List<Space> list = new ArrayList<>();
        Space s;
        for(int i=0; i < spaceInfoQueryResult.size()-1; i+=2) {
            s = new Space(spaceInfoQueryResult.get(i), spaceInfoQueryResult.get(i+1));
            list.add(s);
        }
        return list;
    }

    public static List<ODPair> getCostOfAllODPairs( InfModel infModel) throws Exception {
        List<String> odPairQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/FindO-DPairs.txt");
        List<ODPair> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        ODPair odp;
        for(int i=0; i < odPairQueryResult.size()-2; i+=3) {
            odp = new ODPair(odPairQueryResult.get(i), odPairQueryResult.get(i+1), odPairQueryResult.get(i+2));
            list.add(odp);
            sb.append(odPairQueryResult.get(i) + "\t" + odPairQueryResult.get(i+1) + "\t" + odPairQueryResult.get(i+2) + "\n");
            odp = new ODPair(odPairQueryResult.get(i+1), odPairQueryResult.get(i), odPairQueryResult.get(i+2));
            sb.append(odPairQueryResult.get(i+1) + "\t" + odPairQueryResult.get(i) + "\t" + odPairQueryResult.get(i+2) + "\n");
            list.add(odp);
        }
        Files.write(Paths.get("data/output/O-DPairDistance.txt"), sb.toString().getBytes(), StandardOpenOption.WRITE);
        return list;
    }

    public static void setPeopleInBuilding( InfModel infModel, int peopleCount, boolean allPersonMove) throws Exception {
        long initialTime = System.currentTimeMillis();
        int randomPersonsCount;
        Resource personInstance;
        Resource spaceInstance;

        if(allPersonMove){
            randomPersonsCount = peopleCount;
        } else {
            randomPersonsCount = MathOperations.getRandomNumberInRange(peopleCount,1);
        }

        List<String> availableSpaces = getAvailableSpaces(infModel);

        for(int i=1 ; i <= randomPersonsCount; i++){
            personInstance = ResourceFactory.createResource( HelpingVariables.exPrefix+ "Person" +i);
            infModel.add(personInstance, HelpingVariables.rdfType, HelpingVariables.personClass);
            infModel.add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateStanding);
            infModel.addLiteral(personInstance, HelpingVariables.id, i);

            if(allPersonMove){
                infModel.add(personInstance, HelpingVariables.activityStatus, HelpingVariables.activityStatusEvacuating);
            } else if (MathOperations.getRandomBoolean()) {
                infModel.add(personInstance, HelpingVariables.activityStatus, HelpingVariables.activityStatusEvacuating);
            }

            //Choosing a random space as a person location
            String rs = availableSpaces.get(MathOperations.getRandomNumber(availableSpaces.size()));
            spaceInstance = ResourceFactory.createResource(rs);
            infModel.add(personInstance, HelpingVariables.locatedIn, spaceInstance);
            infModel.addLiteral(personInstance, HelpingVariables.atTime, initialTime);
        }
    }

    public static List<String> getAvailableSpaces( InfModel infModel ) throws Exception {
        return SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/FindAllAvailableSpacesInBuilding.txt");
    }

    public static void updateModelWhenPersonFinishedMoving(List<PersonMovementTime> list) {
        Resource personInstance;
        for (PersonMovementTime personMovementTime : list) {
            personInstance = CareeInfModel.Instance().getResource(personMovementTime.getPerson());
            Resource destinationInstance = CareeInfModel.Instance().getResource(personMovementTime.getDestination());
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateWalking);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateStanding);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.locatedIn, destinationInstance);
        }
    }

    public static void updateModelBeforePersonMoves(List<PersonMovementTime> list) {
        Resource personInstance;
        for (PersonMovementTime t : list) {
            personInstance = CareeInfModel.Instance().getResource(t.getPerson());
            Resource originInstance = CareeInfModel.Instance().getResource(t.getOrigin());
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateStanding);
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.locatedIn, originInstance);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateWalking);
        }
    }
}
