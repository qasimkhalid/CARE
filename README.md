# Context-AwaRe Emergency Evacuation (CAREE)
Context-Aware Emergency Evacuation (CAREE) uses complex event processing and semantic stream reasoning technologies for analysing streams of data coming from sensors installed in a smart building, identifying emergency conditions (e.g. hazardous situations that can be dangerous for the safety of the occupants of the building) and proposing safe and efficient individual evacuation routes to the occupants of the building according to the context (status of the building and peoples characteristics). This system uses [Smart Building Evacuation Ontology (SBEO)](https://w3id.org/sbeo/ "SBEO ontology")  for knowledge representation. 

This document describes a simulated environment by means of a simple scenario of a building floor to test CAREE, as shown in Figure 1 . Each entity, such as space, floor exit, and fire extinguisher, is represented using SBEO. Also, specific attributes of spaces, such as accommodation capacity, connections with other spaces, and the distance between the connected spaces (e.g., cost of each Origin-Destination (O-D) pair), are expressed.

| ![buildingfloor.jpg](https://github.com/qasimkhalid/CAREE/blob/sbeoPaperSetup/Figures/FloorPlanOfficeSpace%20-With%20Legends.png?raw=true) | 
|:--:|
| *Figure 1: A building floor plan with an entrance (which may also be an exit), an emergency exit and some closed spaces* |

In addition, the building floor is further represented as a graph **G** = (**N**, **A**), as seen in Figure 2, with 17 nodes, where each node in **N** represents an entity shown in Figure 1, e.g., closed space, junction (A junction is an imaginary route element that connects multiple corridors or other route elements (i.e., nodes)), point of interest, or entrance or an emergency exit. On the other hand, **A** represents the arcs between the connected nodes. We also assume that each node, as seen in Figure 1 with a diamond symbol, is equipped with four types--Temperature, Smoke, Humidity, and Human Detection--of sensors and modelled using SBEO. In the end, we also modelled ten persons (including their demographics and physical characteristics)  using SBEO, in which two of them are mobility impaired.

| ![nodes.jpg](https://github.com/qasimkhalid/CAREE/blob/sbeoPaperSetup/Figures/scenario_figure_with_node_id.png?raw=true) | 
|:--:|
| *Figure 2: Network modelling from a smart building floor plan. Nodes are labelled as names and Ids, and Arcs between two connected nodes are expressed as lines.* |

------------
------------
## How to Run Run the Simulation:

1. Clone the project by selecting any of the options, e.g., Downloading zip, cloning it using git.  
2. Once, the project is cloned, please, go to the [**CAREE.java**](https://github.com/qasimkhalid/CAREE/blob/4e86d73b25a69e373851241bd61f73cfe7b0b5b5/src/main/java/CAREE.java) file that can be found in *src/main/java/* directory .
3. In **CAREE.java** file, on line 30 (i.e., *setPeopleInBuilding* method), a user is supposed to give the arugments to the  parameters given below:
	1. ***personCount (int)***: total number of persons to set in the building for simulation purpose. 
	2. ***allPersonMove (boolean)***: expresses whether all persons are moving or not. If the value is true, all persons will be moving with respect to their assigned criteria, whereas if the value is false, a random percentage of total number of persons will be moving and the rest of them will be staying at their current location during the whole time.
	3. ***personsWithWheelChair (int)***: expresses the number of persons that are mobility-impaired among the total number of person.
4. Later, on lines 51 and 52 two objects *hlstream* and *ssStream* of classes  *HumanLocationStreamer* and *SpaceSensorsStreamer*, respectively, are initialized to generate the data streams randomly. 
5. In *hlstream*,  a user may also give the arguments to the parameters of its constructor (i.e., *HumanLocationStreamer*). These parameters are as follow:
	1. ***IRI (string)***: It is not recommended to change it as it is related the iri of the knowledge base.
	2. ***timeStep (long)***: a timestep when the data is injected to the datastream. In other words, the output will be generated after each timestep.
	3. ***freeFlow (boolean)***: expresses either there is a freeflow of people in the building or not. If yes, the cost from going from one node to another node is not affected by anything. On the other hand, if there is not any freeflow, the cost of going from one node to another node is affected if the relative occupancy of a specific space exceeds than its accommodation capacity.
	4. ***areaPerPersonM2 (float)***: expresses the area (in square meters) required for a person to have a freeflow in a space. Generally, it is taken as 1m^2.  
6. Similarly, in *ssStream*, the timestep parameter can be also adjusted. 
7. Once, a user has set all the parameters, the application can be run. 
8. As soon as the application runs, following outputs will start to be saved in the *data/output/_m* directory:
	1. ***node.txt*** : it stores the information about all nodes at each timestep in the following format. 
	
	`    node#  | safety value | node capacity (max. no. of people that fit in the node)|  No. of people positioned at node 
`

	2.  ***location_of_each_person.txt*** : it stores the information about the location of all persons at each timestep in the following format. 
	
	`person # | positioned at node #
`

	3.  ***edges_details_plus_excluded_persons.txt*** : it stores the information about all edges after each timestep, along with the details of the person who cannot access these edges in the following format. 

	`edge: (node#, node#) | cost (freeflow crossing time, e.g. [sec]) | safety value in range in the domain [0,...,1] | edge capacity [Max. No. of people that can pass through an edge per sec.] | not apt for evacuation for persons [person #]`

------------
------------

## Step-by-Step Explanation of CAREE

1. Creating a new object of Csparql Engine.

2. Initialize the engine with performTimestampFunction true

3. Setting up people in the building using a method *setPeopleInBuilding*

4. Registering C-SPARQL queries 

	+ Example 1 (each value represents one column)

		+ edge: (node#, node#)

		+ cost (freeflow crossing time, e.g. [sec]) | safety value in range in the domain [0,...,1]

		+ edge capacity [Max. No. of people that can pass through an edge per sec.]

		+ not apt for evacuation for persons [person #]

	+ Example 2

		+ node # 

		+ safety value 

		+ node capacity (max. no. of people that fit in the node)

		+ No. of people positioned at a node

	+ Example 3

		+ person #

		+ positioned at node #

5. Registering static data (schema (base model) + data (knowledge base) + inferences) in the C-SPARQL as a base model.

6. Creating instances of Random data streams 

	+ Human Location Streamer 

	+ Space Sensor Streamer

7. Injecting the streamers into the engine.

8. Binding the streams with new threads.

9. Creating the instances of the listeners and registering the CSPARQL queries to the engine. 

10. Adding the observers to the instances of the listeners.

11. Initializing all the threads.

12. Putting all the threads to sleep for a long time (e.g., 200 seconds).

13. Stopping all the threads.  

14. Unregistering all the streamers. 

15. Exiting the system.

------------

### Human Location Streamer 

The purpose of this steamer is to simulate the movements of person in the building randomly. This streamer consists of two schedulers:

+ Person Moving Scheduler

+ Person Resting Scheduler 
 
Following are the steps of this streamer: 

1. Keep running the streamer:

2. Run ComputeRestingphase method

3. Run a query to check who needs to move (i.e., whose motion status is Standing)

	+ If the query result is not empty

		+ Add it to the list of PersonTimerInformation class- using person, id, origin, destination, and cost of O-D pair.

		+ Update the space occupancy map depending on each person's current location (i.e., origin). 

		+ Check if there is freeflow:

			+ If yes, Add each person in the list of PersonTimerInformation in the Person Moving Scheduler individually using the addMovingPerson method. 

			+ Otherwise, run the ComputeAndAddExtraTime method

4. Update the Inference Model using updateModelBeforePersonMoves method. 

5. Check if someone finishes his/her movement

	Update the Inference Model using updateModelWhenPersonFinishedMoving method. 

6. Injecting the results for CSPARQL query using detectPersonLocationUsingIdQuadrupleGenerator()

------------

### Space Sensor Streamer

1. Using a SPARQL query to get all sensors installed in the building along with their type and location. 

2. Adding each Sensor object in the list of Sensor object class (it consists of sensorName, observationType, location, value).

3. Find if the location of the sensor exist in the  allSensorsValueAtSpecificLocationList (it consists of location and Space Object mapping). If not, update the allSensorsValueAtSpecificLocationList with the location name

4. Run generateSensorValue method.

5. Put the thread to sleep.  

6. Keep running the streamer:

	+ For each Sensor object in sensorDetailsList

		+ Run generateSensorValue method.

	+ For each Space in allSensorsValueAtSpecificLocationList

		+ Inject the quadruples for Instantaneous Safety Value in CSPARQL query

	+ Put the thread to sleep.  


------------
------------
