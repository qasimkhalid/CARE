package model;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;

import operations.Sparql;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CareeInfModel {
    private final static String ontologyStatic = "https://raw.githubusercontent.com/qasimkhalid/SBEO/master/sbeo.owl";
    private final static String baseIRI = "https://w3id.org/sbeo";
    private final static String kbIRI = "https://w3id.org/sbeo/example/officescenario";
    private static InfModel _infModel;
    private static CareeInfModel _instance;
    private final List<String> _emptyList = new ArrayList<>();

    private CareeInfModel(InfModel infModel) {
        _infModel = infModel;
    }

    public static CareeInfModel Instance() {
        if (_instance == null) {
            //read the latest version of schema from the git repository.
            OntModel baseModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
            baseModel.read(ontologyStatic, baseIRI, "TURTLE");

            //read the data (includes the information about the building, such as distance, location of sensors) file based on the schema as an input .
//        InputStream in = new FileInputStream("G:\\.shortcut-targets-by-id\\1DQfFtktu-cWZCdp7V2zH4sCOhw49RekS\\Qasim-Shared\\sbeo_paper_evaluation_example_modeling\\data\\kb\\initial_scenario.owl");
            InputStream in = null;
            try {
//                in = new FileInputStream("data/kb/initial_scenario.owl");
                in = new FileInputStream("data/kb/2nd_scenario_shopping_mall.owl");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //binding the schema with data using a light reasoner(just transitive and symmetric inferences).
            OntModel kbModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
            kbModel.read(in, kbIRI, "TURTLE");
            Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();
            reasoner = reasoner.bindSchema(baseModel);

            InfModel infModel = ModelFactory.createInfModel(reasoner, kbModel);
            _instance = new CareeInfModel(infModel);
        }

        return _instance;
    }

    public synchronized void remove(Statement statement) {
        _infModel.remove(statement);
    }

    public synchronized void remove(Resource resource1, Property property, Resource resource2) {
        _infModel.remove(resource1, property, resource2);
    }

    public synchronized void add(Resource resource1, Property property, Resource resource2) {
        _infModel.add(resource1, property, resource2);
    }

    public synchronized void addStatement(Statement s) {
        _infModel.add(s);
    }

    public synchronized void addLiteral(Resource resource1, Property property, double literal) {
        _infModel.addLiteral(resource1, property, literal);
    }

    public synchronized List<String> getQueryResult(String path) {
        try {
            return Sparql.getSPARQLQueryResult(_infModel, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _emptyList;
    }

    public synchronized Resource getResource(String key) {
//        return _infModel.getResource(key);
        return _infModel.getResource(key);
    }

    public synchronized Statement getRequiredProperty(Resource resource, Property property) {
        return _infModel.getRequiredProperty(resource, property);
    }

    public synchronized Property getProperty(String key) {
        return _infModel.getProperty(key);
    }

    public synchronized RDFNode getRDFNode(Node n) {
        return _infModel.getRDFNode(n);
    }

    public NodeIterator listObjectsOfProperty(Resource s, Property p){
        return _infModel.listObjectsOfProperty(s, p);
    }

    public synchronized boolean contains(Resource resource, Property property) {
        return _infModel.contains(resource, property);
    }

    public synchronized boolean contains(Resource resource, Property property, String object) {
        return _infModel.contains(resource, property, object);
    }

    public synchronized boolean contains(Resource resource, Property property, RDFNode object) {
        return _infModel.contains(resource, property, object);
    }

    public synchronized boolean contains(Statement s) {
        return _infModel.contains(s);
    }

    public synchronized Statement createStatement(Resource r, Property p, RDFNode o) {
        return _infModel.createStatement(r, p, o);
    }


    public synchronized InfModel getInfModel() {
        return _infModel;
    }


    public synchronized void write(OutputStream out) {
        RDFDataMgr.write(out, _infModel, RDFFormat.TURTLE_PRETTY);
    }
}