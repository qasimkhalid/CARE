package extra.stuff;

import java.util.List;

public class Node {
    String sbeoPrefix = "sbeo:";
    String exPrefix = "ex:";

    String name;
    String type;
    String area;
    int accommodationCapacity;
    int id;

    public Node(String name, String type, int accommodationCapacity, float area, int id) {
        this.name = exPrefix + name;
        this.type = type;
        this.area = "\"" + area + ".0\"^^xsd:float";
        this.accommodationCapacity = accommodationCapacity;
        this.id = id;
    }

    @Override
    public String toString() {
        return name + " rdf:type owl:NamedIndividual ," +
               type + " ;" +
               "sbeo:accommodationCapacity " + accommodationCapacity + " ;" +
               "sbeo:area " + area + " ;" +
               "sbeo:id " + id + ". " +

                name+"_HumanDetection_Sensor rdf:type owl:NamedIndividual , " +
                "sosa:Sensor ;" +
                " sosa:observes ex:HumanDetection ;" +
                " sbeo:installedIn " + name + " . " +

                name+"_Smoke_Sensor rdf:type owl:NamedIndividual , " +
                "sosa:Sensor ;" +
                " sosa:observes ex:Smoke ;" +
                " sbeo:installedIn " + name + " . " +

                name+"_Temperature_Sensor rdf:type owl:NamedIndividual , " +
                "sosa:Sensor ;" +
                " sosa:observes ex:Temperature ;" +
                " sbeo:installedIn " + name + " . " +

                name+"_SpaceAccessibility_Sensor rdf:type owl:NamedIndividual , " +
                "sosa:Sensor ;" +
                " sosa:observes ex:SpaceAccessibility ;" +
                " sbeo:installedIn " + name + " . " +

                name+"_Humidity_Sensor rdf:type owl:NamedIndividual , " +
                "sosa:Sensor ;" +
                " sosa:observes ex:Humidity ;" +
                " sbeo:installedIn " + name + " . ";
    }
}
