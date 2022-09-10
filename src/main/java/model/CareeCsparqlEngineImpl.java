package model;

import eu.larkc.csparql.cep.api.RdfStream;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;

import java.text.ParseException;

public class CareeCsparqlEngineImpl {
    private static CsparqlEngineImpl _cSparqlEngineImpl;
    private static CareeCsparqlEngineImpl _instance;

    private CareeCsparqlEngineImpl(CsparqlEngineImpl csparqlEngineImpl) {
        _cSparqlEngineImpl = csparqlEngineImpl;
        _cSparqlEngineImpl.initialize(true);
    }

    public static CareeCsparqlEngineImpl Instance() {
        if (_instance == null) {
            CsparqlEngineImpl engine = new CsparqlEngineImpl();
            _instance = new CareeCsparqlEngineImpl(engine);
        }
        return _instance;
    }

    public void putStaticNamedModel (String prefix, String result){
        _cSparqlEngineImpl.putStaticNamedModel(prefix, result);

    }

    public void registerStream (RdfStream stream){
        _cSparqlEngineImpl.registerStream(stream);
    }

    public CsparqlQueryResultProxy registerQuery (String query, boolean activateInference) throws ParseException {
        return _cSparqlEngineImpl.registerQuery(query, activateInference);
    }

    public void unregisterStream (String stream){
        _cSparqlEngineImpl.unregisterStream(stream);
    }

    public void destroy(){
        _cSparqlEngineImpl.destroy();
    }

}