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

    public Output( String outputFileLocation ) {
        this.outputFileLocation = outputFileLocation;
    }

    public Output( ) {
    }

    public void update( Observable o, Object arg ) {

        StringBuilder sb = new StringBuilder();
        RDFTable q = (RDFTable) arg;
        Iterator i$ = q.iterator();
        while (i$.hasNext()) {
                sb.append(i$.next().toString() + "\n");
        }
        try {
            Files.write(Paths.get(outputFileLocation), sb.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}
