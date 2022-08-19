package helper;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import model.*;
import model.scheduler.RestingScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AutomatedOperations {

    public static List<Space> getSpaceInfo(InfModel infModel, List<ODPair> odPairList) {
        List<String> spaceInfoQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel,
                "data/Queries/sparql/GetSpaceInfo.txt");
        List<Space> list = new ArrayList<>();
        Space s;
        for (int i = 0; i < spaceInfoQueryResult.size() - 2; i += 3) {
            if (spaceInfoQueryResult.get(i).contains("_")) {
                s = new Space(spaceInfoQueryResult.get(i), spaceInfoQueryResult.get(i + 1),
                        spaceInfoQueryResult.get(i + 2), "edge");

                String[] tokens = spaceInfoQueryResult.get(i).split("_");
                List<ODPair> odPairMatchedList = odPairList.stream()
                        .filter(x -> (x.getOrigin().equals(tokens[0]) && x.getDestination()
                                .equals("https://w3id.org/sbeo/example/officescenario#" + tokens[1]))
                                || (x.getOrigin().equals("https://w3id.org/sbeo/example/officescenario#" + tokens[1])
                                        && x.getDestination().equals(tokens[0])))
                        .collect(Collectors.toList());

                if (!odPairMatchedList.isEmpty() && odPairList.size() != 2) {
                    for (ODPair odPair : odPairMatchedList) {
                        odPair.setSpace(s);
                    }
                } else {
                    System.out.println("Origin and Destination not found in odPairList for injecting a common edge");
                }

                list.add(s);
            } else
                list.add(new Space(spaceInfoQueryResult.get(i), spaceInfoQueryResult.get(i + 1),
                        spaceInfoQueryResult.get(i + 2), "node"));
        }
        return list;
    }

    public static List<ODPair> getCostOfAllODPairs(InfModel infModel) {
        List<String> odPairQueryResult = null;
        try {
            odPairQueryResult = SparqlFunctions.getSPARQLQueryResult(infModel, "data/Queries/sparql/FindO-DPairs.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ODPair> list = new ArrayList<>();
        ODPair odp;
        for (int i = 0; i < odPairQueryResult.size() - 2; i += 3) {
            odp = new ODPair(odPairQueryResult.get(i), odPairQueryResult.get(i + 1), odPairQueryResult.get(i + 2));
            list.add(odp);
            odp = new ODPair(odPairQueryResult.get(i + 1), odPairQueryResult.get(i), odPairQueryResult.get(i + 2));
            list.add(odp);
        }
        return list;
    }

    public static void setPeopleInBuilding(InfModel infModel, int personsCount, boolean allPersonMove,
            int personsWithWheelChair) throws Exception {
        long initialTime = System.currentTimeMillis();
        int personsWithWheelChairCounter = 0;
        Resource personInstance;
        Resource spaceInstance;

        List<String> availableSpaces = getAvailableNodes(infModel);

        for (int i = 1; i <= personsCount; i++) {
            personInstance = ResourceFactory.createResource(HelpingVariables.exPrefix + "Person" + i);

            if (personsWithWheelChairCounter < personsWithWheelChair) {
                infModel.add(personInstance, HelpingVariables.rdfType,
                        HelpingVariables.NonMotorisedWheelchairPersonClass);
                personsWithWheelChairCounter++;
            } else {
                infModel.add(personInstance, HelpingVariables.rdfType, HelpingVariables.personClass);
            }
            infModel.addLiteral(personInstance, HelpingVariables.id, i);

            if (allPersonMove)
                infModel.add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateStanding);
            else if (MathOperations.getRandomBoolean())
                infModel.add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateStanding);
            else
                infModel.add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateResting);

            // Choosing a random space as a person location
            // String rs =
            // availableSpaces.get(MathOperations.getRandomNumber(availableSpaces.size()));
            String rs = "https://w3id.org/sbeo/example/officescenario#Office1";
            spaceInstance = ResourceFactory.createResource(rs);
            infModel.add(personInstance, HelpingVariables.locatedIn, spaceInstance);
            infModel.addLiteral(personInstance, HelpingVariables.atTime, initialTime);
        }
    }

    public static List<String> getAvailableSpaces(InfModel infModel) {
        return SparqlFunctions.getSPARQLQueryResult(infModel,
                "data/Queries/sparql/FindAllAvailableSpacesInBuilding.txt");
    }

    public static List<String> getAvailableNodes(InfModel infModel) {
        return SparqlFunctions.getSPARQLQueryResult(infModel,
                "data/queries/sparql/FindAllAvailableNodesInBuilding.txt");
    }

    public static void updateModelWhenPersonFinishesRoute(List<PersonMovementInformation> list) {
        Resource personInstance;
        for (PersonMovementInformation personMovementInformation : list) {
            personInstance = CareeInfModel.Instance().getResource(personMovementInformation.getPerson());
            Resource destinationInstance = CareeInfModel.Instance()
                    .getResource(personMovementInformation.getCurrentDestination());
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.motionState,
                    HelpingVariables.motionStateWalking);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.locatedIn, destinationInstance);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.motionState,
                    HelpingVariables.motionStateResting);
        }
    }

    public static void updateModelWhenPersonTraverseSingleEdge(String personName, String newLocation) {
        Resource personResource = CareeInfModel.Instance().getResource(personName);
        Resource locationResource = CareeInfModel.Instance().getResource(newLocation);
        CareeInfModel.Instance().add(personResource, HelpingVariables.locatedIn, locationResource);
    }

    /**
     * This method assumes the person has been updated in the model with its
     * newLocation
     *
     * @param person
     */
    public static void updateModelWhenPersonCompletesPath(String personName) {
        Resource personResource = CareeInfModel.Instance().getResource(personName);
        CareeInfModel.Instance().remove(personInstance, HelpingVariables.motionState,
                HelpingVariables.motionStateWalking);
        CareeInfModel.Instance().add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateResting);
    }

    public static void updateModelWhenPersonFinishesStepBasedMovement(List<PersonMovementInformation> list) {
        Resource personInstance;
        for (PersonMovementInformation personMovementInformation : list) {
            personInstance = CareeInfModel.Instance().getResource(personMovementInformation.getPerson());
            Resource destinationInstance = CareeInfModel.Instance()
                    .getResource(personMovementInformation.getCurrentDestination());
            CareeInfModel.Instance().add(personInstance, HelpingVariables.locatedIn, destinationInstance);
        }
    }

    public static void updateModelBeforePersonStartsToFollowRoute(List<PersonMovementInformation> list) {
        Resource personInstance;
        for (PersonMovementInformation t : list) {
            personInstance = CareeInfModel.Instance().getResource(t.getPerson());
            Resource originInstance = CareeInfModel.Instance().getResource(t.getCurrentOrigin());
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.motionState,
                    HelpingVariables.motionStateStanding);
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.locatedIn, originInstance);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.motionState,
                    HelpingVariables.motionStateWalking);
        }
    }

    public static void updateModelBeforePersonStartsStepBasedMovement(List<PersonMovementInformation> list) {
        Resource personInstance;
        for (PersonMovementInformation t : list) {
            personInstance = CareeInfModel.Instance().getResource(t.getPerson());
            Resource originInstance = CareeInfModel.Instance().getResource(t.getCurrentOrigin());
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.locatedIn, originInstance);
        }
    }

    public static void computeRestingPhase(long deltaTime, RestingScheduler personRestingScheduler) {
        personRestingScheduler.updatePersonResting(deltaTime, personRestingScheduler.getRestingPersons());
        List<String> personNeedToRestQueryResult = CareeInfModel.Instance()
                .getQueryResult("data/Queries/sparql/PersonWhoNeedToRest.txt");
        if (!personNeedToRestQueryResult.isEmpty()) {
            for (int i = 0; i < personNeedToRestQueryResult.size() - 1; i += 2) {
                personRestingScheduler.addRestingPerson(personNeedToRestQueryResult.get(i));
            }
        }
    }

    public static void ComputeAndAddExtraTime(Map<String, Integer> spaceOccupancyMap, PersonMovementInformation person,
            float areaPerPersonM2) throws Exception {
        float area;
        long extraTime;
        // find space area
        Optional<Space> space = HelpingVariables.spaceInfoList.stream()
                .filter(x -> x.getName().equals(person.getCurrentOrigin())).findFirst();
        if (space.isPresent()) {
            area = space.get().getArea();
        } else {
            throw new Exception("Origin of person not found in spaceInfoList");
        }

        // get the space occupancy status where the person is located
        Integer SpaceOccupancy = spaceOccupancyMap.get(person.getCurrentOrigin());
        extraTime = MathOperations.getExtraTime(area, SpaceOccupancy, areaPerPersonM2);
        if (extraTime > 0) {
            person.incrementTimeRequired(extraTime);
        }
    }

    public static long getODPairCostInSeconds(String origin, String destination) throws Exception {
        Optional<ODPair> odPair = HelpingVariables.odPairList.stream()
                .filter(x -> x.getOrigin().equals(origin) && x.getDestination().equals(destination)).findFirst();

        if (odPair.isPresent()) {
            return odPair.get().getCost() * 1000;
        } else {
            throw new Exception("Origin and Destination not found in odPairList");
        }
    }
}
