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
    private int timeStep = 1;
    public Output( String outputFileLocation ) {
        this.outputFileLocation = outputFileLocation;
    }

    public Output( ) {
    }

    public void update( Observable o, Object arg ) {

        StringBuilder sb = new StringBuilder();
        RDFTable q = (RDFTable) arg;
        Iterator i$ = q.iterator();
        sb.append("******"+timeStep+"******"+ "\n");

        while (i$.hasNext()) {
                sb.append(i$.next().toString() + "\n");
        }

        timeStep++;

        try {
            Files.write(Paths.get(outputFileLocation), sb.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }
}
