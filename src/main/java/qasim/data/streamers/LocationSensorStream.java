package main.java.qasim.data.streamers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import java.io.*;
import java.nio.charset.StandardCharsets;


public class LocationSensorStream extends RdfStream implements Runnable {
    private int sleepTime;
    private String DataStreamFile;
    private String iri_events;
    private int pre_sleep = 1000;
    private boolean keepRunning = false;

    public LocationSensorStream(String iri,int sleepTime, String DataStreamFile) {
        super(iri);
        this.iri_events = iri;
        this.sleepTime = sleepTime;
        this.DataStreamFile = DataStreamFile;
    }

    public void pleaseStop() {
        keepRunning = false;
    }

    private long HumanTimeToMilliSeconds(String time) {
        int time_millisecond;
        String[] time_filter =time.split(":");
        int hour=Integer.parseInt(time_filter[0]);
        int minute=Integer.parseInt(time_filter[1]);
        int second=Integer.parseInt(time_filter[2]);
        time_millisecond = (second + (60 * minute) + (3600 * hour)) * 1000;
        return time_millisecond;
    }
    public void run() {
        synchronized (DataStreamFile) {
        try {
//            System.out.println("Getting a data from Location Stream");
            Thread.sleep(pre_sleep);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String time_check_old = null;
        String time_check;
        String sensor_check_old = null;
        int event_counter = 1;
        String event_sequence;
        String strLine;
        int observer_sleep = sleepTime;
//        String event_iri = "http://www.ia.urjc.es/ontologies/building/sbevac/" + type + "Events/";


            try {
                LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(DataStreamFile), StandardCharsets.UTF_8));
                while ((strLine = reader.readLine()) != null && !keepRunning) {
                    String[] data = strLine.split(" ");
                    time_check = data[0];
                    if ((!time_check.equals(time_check_old)) && (reader.getLineNumber() > 1)) {
                        try {
//                            System.out.print("Location Observer Sleeps for " + observer_sleep + " at Time " + time_check_old + "\n");
                            Thread.sleep(observer_sleep);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        event_counter += 1;
                    } else if ((!data[1].equals(sensor_check_old)) && (reader.getLineNumber() > 1)) {
                        event_counter += 1;
                    }
                    event_sequence = iri_events + event_counter;
                    RdfQuadruple q = new RdfQuadruple((event_sequence), iri_events + "PersonID", "\"" + data[2] + "\"^^http://www.w3.org/2001/XMLSchema#integer", HumanTimeToMilliSeconds(data[0]));
//                    System.out.println("Person Line of Data has been Feeded " + q.toString());
                    this.put(q);

                    q = new RdfQuadruple((event_sequence), iri_events + "SensorID", "http://www.ia.urjc.es/ontologies/building/sbevac_scenario#" + data[1], HumanTimeToMilliSeconds(data[0]));
//                    System.out.println("Sensor Line of Data has been Feeded " + q.toString());
                    this.put(q);
                    time_check_old = time_check;
                    sensor_check_old = data[1];
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}
