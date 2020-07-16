package main.java.qasim.data;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.PrintUtil;

import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

import eu.larkc.csparql.common.utils.CsparqlUtils;
import eu.larkc.csparql.core.engine.ConsoleFormatter;

import eu.larkc.csparql.common.RDFTable;
import eu.larkc.csparql.common.RDFTuple;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Observable;
import java.io.*;


public class Output extends ConsoleFormatter {

    public OntModel base;
    public InfModel baseInferred;
    public String type;
    public String outputFileLocation;


    public Output() {
    }

    public Output(String type, String outputFileLocation) {
        this.type = type;
        this.outputFileLocation = outputFileLocation;
    }

    public Output(OntModel baseModel, String type) {
        this.base = baseModel;
        this.type = type;
    }

    public Output(OntModel baseModel, InfModel baseModelInferred, String type) {
        this.base = baseModel;
        this.type = type;
        this.baseInferred = baseModelInferred;
    }




    private String MillisecondsToHumanTime( int millis ) {

        int hour = ((millis / 1000) / 3600);
        int minute = (((millis / 1000) / 60) % 60);
        int second = ((millis / 1000) % 60);
        return String.format("%d:%02d:%02d", hour, minute, second);
    }

    private boolean delete_lines_from_file( String file_name, int startline, int numlines)
    {
        try
        {
            BufferedReader br=new BufferedReader(new FileReader(file_name));

            //String buffer to store contents of the file
            StringBuffer sb=new StringBuffer("");

            //Keep track of the line number
            int linenumber=1;
            String line;

            while((line=br.readLine())!=null)
            {
                //Store each valid line in the string buffer
                if(linenumber<startline||linenumber>=startline+numlines)
                    sb.append(line+"\n");
                linenumber++;
            }
            if(startline+numlines>linenumber)
                System.out.println("End of file reached.");
            br.close();

            FileWriter fw=new FileWriter(new File(file_name));
            //Write entire string buffer into the file
            fw.write(sb.toString());
            fw.flush();
            fw.close();
        }
        catch (Exception e)
        {
            System.out.println("Something went horribly wrong: "+e.getMessage());
        }
        return true;
    }

    public static InfModel Rules(InfModel model, String file) throws Exception {

        String rules = CsparqlUtils.fileToString(file);

        PrintUtil.registerPrefix("sbeo", "http://www.ia.urjc.es/ontologies/building/sbeo#");
        PrintUtil.registerPrefix("foaf", "http://xmlns.com/foaf/0.1/");
        PrintUtil.registerPrefix("seas", "https://w3id.org/seas/");
        PrintUtil.registerPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        PrintUtil.registerPrefix("", "http://www.ia.urjc.es/ontologies/building/sbeo_scenario#");

        Reasoner ruleReasoner = new GenericRuleReasoner(Rule.parseRules(rules));
        model = ModelFactory.createInfModel(ruleReasoner, model);
        return model;
    }


    public void update( Observable o, Object arg) {
        RDFTable q = (RDFTable) arg;
        System.out.println(q.size() + " results updated in " + type + " output file ");
        Iterator i$ = q.iterator();
        RDFTuple t;
        String data_separator = ", ";

        if (type !=null) {
            switch (type) {

                case "EachPersonLocationUpdate": {
                    String modifiedFileContent;
                    String[] eachTriple = new String[3];
                    String subject = null;
                    while (i$.hasNext()) {
                        t = (RDFTuple) i$.next();
                        String data = t.toString();
                        String[] data1 = data.split("\t");

                        subject = (data1[0].substring(56, data1[0].length()));
                        eachTriple[0] = subject;
                        if (data1[1].contains("atTime")) {
                            String time_milliseconds = (data1[2].substring(1, data1[2].length() - 43));
                            String time_human = MillisecondsToHumanTime(Integer.parseInt(time_milliseconds));
                            eachTriple[1] = time_human;
                        } else if (data1[1].contains("locatedIn")) {
                            //Location Information
                            String location = (data1[2].substring(56, data1[2].length()));
                            eachTriple[2] = location;
                        }

                        if ((eachTriple[1] != null) && (eachTriple[2] != null)) {

                            String output = eachTriple[0] + " is located at " + eachTriple[2] + " at time " + eachTriple[1];

                            BufferedWriter writer;

                            try {
//                                LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(outputFileLocation)));
//                                String currentReadingLine = reader.readLine();
//                                while (currentReadingLine != null) {
//
//                                    String[] data_reader = currentReadingLine.split(" ");
//                                    String old_subject = data_reader[0];
//                                    String old_time = data_reader[7];
//                                    String old_location = data_reader[4];
//
//                                    if (eachTriple[0].equals(old_subject) && (!eachTriple[1].equals(old_time) || !eachTriple[2].equals(old_location))) {
//                                        int line_number = reader.getLineNumber();
//                                        reader.close();
//                                        boolean line_delete = delete_lines_from_file(outputFileLocation, line_number, 1);
//                                        break;
//                                    }
//                                    currentReadingLine = reader.readLine();
//
//                                }

                                modifiedFileContent = output;
                                writer = new BufferedWriter(new FileWriter(outputFileLocation, true));
                                writer.write(modifiedFileContent + "\n");
                                writer.flush();
                                writer.close();
                                Arrays.fill(eachTriple, null);

                            } catch (IOException e) {
                            }
                        }
                    }
                    break;
                }

                case "FireCheck": {
                    String modifiedFileContent;
                    String[] eachTriple = new String[3];
                    String subject = null;
                    while (i$.hasNext()) {
                        t = (RDFTuple) i$.next();
                        String data = t.toString();
                        String[] data1 = data.split("\t");

                        subject = (data1[0].substring(56, data1[0].length()));
                        eachTriple[0] = subject;
                        if (data1[1].contains("atTime")) {
                            // Time Information
                            String time_milliseconds = (data1[2].substring(1, data1[2].length() - 43));
                            String time_human = MillisecondsToHumanTime(Integer.parseInt(time_milliseconds));
                            eachTriple[1] = time_human;
                        } else if (data1[1].contains("locatedIn")) {
                            //Location Information
                            String location = (data1[2].substring(56, data1[2].length()));
                            eachTriple[2] = location;
                        }

                        if ((eachTriple[1] != null) && (eachTriple[2] != null)) {

                            String output = eachTriple[0] + " exists at " + eachTriple[2] + " at time " + eachTriple[1];

                            BufferedWriter writer;
                            try {
//                                LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(outputFileLocation)));
//                                String currentReadingLine = reader.readLine();
//                                while (currentReadingLine != null) {
//
//                                    String[] data_reader = currentReadingLine.split(" ");
//                                    String old_subject = data_reader[0];
//                                    String old_time = data_reader[6];
//                                    String old_location = data_reader[4];
//
//                                    if (eachTriple[0].equals(old_subject) && (!eachTriple[1].equals(old_time) || !eachTriple[2].equals(old_location))) {
//                                        int line_number = reader.getLineNumber();
//                                        reader.close();
//                                        boolean line_delete = delete_lines_from_file(outputFileLocation, line_number, 1);
//                                        break;
//                                    }
//                                    currentReadingLine = reader.readLine();
//
//                                }

                                modifiedFileContent = output;
                                writer = new BufferedWriter(new FileWriter(outputFileLocation, true));
                                writer.write(modifiedFileContent + "\n");
                                writer.flush();
                                writer.close();
                                Arrays.fill(eachTriple, null);

                            } catch (IOException e) {
                            }
                        }
                    }
                    break;
                }

                case "VulnerablePeopleMovement": {
                    String modifiedFileContent;
                    String[] eachTriple = new String[4];
                    String subject = null;
                    while (i$.hasNext()) {
                        t = (RDFTuple) i$.next();
                        String data = t.toString();
                        String[] data1 = data.split("\t");

                        subject = (data1[0].substring(56));
                        eachTriple[0] = subject;
                        if (data1[1].contains("atTime")) {
                            // Time Information
                            String time_milliseconds = (data1[2].substring(1, data1[2].length() - 43));
                            String time_human = MillisecondsToHumanTime(Integer.parseInt(time_milliseconds));
                            eachTriple[1] = time_human;
                        } else if (data1[1].contains("movedTo")) {
                            //Location Information
                            String location = (data1[2].substring(56));
                            eachTriple[2] = location;
                        }  else if (data1[1].contains("movedFrom")) {
                            //Location Information
                            String location = (data1[2].substring(56));
                            eachTriple[3] = location;
                        }

                        if ((eachTriple[1] != null) && (eachTriple[2] != null) && (eachTriple[3] != null)) {


                            BufferedWriter writer;
                            try {
//                                LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(outputFileLocation)));
//                                String currentReadingLine = reader.readLine();
//                                while (currentReadingLine != null) {
//
//                                    String[] data_reader = currentReadingLine.split(" ");
//                                    String old_subject = data_reader[0];
//                                    String old_time = data_reader[7];
//                                    String old_location_check = data_reader[4];
//
//                                    if (eachTriple[0].equals(old_subject) && (!eachTriple[1].equals(old_time) || !eachTriple[2].equals(old_location_check))) {
//                                        int line_number = reader.getLineNumber();
//                                        reader.close();
//                                        boolean line_delete = delete_lines_from_file(outputFileLocation, line_number, 1);
//                                        break;
//                                    }
//                                    currentReadingLine = reader.readLine();
//
//                                }
                                String output = eachTriple[0] + " is moved to " + eachTriple[2] + " at time " + eachTriple[1] + " from " + eachTriple[3];

                                modifiedFileContent = output;
                                writer = new BufferedWriter(new FileWriter(outputFileLocation, true));
                                writer.write(modifiedFileContent + "\n");
                                writer.flush();
                                writer.close();
                                Arrays.fill(eachTriple, null);

                            } catch (IOException e) {
                            }
                        }
                    }
                    break;
                }

                case "NonVulnerablePeopleMovement": {
                    String modifiedFileContent;
                    String[] eachTriple = new String[4];
                    String subject = null;
                    while (i$.hasNext()) {
                        t = (RDFTuple) i$.next();
                        String data = t.toString();
                        String[] data1 = data.split("\t");

                        subject = (data1[0].substring(56));
                        eachTriple[0] = subject;
                        if (data1[1].contains("atTime")) {
                            // Time Information
                            String time_milliseconds = (data1[2].substring(1, data1[2].length() - 43));
                            String time_human = MillisecondsToHumanTime(Integer.parseInt(time_milliseconds));
                            eachTriple[1] = time_human;
                        } else if (data1[1].contains("movedTo")) {
                            //Location Information
                            String location = (data1[2].substring(56));
                            eachTriple[2] = location;
                        }  else if (data1[1].contains("movedFrom")) {
                            //Location Information
                            String location = (data1[2].substring(56));
                            eachTriple[3] = location;
                        }

                        if ((eachTriple[1] != null) && (eachTriple[2] != null) && (eachTriple[3] != null)) {


                            BufferedWriter writer;
                            try {
//                                LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(outputFileLocation)));
//                                String currentReadingLine = reader.readLine();
//                                while (currentReadingLine != null) {
//
//                                    String[] data_reader = currentReadingLine.split(" ");
//                                    String old_subject = data_reader[0];
//                                    String old_time = data_reader[7];
//                                    String old_location_check = data_reader[4];
//
//                                    if (eachTriple[0].equals(old_subject) && (!eachTriple[1].equals(old_time) || !eachTriple[2].equals(old_location_check))) {
//                                        int line_number = reader.getLineNumber();
//                                        reader.close();
//                                        boolean line_delete = delete_lines_from_file(outputFileLocation, line_number, 1);
//                                        break;
//                                    }
//                                    currentReadingLine = reader.readLine();
//
//                                }
                                String output = eachTriple[0] + " is moved to " + eachTriple[2] + " at time " + eachTriple[1] + " from " + eachTriple[3];

                                modifiedFileContent = output;
                                writer = new BufferedWriter(new FileWriter(outputFileLocation, true));
                                writer.write(modifiedFileContent + "\n");
                                writer.flush();
                                writer.close();
                                Arrays.fill(eachTriple, null);

                            } catch (IOException e) {
                            }
                        }
                    }
                    break;
                }

                case "EvacuationStatusCompleted": {
                    String modifiedFileContent;
                    String[] eachTriple = new String[3];
                    String subject = null;
                    while (i$.hasNext()) {
                        t = (RDFTuple) i$.next();
                        String data = t.toString();
                        String[] data1 = data.split("\t");

                        subject = (data1[0].substring(56, data1[0].length()));
                        eachTriple[0] = subject;
                        if (data1[1].contains("atTime")) {
                            String time_milliseconds = (data1[2].substring(1, data1[2].length() - 43));
                            String time_human = MillisecondsToHumanTime(Integer.parseInt(time_milliseconds));
                            eachTriple[1] = time_human;
                        } else if (data1[1].contains("evacuationStatus")) {
                            //Location Information
                            String status = (data1[2].substring(1, data1[2].length() - 42));
                            eachTriple[2] = status;
                        }

                        if ((eachTriple[1] != null) && (eachTriple[2] != null)) {


                            BufferedWriter writer;
                            boolean found=false;
                            try {
                                LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(outputFileLocation)));
                                String currentReadingLine = reader.readLine();
                                while (currentReadingLine != null) {
                                    String[] data_reader = currentReadingLine.split(" ");
                                    String old_subject = data_reader[0];
                                    if (eachTriple[0].equals(old_subject)){
                                        found = true;
                                        break;
                                    }
                                    currentReadingLine = reader.readLine();
                                }
                                if(!found) {
                                    String output = eachTriple[0] + " has " + eachTriple[2] + " the evacuation successfully at time " + eachTriple[1];
                                    modifiedFileContent = output;
                                    writer = new BufferedWriter(new FileWriter(outputFileLocation, true));
                                    writer.write(modifiedFileContent + "\n");
                                    writer.flush();
                                    writer.close();
                                    Arrays.fill(eachTriple, null);
                                }
                            } catch (IOException e) {
                            }
                        }
                    }
                    break;
                }

                default: {
                    while (i$.hasNext()) {
                        t = (RDFTuple) i$.next();
                        System.out.println(t.toString());
                    }
                }

            }
        }else {
            while (i$.hasNext()) {
                t = (RDFTuple) i$.next();
                System.out.println(t.toString());
            }
        }

    }
}





