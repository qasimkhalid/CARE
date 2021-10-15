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
    private StringBuilder sb;
    public Output( String outputFileLocation ) {
        this.outputFileLocation = outputFileLocation;
    }
    public Output(  ) {
    }

    private String MillisecondsToHumanTime( int millis ) {

        int hour = ((millis / 1000) / 3600);
        int minute = (((millis / 1000) / 60) % 60);
        int second = ((millis / 1000) % 60);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public void update( Observable o, Object arg ) {

        sb = new StringBuilder();
        RDFTable q = (RDFTable) arg;
        Iterator i$ = q.iterator();
        while (i$.hasNext()) {
//            String[] splitResult = i$.next().toString().split("#|\"|\t");
//            if(!splitResult[0].isEmpty()) {
//                String line = splitResult[0] + "\t"+ splitResult[2] + "\t"+ splitResult[4];
//                sb.append(line + "\n");
                sb.append(i$.next().toString() + "\n");
//            }
        }

        try {
            Files.write(Paths.get(outputFileLocation), sb.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}
