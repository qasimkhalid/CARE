package operations;

import com.hp.hpl.jena.rdf.model.*;
import entities.PersonController;
import helper.HelpingVariables;
import model.CareeInfModel;
import model.graph.ODPair;
import model.Space;


import java.util.*;
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
        List<String> spaceInfoQueryResult = Sparql.getSPARQLQueryResult(infModel,
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

                if (!odPairMatchedList.isEmpty()){
                    if(odPairList.size() != 2) {
                        for (ODPair odPair : odPairMatchedList) {
                            odPair.setSpace(s);
                        }
                    } else {
                        System.out.println("odPairList size is not 2, i.e., " + odPairList.size());
                    }
                } else {
                    System.out.println("odPairMatchedList is empty");
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
            odPairQueryResult = Sparql.getSPARQLQueryResult(infModel, "data/Queries/sparql/FindO-DPairs.txt");
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
     * @param personQuantity       - List of persons of each type
     * @param personsTypes - List of types of persons
     */
    public static void setPeopleInBuilding(InfModel infModel, List<Integer> personQuantity, List<Resource> personsTypes) {
        long initialTime = System.currentTimeMillis();
        Resource personInstance;
        Resource spaceInstance;

        List<String> availableSpaces = getAvailableNodes(infModel);
        int personCount = 1;
        for (int i = 0; i < personQuantity.size(); i++) {
            for (int j = 1; j <= personQuantity.get(i); j++) {
                //Debugging
                //  System.out.println("Person"+personCount+ " "+ personsTypes.get(i)+ "id="+ personCount);
                personInstance = ResourceFactory.createResource(HelpingVariables.exPrefix + "Person" + personCount);
                infModel.add(personInstance, HelpingVariables.rdfType, personsTypes.get(i));

                infModel.addLiteral(personInstance, HelpingVariables.id, personCount);
                infModel.add(personInstance, HelpingVariables.motionState, HelpingVariables.motionStateResting);

                // Choosing a random space as a person location
                String rs = availableSpaces.get(MathOperations.getRandomNumber(availableSpaces.size()));
                spaceInstance = ResourceFactory.createResource(rs);
                infModel.add(personInstance, HelpingVariables.locatedIn, spaceInstance);
                infModel.addLiteral(personInstance, HelpingVariables.atTime, initialTime);
                personCount++;
            }
        }
    }


    /**
     * This method returns a list of available spaces in the building.
     *
     * @param infModel - Inference Model
     * @return A list of strings having all available spaces. It needs to be
     * processed before using it.
     */
    public static List<String> getAvailableSpaces(InfModel infModel) {
        return Sparql.getSPARQLQueryResult(infModel,
                "data/Queries/sparql/FindAllAvailableSpacesInBuilding.txt");
    }

    /**
     * This method returns a list of available nodes in the model.graph.
     * THIS METHOD IS AS SAME AS getAvailableSpaces method, but a bit better in
     * terms of results.
     *
     * @param infModel - Inference Model
     * @return A list of strings having all available nodes (i.e., spaces). It needs
     * to be processed before using it.
     */
    public static List<String> getAvailableNodes(InfModel infModel) {
        return Sparql.getSPARQLQueryResult(infModel,
                "data/queries/sparql/FindAllAvailableNodesInBuilding.txt");
    }

//    public static void updateModelWhenPersonFinishesRoute(List<PersonMovementInformation> list) {
//        Resource personInstance;
//        for (PersonMovementInformation personMovementInformation : list) {
//            personInstance = CareeInfModel.Instance().getResource(personMovementInformation.getPerson());
//            Resource destinationInstance = CareeInfModel.Instance()
//                    .getResource(personMovementInformation.getDestination());
//            CareeInfModel.Instance().remove(personInstance, HelpingVariables.motionState,
//                    HelpingVariables.motionStateWalking);
//            CareeInfModel.Instance().add(personInstance, HelpingVariables.locatedIn, destinationInstance);
//            CareeInfModel.Instance().add(personInstance, HelpingVariables.motionState,
//                    HelpingVariables.motionStateResting);
//        }
//    }

//        public static void updateModelWhenARouteIsAssignedToPerson(String<List> route) {
//            String r = route.get(0) + "_" + route.get(route.size() - 1);
//            Resource RouteResource = CareeInfModel.Instance().getResource(route);
//            boolean x  = CareeInfModel.Instance().contains(RouteResource, null);
//            int y = 0;
//        }


    /**
     * This method updates/assigns the assigned route to the person in the model.
     * If the route already exists in the model, compare it with the existing one (size, slots with their order).
     * Otherwise, create a new route.
     * While creating a route, check for the existing slots in the model, so that the slot names won't be repeated.
     * Assign the newly created route to the person.
     * @param personName - URI of the person to whom the route will be assigned.
     * @param newRoute - list of spaces.
     */
    public static void updateModelWhenARouteIsAssignedToPerson(String personName, List<String> newRoute) {
        Resource routeResource = null;
        if (newRoute != null) {
            String r = newRoute.get(0) + "_" + "To_" + newRoute.get(newRoute.size() - 1).split("#")[1] + "_Route";
            routeResource = CareeInfModel.Instance().getResource(r);
            boolean routeAlreadyExistsInModel = CareeInfModel.Instance().contains(routeResource, null);
            boolean bothRoutesSame = false;

            if (routeAlreadyExistsInModel) {
                bothRoutesSame = areBothRoutesSame(routeResource, newRoute);
            }

            if (!routeAlreadyExistsInModel || !bothRoutesSame) {
                // Create a new route and put it in the model.
                CareeInfModel.Instance().add(routeResource, HelpingVariables.rdfType, HelpingVariables.ExitRouteClass);
                CareeInfModel.Instance().addLiteral(routeResource, HelpingVariables.lengthOlo, newRoute.size());
                for (int i = 0; i < newRoute.size(); i++) {
                    int counter = 1;
                    Resource slotToAdd = createSlot(HelpingVariables.exPrefix + "slot", counter, i);
                    CareeInfModel.Instance().add(routeResource, HelpingVariables.slot, slotToAdd);
                    CareeInfModel.Instance().addStatement(CareeInfModel.Instance().createStatement(slotToAdd, HelpingVariables.item, HelpingVariables.SlotClass));
                    CareeInfModel.Instance().addLiteral(slotToAdd, HelpingVariables.index, i);
                }
            }
        } 
        
        Resource personResource = CareeInfModel.Instance().getResource(personName);
        // Removing the assigned path of a person, if any.
        if (CareeInfModel.Instance().contains(personResource, HelpingVariables.assignedRoute)){
            Statement currentAssignedRoute = CareeInfModel.Instance().getRequiredProperty(personResource, HelpingVariables.assignedRoute);
            CareeInfModel.Instance().remove(currentAssignedRoute);
        }

        if (routeResource!= null) {
            // Assigning a new path to the person.
            CareeInfModel.Instance().add(personResource, HelpingVariables.assignedRoute, routeResource);
        }
    }

    /**
     * Create a new slot if a slot with similar URI already exists in the model
     * Otherwise, returns the same one (which is rare)
     * Associated with {@link #updateModelWhenARouteIsAssignedToPerson(String personName, List newRoute)}
     * @param slot slot URI
     * @param incrementer an int to add as a suffix to the slot if a slot with the exisiting URI already exists in the model.
     * @param index index of the slot in the route.
     * @return a slot as resource.
     */
        private static Resource createSlot(String slot, int incrementer, int index) {
            String currentSlot = slot;
            Resource slotResource = CareeInfModel.Instance().getResource(currentSlot);
            Statement slotStatement = CareeInfModel.Instance().createStatement(slotResource, HelpingVariables.rdfType, HelpingVariables.SlotClass);
            if (CareeInfModel.Instance().contains(slotStatement)) {
                Statement slotIndexTriple = CareeInfModel.Instance().getRequiredProperty(slotResource, HelpingVariables.index);
                Statement slotItemTriple = CareeInfModel.Instance().getRequiredProperty(slotResource, HelpingVariables.item);
                if (slotIndexTriple.getLiteral().getInt() != index && !slotItemTriple.getObject().toString().equals(currentSlot)) ;
                    currentSlot = currentSlot.split("_")[0] + "_" + incrementer;
                    incrementer++;
                    slotResource = createSlot(currentSlot, incrementer, index);
                }
            return slotResource;
        }

    /**
     * Compare (size, slots, and order of the slots of)  the exiting route in the model with the new route.
     * Associated with {@link #updateModelWhenARouteIsAssignedToPerson(String personName, List newRoute)}
     * @param routeResource Existing route in the model
     * @param newRoute list of spaces.
     * @return a boolean if they are same or not
     */
    private static boolean areBothRoutesSame(Resource routeResource, List<String> newRoute) {
        NodeIterator ni = CareeInfModel.Instance().listObjectsOfProperty(routeResource, HelpingVariables.slot);
        List<RDFNode> objects = new ArrayList<>();
        while (ni.hasNext()) {
            RDFNode node = ni.next();
            objects.add(node);
        }
        String[] existingRouteInModel = new String[objects.size()];
        for (RDFNode o : objects){
            NodeIterator itemIterator = CareeInfModel.Instance().listObjectsOfProperty(o.asResource(), HelpingVariables.item);
            NodeIterator indexIterator = CareeInfModel.Instance().listObjectsOfProperty(o.asResource(), HelpingVariables.index);
            while (itemIterator.hasNext()) {
                RDFNode item = itemIterator.next();
                RDFNode index = indexIterator.next();
                existingRouteInModel[index.asLiteral().getInt()] = item.toString();
            }
        }
        if (existingRouteInModel.length != newRoute.size()){
            return false;
        }

        return Arrays.asList(existingRouteInModel).equals(newRoute);
    }


    public static void updateModelWhenPersonTraversesPathSuccessfully(String personName, String personOldLocation, String personNewLocation) {
        Resource personResource = CareeInfModel.Instance().getResource(personName);
        Resource locationResource = CareeInfModel.Instance().getResource(personOldLocation);
        CareeInfModel.Instance().remove(personResource, HelpingVariables.locatedIn, locationResource);
        locationResource = CareeInfModel.Instance().getResource(personNewLocation);
        CareeInfModel.Instance().add(personResource, HelpingVariables.locatedIn, locationResource);
    }

    /**
     * This method assumes the person has been updated in the model with its
     * newLocation
     *
     * @param personName name of the person.
     */
    public static void updateModelWhenPersonCompletesPath(String personName) {
        Resource personResource = CareeInfModel.Instance().getResource(personName);
        CareeInfModel.Instance().remove(personResource, HelpingVariables.motionState,
                HelpingVariables.motionStateWalking);
        CareeInfModel.Instance().add(personResource, HelpingVariables.motionState, HelpingVariables.motionStateResting);
    }

    //Todo: It is supposed to be synchronized with the speed factor and timestep. Right now it is just in seconds.
    public static long getODPairCostInSeconds(String origin, String destination) throws Exception {
        Optional<ODPair> odPair = HelpingVariables.odPairList.stream()
                .filter(x -> x.getOrigin().equals(origin) && x.getDestination().equals(destination)).findFirst();

        if (odPair.isPresent()) {
            //update for some checks and for an application scenario
            return odPair.get().getCost() * 100;
            //Before it was seconds
//            return odPair.get().getCost() * 1000;
        } else {
            throw new Exception("Origin and Destination not found in odPairList");
        }
    }

    public static List<String> getExits() {
        return Sparql.getSPARQLQueryResult(CareeInfModel.Instance().getInfModel(), "data/queries/sparql/GetExits.txt");
    }

    public static void SetupPersonsMap(Map<String, PersonController> personControllerMap) {
        List<String> getAllPersonQueryResult = CareeInfModel.Instance()
                //.getQueryResult("data/queries/sparql/GetAllPersons.txt");
                .getQueryResult("data/queries/sparql/GetAllPersonsWithLocation.txt");
        for (int i = 0; i < getAllPersonQueryResult.size() - 2; i += 3) {
            String person = getAllPersonQueryResult.get(i);
            String type = getAllPersonQueryResult.get(i + 1);
            String personLocation = getAllPersonQueryResult.get(i + 2);
            CommonOperations.putPersonInPersonControllerMap(person, type, personLocation, personControllerMap);
        }
    }

    public static List<PersonController> GetAllPersonControllers() {
//        Map<String, PersonController> personsMap = new HashMap<>();
        Map<String, PersonController> personsMap = new LinkedHashMap<>();
        SetupPersonsMap(personsMap);
        Map<String, PersonController> personsMapFixed = sortPersonControllerMap(personsMap);
        return new ArrayList<>(personsMapFixed.values());
    }

    public static Map<String, PersonController> sortPersonControllerMap(Map<String, PersonController> unsortedMap) {
        // sorting the LinkedHashMap by considering the integer value after the string "Person"
        LinkedHashMap<String, PersonController> sortedMap = new LinkedHashMap<>();
        unsortedMap.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getKey().split("#")[1].substring(6))))
                .forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));
        return sortedMap;
    }

}


