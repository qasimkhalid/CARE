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
    /**
     * This methods runs a Sparql query and filters out the nodes and edges, along
     * with their other characteristics,
     * such as area, and accommodation capacity.
     * 
     * @param infModel   - Inference Model
     * @param odPairList - List of ODPair objects (already loaded)
     * @return List of Space objects.
     */
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

    /**
     * This methods returns a list of OD Pairs (i.e., edges), along with their other
     * characteristics, such as cost,
     * corresponding space, both nodes (i.e., origin and destination) to which it is
     * connected.
     * 
     * @param infModel - Inference Model
     * @return list of ODPair objects
     */
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

    /**
     * This method sets up the persons in the building who might later be used as a
     * moving agents
     * 
     * @param infModel              - Inference Model
     * @param personsCount          - Total number of persons.
     * @param personsWithWheelChair - Number of persons having a mobility impairment
     */
    public static void setPeopleInBuilding(InfModel infModel, int personsCount, int personsWithWheelChair, int seed) {
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
            infModel.add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateResting);

            // Choosing a random space as a person location
            String rs = availableSpaces.get(MathOperations.getRandomNumber(availableSpaces.size(), seed));
            spaceInstance = ResourceFactory.createResource(rs);
            infModel.add(personInstance, HelpingVariables.locatedIn, spaceInstance);
            infModel.addLiteral(personInstance, HelpingVariables.atTime, initialTime);
        }
    }

    // /**
    // * This method sets up the persons in the building who might later be used as
    // a moving agents
    // * @param infModel - Inference Model
    // * @param personsCount - Total number of persons.
    // * @param allPersonMove - flag that expresses either all number of persons
    // will be moving or some of them will be
    // * chosen (randomly) to be in the resting state.
    // * @param personsWithWheelChair - Number of persons having a mobility
    // impairment
    // */
    // public static void setPeopleInBuilding(InfModel infModel, int personsCount,
    // boolean allPersonMove,
    // int personsWithWheelChair) {
    // long initialTime = System.currentTimeMillis();
    // int personsWithWheelChairCounter = 0;
    // Resource personInstance;
    // Resource spaceInstance;
    //
    // List<String> availableSpaces = getAvailableNodes(infModel);
    //
    // for (int i = 1; i <= personsCount; i++) {
    // personInstance = ResourceFactory.createResource(HelpingVariables.exPrefix +
    // "Person" + i);
    //
    // if (personsWithWheelChairCounter < personsWithWheelChair) {
    // infModel.add(personInstance, HelpingVariables.rdfType,
    // HelpingVariables.NonMotorisedWheelchairPersonClass);
    // personsWithWheelChairCounter++;
    // } else {
    // infModel.add(personInstance, HelpingVariables.rdfType,
    // HelpingVariables.personClass);
    // }
    // infModel.addLiteral(personInstance, HelpingVariables.id, i);
    //
    // if (allPersonMove)
    // infModel.add(personInstance, HelpingVariables.motionState,
    // HelpingVariables.motionStateStanding);
    // else if (MathOperations.getRandomBoolean())
    // infModel.add(personInstance, HelpingVariables.motionState,
    // HelpingVariables.motionStateStanding);
    // else
    // infModel.add(personInstance, HelpingVariables.motionState,
    // HelpingVariables.motionStateResting);
    //
    // // Choosing a random space as a person location
    // String rs =
    // availableSpaces.get(MathOperations.getRandomNumber(availableSpaces.size()));
    // spaceInstance = ResourceFactory.createResource(rs);
    // infModel.add(personInstance, HelpingVariables.locatedIn, spaceInstance);
    // infModel.addLiteral(personInstance, HelpingVariables.atTime, initialTime);
    // }
    // }

    /**
     * This method returns a list of available spaces in the building.
     * 
     * @param infModel - Inference Model
     * @return A list of strings having all available spaces. It needs to be
     *         processed before using it.
     */
    public static List<String> getAvailableSpaces(InfModel infModel) {
        return SparqlFunctions.getSPARQLQueryResult(infModel,
                "data/Queries/sparql/FindAllAvailableSpacesInBuilding.txt");
    }

    /**
     * This method returns a list of available nodes in the graph.
     * THIS METHOD IS AS SAME AS getAvailableSpaces method, but a bit better in
     * terms of results.
     * 
     * @param infModel - Inference Model
     * @return A list of strings having all available nodes (i.e., spaces). It needs
     *         to be processed before using it.
     */
    public static List<String> getAvailableNodes(InfModel infModel) {
        return SparqlFunctions.getSPARQLQueryResult(infModel,
                "data/queries/sparql/FindAllAvailableNodesInBuilding.txt");
    }

    public static void updateModelWhenPersonFinishesRoute(List<PersonMovementInformation> list) {
        Resource personInstance;
        for (PersonMovementInformation personMovementInformation : list) {
            personInstance = CareeInfModel.Instance().getResource(personMovementInformation.getPerson());
            Resource destinationInstance = CareeInfModel.Instance()
                    .getResource(personMovementInformation.getDestination());
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.motionState,
                    HelpingVariables.motionStateWalking);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.locatedIn, destinationInstance);
            CareeInfModel.Instance().add(personInstance, HelpingVariables.motionState,
                    HelpingVariables.motionStateResting);
        }
    }

    public static void updatePersonLocation(String personName, String newLocation) {
        Resource personResource = CareeInfModel.Instance().getResource(personName);
        Resource locationResource = CareeInfModel.Instance().getResource(newLocation);
        CareeInfModel.Instance().add(personResource, HelpingVariables.locatedIn, locationResource);
    }

    /**
     * This method assumes the person has been updated in the model with its
     * newLocation
     * 
     * @param personName
     */
    public static void updateModelWhenPersonCompletesPath(String personName) {
        Resource personResource = CareeInfModel.Instance().getResource(personName);
        CareeInfModel.Instance().remove(personResource, HelpingVariables.motionState,
                HelpingVariables.motionStateWalking);
        CareeInfModel.Instance().add(personResource, HelpingVariables.motionState, HelpingVariables.motionStateResting);
    }

    public static void updateModelWhenPersonFinishesStepBasedMovement(List<PersonMovementInformation> list) {
        Resource personInstance;
        for (PersonMovementInformation personMovementInformation : list) {
            personInstance = CareeInfModel.Instance().getResource(personMovementInformation.getPerson());
            Resource destinationInstance = CareeInfModel.Instance()
                    .getResource(personMovementInformation.getDestination());
            CareeInfModel.Instance().add(personInstance, HelpingVariables.locatedIn, destinationInstance);
        }
    }

    public static void updateModelBeforePersonStartsToFollowRoute(List<PersonMovementInformation> list) {
        Resource personInstance;
        for (PersonMovementInformation t : list) {
            personInstance = CareeInfModel.Instance().getResource(t.getPerson());
            Resource originInstance = CareeInfModel.Instance().getResource(t.getOrigin());
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
            Resource originInstance = CareeInfModel.Instance().getResource(t.getOrigin());
            CareeInfModel.Instance().remove(personInstance, HelpingVariables.locatedIn, originInstance);
        }
    }

    public static void computeRestingPhase(long deltaTime, RestingScheduler personRestingScheduler) {
        personRestingScheduler.updatePersonResting(deltaTime, personRestingScheduler.getRestingPersons());
        List<String> personNeedToRestQueryResult = CareeInfModel.Instance()
                .getQueryResult("data/Queries/sparql/PersonWhoNeedToRest.txt");
        if (!personNeedToRestQueryResult.isEmpty()) {
            for (int i = 0; i < personNeedToRestQueryResult.size() - 1; i += 2) {

                String personNeedToRest = personNeedToRestQueryResult.get(i);
                Optional<PersonMovementInformation> personAlreadyResting = personRestingScheduler.getRestingPersons()
                        .stream()
                        .filter(x -> x.getPerson().equals(personNeedToRest)).findFirst();

                if (!personAlreadyResting.isPresent()) {
                    // personRestingScheduler.addRestingPerson(personNeedToRest);
                }
            }
        }
    }

    public static void ComputeAndAddExtraTime(Map<String, Integer> spaceOccupancyMap, PersonMovementInformation person,
            float areaPerPersonM2) throws Exception {
        float area;
        long extraTime;
        // find space area
        Optional<Space> space = HelpingVariables.spaceInfoList.stream()
                .filter(x -> x.getName().equals(person.getOrigin())).findFirst();
        if (space.isPresent()) {
            area = space.get().getArea();
        } else {
            throw new Exception("Origin of person not found in spaceInfoList");
        }

        // get the space occupancy status where the person is located
        Integer SpaceOccupancy = spaceOccupancyMap.get(person.getOrigin());
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

    public static List<String> getExits() {
        return SparqlFunctions.getSPARQLQueryResult(infModel,
                "data/queries/sparql/GetExits.txt");
    }
}
