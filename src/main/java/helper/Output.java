package helper;

import eu.larkc.csparql.core.engine.ConsoleFormatter;

import eu.larkc.csparql.common.RDFTable;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.io.*;


public class Output extends ConsoleFormatter {

    private String outputFileLocation;
    private int timeStep;
    private String type;
    private boolean fireCheck = false;

    public Output( String outputFileLocation, String type ) {
        this.outputFileLocation = outputFileLocation;
        this.type = type;
    }
    public Output( String outputFileLocation ) {
        this.outputFileLocation = outputFileLocation;
    }

    public Output( ) {
    }

    public void update( Observable o, Object arg ) {

        StringBuilder sb = new StringBuilder();
        RDFTable q = (RDFTable) arg;
        Iterator i$ = q.iterator();

        sb.append("\n")
                .append("\n")
                .append("****** ")
                .append(timeStep)
                .append(" ******")
                .append("\n");

        switch(type){
            case "streamQueryEdge":
                while (i$.hasNext()) {
                    String[] tokens = i$.next().toString().split("#|\"");
                    if(!fireCheck && Float.parseFloat(tokens[7].trim()) < 0.5f){
                        sb.append("******Caution: Fire Event in this time Step******")
                                .append("\n");
                        fireCheck = true;
                    }

                    sb.append("(")
//                            .append(tokens[1].trim()).append(", ")
                            .append(tokens[2].trim()).append(")\t")
                            .append(tokens[4].trim()).append("\t")
                            .append(tokens[7].trim()).append("\t")
                            .append(tokens[10].trim())
                            .append("\n");
                }
                break;
            case "streamQueryNode":
                while (i$.hasNext()) {
                    String[] tokens = i$.next().toString().split("#|\"");

                    if(!fireCheck && Float.parseFloat(tokens[2].trim()) < 0.5f){
                        sb.append("******Caution: Fire Event in this time Step******")
                                .append("\n");
                        fireCheck = true;
                    }

                    sb.append(tokens[11].trim()).append("\t")
                            .append(tokens[2].trim()).append("\t")
                            .append(tokens[5].trim()).append("\t")
                            .append(tokens[8].trim())
                            .append("\n");
                }
                break;
            case "streamQueryPersonAtNode":
                while (i$.hasNext()) {
                    String[] tokens = i$.next().toString().split("#|\"|\t");
                    sb.append(tokens[1].trim()).append("\t")
                            .append(tokens[5].trim())
                            .append("\n");
                }
                break;
            case "streamQueryEdgeExcludedForPerson":
                Map<String, List<String>> map = new HashMap<>();
                String edge = "";
                while (i$.hasNext()) {
                    String[] tokens = i$.next().toString().split("#|\"|\t");

                    if(tokens.length > 6){
                        edge = "(" + tokens[9]+ ")";
                    }

                    if(map.containsKey(tokens[3])){
                        map.get(tokens[3]).add(edge);
                    } else {
                        map.put(tokens[3], new ArrayList<>());
                        map.get(tokens[3]).add(edge);
                    }
                    edge = "";
                }
                map.entrySet().forEach(entry -> sb.append(entry.getKey())
                        .append(" ")
                        .append(entry.getValue())
                        .append("\n"));

            break;
            case "streamQueryEdgePlusExcludedForPerson":
                Map<String, List<String>> excludedPersonFromEdgeMap = new HashMap<>();
                List<String> list;
                while (i$.hasNext()) {
                    String[] tokens = i$.next().toString().split("#|\"|\t");


                    if(!excludedPersonFromEdgeMap.containsKey(tokens[3])){
                        list = new ArrayList<>();
                        list.add(tokens[6]);
                        list.add(tokens[10]);
                        list.add(tokens[14]);
                        excludedPersonFromEdgeMap.put(tokens[3], list);
                    }

                    if(tokens.length > 17){
                        excludedPersonFromEdgeMap.get(tokens[3]).add(tokens[18]);
                    }

                }

                for (Map.Entry<String, List<String>> entry : excludedPersonFromEdgeMap.entrySet()) {
                    sb.append("(")
                            .append(entry.getKey())
                            .append(")")
                            .append("\t")
                            .append(entry.getValue().get(0))
                            .append("\t")
                            .append(entry.getValue().get(1))
                            .append("\t")
                            .append(entry.getValue().get(2))
                            .append("\t")
                            .append("[ ");
                    if(entry.getValue().size() > 3) {

                        Set<String> personSet = new HashSet<>(entry.getValue().subList(3, entry.getValue().size()));

                        for (String p : personSet) {
                            sb.append(p)
                                    .append(" ");
                        }
                    }
                    sb.append("]")
                            .append("\n");
                }
            break;

            default: System.out.println("Output type not found!");
        }

        timeStep++;
        try {
            Files.write(Paths.get(outputFileLocation), sb.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

    }
}
