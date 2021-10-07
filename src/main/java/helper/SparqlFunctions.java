package helper;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.InfModel;
import eu.larkc.csparql.common.utils.CsparqlUtils;

import java.util.ArrayList;
import java.util.List;

public class SparqlFunctions {

    public SparqlFunctions() {
    }

    public static List<String> getSPARQLQueryResult( InfModel infModel, String path ) throws Exception {
        String queryString = CsparqlUtils.fileToString(path);
        Query query = QueryFactory.create(queryString);
        QueryExecution qExec = QueryExecutionFactory.create(query, infModel);
        List<QuerySolution> resultRaw = ResultSetFormatter.toList(qExec.execSelect());
        List<String> result = new ArrayList<>();
        if (resultRaw.size() != 0) {
            for (QuerySolution a : resultRaw) {
                String str = a.toString();
//                String[] tokens = (objType.equals("literal")) ?  str.split("\\(|= <|> \\)|\\?val =| ") : str.split("\\(|= <|> \\)");
                String[] tokens = str.split("\\(|= <|> \\)|\\?val =| \\)");
                for (int i = 2; i < tokens.length; i += 3) {
                    result.add(tokens[i].trim());
                }
            }
        }
        return result;
    }

    public List<QuerySolution> getSelectQueryResult( InfModel infModel, String path ) throws Exception {
        String queryString = CsparqlUtils.fileToString(path);
        Query query = QueryFactory.create(queryString);
        QueryExecution qExec = QueryExecutionFactory.create(query, infModel);
        return ResultSetFormatter.toList(qExec.execSelect());
    }
}
