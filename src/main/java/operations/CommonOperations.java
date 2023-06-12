package operations;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Resource;
import entities.PersonController;
import model.Person;

public class CommonOperations {

    public static List<Resource> personTypes;

    public static List<Double> personsSafetyValues;

    public static List<Integer> personsQuantity;

    public static String createEdge(String origin, String destination){
        String[] strD = destination.split("#");
        return origin + "_" + strD[1];
    }

    public static void putPersonInPersonControllerMap(String person, String type, String personLocation, Map<String, PersonController> personControllerMap){
        int number = Integer.parseInt(person.split("Person")[1]);
        int index = findIndex(personsQuantity, number);
        Resource ExistingType = personTypes.get(index);
        if (type.equals(ExistingType.toString())){
            Person p = new Person(person, personLocation, type); // Person object is being created
            p.setAllowedSafetyValue(personsSafetyValues.get(index).floatValue());
            PersonController pc = new PersonController(p, personsSafetyValues.get(index).floatValue());
            personControllerMap.put(person, pc);
        }
    }

    public static int findIndex(List<Integer> personsQuantity, int x) {
        int sum = 0;
        for (int i = 0; i < personsQuantity.size(); i++) {
            sum += personsQuantity.get(i);
            if (sum >= x) {
                return i;
            }
        }
        return -1; // return -1 if x cannot be achieved by adding elements of the list
    }



}
