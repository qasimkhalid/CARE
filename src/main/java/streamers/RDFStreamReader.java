package streamers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.larkc.csparql.cep.api.RdfQuadruple;
import eu.larkc.csparql.cep.api.RdfStream;
import java.io.*;
import java.nio.charset.StandardCharsets;


public class RDFStreamReader extends RdfStream implements Runnable {






	/**
	 * The logger.
	 */
	protected final Logger logger = LoggerFactory
			.getLogger(RDFStreamReader.class);

	private boolean keepRunning = false;
	private final String iri;
	private String DataStreamFile;
	private int pre_sleep = 1000;



	public RDFStreamReader(final String Streamiri, String DataStreamFile) {
		super(Streamiri);
		this.iri = Streamiri;
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


	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized (DataStreamFile) {

			// TODO Pause for Starting the Query
			try {
				System.out.println("Getting a data from Stream");
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
			int observer_sleep = 3000;


			try {
				LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(DataStreamFile), StandardCharsets.UTF_8));
				while ((strLine = reader.readLine()) != null && (!keepRunning)) {
					String[] data = strLine.split(" ");
					time_check = data[0];
					if ((!time_check.equals(time_check_old)) && (reader.getLineNumber() > 1)) {
						try {
							System.out.print("Observer Sleeps for " + observer_sleep + "\n");
							Thread.sleep(observer_sleep);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						event_counter += 1;
					}else if((!data[1].equals(sensor_check_old)) && (reader.getLineNumber() > 1)){
						event_counter += 1;
					}
					event_sequence = "http://www.ia.urjc.es/ontologies/building/sbevac/events/event" + event_counter;

					if(data[1].contains("iBeacon")) {
						RdfQuadruple q = new RdfQuadruple((event_sequence), "http://www.ia.urjc.es/ontologies/building/sbevac/events/PersonID", "\"" + data[2] + "\"^^http://www.w3.org/2001/XMLSchema#integer", HumanTimeToMilliSeconds(data[0]));
//                    final RdfQuadruple q1 = new RdfQuadruple((event_sequence), "http://www.ia.urjc.es/ontologies/building/sbevac/events/PersonID","\""+ data[2]+"\"", HumanTimeToMilliSeconds(data[0]));
//						System.out.println("Person Line of Data has been Feeded " + q.toString());
						this.put(q);

						q = new RdfQuadruple((event_sequence), "http://www.ia.urjc.es/ontologies/building/sbevac/events/SensorID", "http://www.ia.urjc.es/ontologies/building/sbevac#" + data[1], HumanTimeToMilliSeconds(data[0]));
//						System.out.println("Sensor Line of Data has been Feeded " + q.toString());
						this.put(q);
					}else if(data[1].contains("Temp")){
						RdfQuadruple q = new RdfQuadruple(event_sequence, "http://www.ia.urjc.es/ontologies/building/sbevac#" + data[1], "\"" + data[2] + "\"^^http://www.w3.org/2001/XMLSchema#integer", HumanTimeToMilliSeconds(data[0]));
//                    final RdfQuadruple q1 = new RdfQuadruple((event_sequence), "http://www.ia.urjc.es/ontologies/building/sbevac/events/PersonID","\""+ data[2]+"\"", HumanTimeToMilliSeconds(data[0]));
//						System.out.println("Temp Line of Data has been Feeded " + q.toString());
						this.put(q);
					}else{
						RdfQuadruple q = new RdfQuadruple(event_sequence, "http://www.ia.urjc.es/ontologies/building/sbevac#"+ data[1], "\"" + data[2] + "\"^^http://www.w3.org/2001/XMLSchema#boolean", HumanTimeToMilliSeconds(data[0]));
//                    final RdfQuadruple q1 = new RdfQuadruple((event_sequence), "http://www.ia.urjc.es/ontologies/building/sbevac/events/PersonID","\""+ data[2]+"\"", HumanTimeToMilliSeconds(data[0]));
//						System.out.println("Smoke Line of Data has been Feeded " + q.toString());
						this.put(q);
					}
					time_check_old = time_check;
					sensor_check_old = data[1];
				}
			}
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


}
