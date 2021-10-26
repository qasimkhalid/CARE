package helper;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import model.CareeInfModel;
import model.ODPair;
import model.PersonMovementTime;
import model.Space;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AutomatedOperations {



    public static List<Space> getSpaceInfo( InfModel infModel, List<ODPair> odPairList ){
        List<String> spaceInfoQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/GetSpaceInfo.txt");
        List<Space> list = new ArrayList<>();
        Space s;
        for(int i=0; i < spaceInfoQueryResult.size()-2; i+=3) {
            if(spaceInfoQueryResult.get(i).contains("_")) {
                s = new Space(spaceInfoQueryResult.get(i), spaceInfoQueryResult.get(i + 1), spaceInfoQueryResult.get(i + 2), "edge");

                String[] tokens = spaceInfoQueryResult.get(i).split("_");
                List<ODPair> odPairMatchedList = odPairList.stream()
                        .filter(x -> (x.getOrigin().equals(tokens[0]) && x.getDestination().equals("https://w3id.org/sbeo/example/officescenario#"+tokens[1])) || (x.getOrigin().equals("https://w3id.org/sbeo/example/officescenario#"+tokens[1]) && x.getDestination().equals(tokens[0])))
                        .collect(Collectors.toList());

                if (!odPairMatchedList.isEmpty() && odPairList.size() !=2) {
                    for (ODPair odPair:odPairMatchedList) {
                        odPair.setSpace(s);
                    }
                } else {
                    System.out.println("Origin and Destination not found in odPairList for injecting a common edge");
                }

                list.add(s);
            } else list.add(new Space(spaceInfoQueryResult.get(i), spaceInfoQueryResult.get(i+1), spaceInfoQueryResult.get(i+2), "node"));
        }
        return list;
    }

    public static List<ODPair> getCostOfAllODPairs( InfModel infModel){
        List<String> odPairQueryResult = null;
        try {
            odPairQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/FindO-DPairs.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//        try {
//            Files.write(Paths.get("data/output/O-DPairDistance.txt"), sb.toString().getBytes(), StandardOpenOption.WRITE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return list;
    }

    public static void setPeopleInBuilding( InfModel infModel, int personsCount, boolean allPersonMove, int personsWithWheelChair) throws Exception {
        long initialTime = System.currentTimeMillis();
        int personsWithWheelChairCounter = 0;
        Resource personInstance;
        Resource spaceInstance;

        List<String> availableSpaces = getAvailableNodes(infModel);

        for(int i=1 ; i <= personsCount; i++){
            personInstance = ResourceFactory.createResource( HelpingVariables.exPrefix+ "Person" +i);

            if(personsWithWheelChairCounter < personsWithWheelChair){
                infModel.add(personInstance, HelpingVariables.rdfType, HelpingVariables.NonMotorisedWheelchairPersonClass);
                personsWithWheelChairCounter++;
            } else {
                infModel.add(personInstance, HelpingVariables.rdfType, HelpingVariables.personClass);
            }
            infModel.addLiteral(personInstance, HelpingVariables.id, i);

            if(allPersonMove)
                infModel.add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateStanding);
            else if (MathOperations.getRandomBoolean())
                infModel.add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateResting);
//                infModel.add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateStanding);
            else infModel.add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateResting);
//                infModel.add(personInstance, HelpingVariables.activityStatus, HelpingVariables.activityStatusEvacuating);

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

    public static List<String> getAvailableNodes( InfModel infModel ) throws Exception {
        return SparqlFunctions.getSPARQLQueryResult(infModel, "data/queries/sparql/FindAllAvailableNodesInBuilding.txt");
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
