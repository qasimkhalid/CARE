package streamers;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.PrintUtil;
import eu.larkc.csparql.cep.api.RdfStream;

public class HumanLocationStream extends RdfStream implements Runnable {

    private int sleepTime;
    private String streamIRI;
    private boolean keepRunning = true;
    private InfModel modelLoaded;

    public HumanLocationStream(String iri,int sleepTime, InfModel model) {
        super(iri);
        this.streamIRI = iri;
        this.sleepTime = sleepTime;
        this.modelLoaded = model;
    }

    public void pleaseStop() {
        keepRunning = false;
    }

    @Override
    public void run() {















    }
}
