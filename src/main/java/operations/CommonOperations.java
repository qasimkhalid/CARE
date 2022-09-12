package operations;

import java.util.ArrayList;
import java.util.List;

public class CommonOperations {

    public static String createEdge(String origin, String destination){
        String[] strD = destination.split("#");
        return origin + "_" + strD[1];
    }
}
