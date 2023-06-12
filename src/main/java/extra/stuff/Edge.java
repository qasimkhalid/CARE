package extra.stuff;

import java.util.List;

public class Edge {
    String exPrefix = "ex:";

    String origin;
    String destination;
    String name;
    String type;
    String area;
    int cost;
    int accommodationCapacity;

    public Edge(String origin, String destination, String type, int accommodationCapacity, int area, int cost) {
        this.name = exPrefix + origin + "_" + destination;
        this.origin = origin;
        this.destination = destination;
        this.type = type;
        this.area = "\"" + area + ".0\"^^xsd:float";
        this.accommodationCapacity = accommodationCapacity;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return name + " rdf:type owl:NamedIndividual ," +"\n" +
                type + " ;" +"\n" +
                "sbeo:accommodationCapacity " + accommodationCapacity + " ;" +"\n" +
                "sbeo:area " + area + " ;" +"\n" +
                "sbeo:connectedTo " + exPrefix+origin + "," +"\n" +
                 exPrefix+destination + ". "  +"\n" + "\n" +

                name+"_HumanDetection_Sensor rdf:type owl:NamedIndividual , " +"\n" +
                "sosa:Sensor ;" +"\n" +
                " sosa:observes ex:HumanDetection ;" +"\n" +
                " sbeo:installedIn " + name + " . " +"\n" +"\n" +

                name+"_Smoke_Sensor rdf:type owl:NamedIndividual , " +"\n" +
                "sosa:Sensor ;" +"\n" +
                " sosa:observes ex:Smoke ;" +"\n" +
                " sbeo:installedIn " + name + " . " +"\n" +"\n" +

                name+"_Temperature_Sensor rdf:type owl:NamedIndividual , " +"\n" +
                "sosa:Sensor ;" +"\n" +
                " sosa:observes ex:Temperature ;" +"\n" +
                " sbeo:installedIn " + name + " . " +"\n" +"\n" +

                name+"_SpaceAccessibility_Sensor rdf:type owl:NamedIndividual , " +"\n" +
                "sosa:Sensor ;" +"\n" +
                " sosa:observes ex:SpaceAccessibility ;" +"\n" +
                " sbeo:installedIn " + name + " . " +"\n" +"\n" +

                name+"_Humidity_Sensor rdf:type owl:NamedIndividual , " +"\n" +
                "sosa:Sensor ;" +"\n" +
                " sosa:observes ex:Humidity ;" +"\n" +
                " sbeo:installedIn " + name + " . " +"\n" +"\n" +

                exPrefix + "Dist_"+ origin + "_" + destination + " rdf:type owl:NamedIndividual ," +"\n" +
                "sbeo:Distance ;" +"\n" +
                "sbeo:destination " + exPrefix+origin + ";" +"\n" +
                "sbeo:origin " + exPrefix+destination + ";" +"\n" +
                "sbeo:hasValue " + cost + "." + "\n" + "\n" ;

    }
}
