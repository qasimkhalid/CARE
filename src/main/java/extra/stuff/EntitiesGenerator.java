package extra.stuff;

import java.util.*;

public class EntitiesGenerator {

    public static void main( String[] args ) throws Exception {

        //Edges
        List<Edge> corridorSegment = new ArrayList<Edge>();
        //For Junctions and Floor Exits Edges
        corridorSegment.add(new Edge("EE3", "POI1", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("EE0", "J3", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("EE1", "HE6", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("EE2", "HE11", "sbeo:CorridorSegment", 5, 10 , 1));

        corridorSegment.add(new Edge("J1", "RE7", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J1", "REw2", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J1", "HE1", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("J1", "RE6", "sbeo:CorridorSegment", 5, 10 , 1));

        corridorSegment.add(new Edge("J2", "RE8", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("J2", "REr1", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J2", "RE25", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J2", "REc1", "sbeo:CorridorSegment", 5, 10 , 2));

        corridorSegment.add(new Edge("J3", "HE11", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("J3", "RE9", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J3", "RE26", "sbeo:CorridorSegment", 5, 10 , 2));

        corridorSegment.add(new Edge("J4", "RE5", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J4", "REr1a", "sbeo:CorridorSegment", 5, 10 , 3));
        corridorSegment.add(new Edge("J4", "RE4", "sbeo:CorridorSegment", 5, 10 , 1));

        corridorSegment.add(new Edge("J5", "REr1", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J5", "REr1a", "sbeo:CorridorSegment", 5, 10 , 3));
        corridorSegment.add(new Edge("J5", "Rer3a", "sbeo:CorridorSegment", 5, 10 , 1));

        corridorSegment.add(new Edge("J6", "RE4", "sbeo:CorridorSegment", 5, 10 , 3));
        corridorSegment.add(new Edge("J6", "RE3", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J6", "REr2a", "sbeo:CorridorSegment", 5, 10 , 3));

        corridorSegment.add(new Edge("J7", "J12", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("J7", "REr2a", "sbeo:CorridorSegment", 5, 10 , 3));
        corridorSegment.add(new Edge("J7", "REr4a", "sbeo:CorridorSegment", 5, 10 , 1));

        corridorSegment.add(new Edge("J8", "RE12", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("J8", "REr3", "sbeo:CorridorSegment", 5, 10 , 3));
        corridorSegment.add(new Edge("J8", "RE28a", "sbeo:CorridorSegment", 5, 10 , 12));

        corridorSegment.add(new Edge("J9", "RE17", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("J9", "REw1", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("J9", "REm1", "sbeo:CorridorSegment", 5, 10 , 3));

        corridorSegment.add(new Edge("J10", "RE24", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("J10", "RE31", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J10", "HE8", "sbeo:CorridorSegment", 5, 10 , 1));

        corridorSegment.add(new Edge("J11", "RE16", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J11", "HE9", "sbeo:CorridorSegment", 5, 10 , 3));
        corridorSegment.add(new Edge("J11", "HE10", "sbeo:CorridorSegment", 5, 10 , 1));

        corridorSegment.add(new Edge("J12", "REr2", "sbeo:CorridorSegment", 5, 10 , 1));
        corridorSegment.add(new Edge("J12", "REr4", "sbeo:CorridorSegment", 5, 10 , 2));
        corridorSegment.add(new Edge("J12", "J7", "sbeo:CorridorSegment", 5, 10 , 1));


        List<Edge> poiSegment = new ArrayList<Edge>();
        poiSegment.add(new Edge("POI1", "EE3", "sbeo:PointOfInterestSegment", 5, 10 , 1));
        poiSegment.add(new Edge("POI1", "POI2", "sbeo:PointOfInterestSegment", 5, 10 , 1));

        poiSegment.add(new Edge("POI2", "POI1", "sbeo:PointOfInterestSegment", 5, 10 , 1));
        poiSegment.add(new Edge("POI2", "RE7", "sbeo:PointOfInterestSegment", 5, 10 , 2));

        poiSegment.add(new Edge("POI3", "HE4", "sbeo:PointOfInterestSegment", 5, 10 , 1));
        poiSegment.add(new Edge("POI3", "POI4", "sbeo:PointOfInterestSegment", 5, 10 , 1));

        poiSegment.add(new Edge("POI4", "POI3", "sbeo:PointOfInterestSegment", 5, 10 , 1));
        poiSegment.add(new Edge("POI4", "HE4", "sbeo:PointOfInterestSegment", 5, 10 , 1));

        poiSegment.add(new Edge("POI5", "HE12", "sbeo:PointOfInterestSegment", 5, 10 , 1));
        poiSegment.add(new Edge("POI5", "POI6", "sbeo:PointOfInterestSegment", 5, 10 , 1));

        poiSegment.add(new Edge("POI6", "POI5", "sbeo:PointOfInterestSegment", 5, 10 , 1));
        poiSegment.add(new Edge("POI6", "HE12", "sbeo:PointOfInterestSegment", 5, 10 , 1));



        List<Edge> roomSegment = new ArrayList<Edge>();
        roomSegment.add(new Edge("R1", "REr1", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("R1", "REr1a", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("R2", "REr2", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("R2", "REr2a", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("J6", "REr2a", "sbeo:RoomSegment", 5, 10 , 3));

        roomSegment.add(new Edge("R3", "REr3", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("R3", "REr3a", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("R4", "REr4", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("REr3", "REr4", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("R4", "REr4a", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("RE21", "REr4a", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("C1", "REC1", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("REra2", "REC1", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("RA1", "REra1", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE30", "REra1", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE15", "REra1", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("RA2", "REra2", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE9", "REra2", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("WC1", "REw1", "sbeo:RoomSegment", 5, 10 , 1));


        roomSegment.add(new Edge("WC2", "REw2", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE7", "REw2", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("RE1", "S1", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE1", "RE18", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE1", "RE17", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE2", "S2", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE2", "RE18", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE2", "RE19", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE3", "S3", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE3", "RE20", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("RE3", "J6", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("RE4", "S4", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE5", "S5", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE5", "RE6", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("RE7", "S7", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE8", "S8", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE9", "S9", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE10", "S10", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE10", "RE26", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE10", "RE27a", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("RE11", "S11", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE11", "RE27a", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE11", "RE27", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("RE12", "S12", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE12", "RE27", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE13", "RE13", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("RE13", "RE28", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE13", "RE28a", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE14", "S14", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE14", "RE28", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("RE14", "RE29", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE15", "S15", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE15", "RE29", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE15", "RE16", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE16", "S16", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE16", "RE15", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE17", "S17", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE18", "S18", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE19", "S19", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE19", "RE20", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE20", "S20", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE21", "S21", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE21", "RE22", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("RE22", "S22", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE22", "RE23", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE23", "S23", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE23", "RE24", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE24", "S24", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE24", "J10", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE25", "S25", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE26", "S26", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE27", "S27", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("RE27a", "S27", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("RE28", "S28", "sbeo:RoomSegment", 5, 10 , 2));
        roomSegment.add(new Edge("RE28a", "S28", "sbeo:RoomSegment", 5, 10 , 2));

        roomSegment.add(new Edge("RE29", "S29", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE30", "S30", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("RE30", "RE31", "sbeo:RoomSegment", 5, 10 , 1));

        roomSegment.add(new Edge("RE31", "S31", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("MO1", "REm1", "sbeo:RoomSegment", 5, 10 , 1));
        roomSegment.add(new Edge("REr3", "REr2", "sbeo:RoomSegment", 5, 10 , 2));




        List<Edge> hallSegment = new ArrayList<Edge>();
        hallSegment.add(new Edge("HE1", "Hall2", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE1", "J1", "sbeo:HallSegment", 5, 10 , 1));

        hallSegment.add(new Edge("HE2", "Hall2", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE2", "REw2", "sbeo:HallSegment", 5, 10 , 1));

        hallSegment.add(new Edge("HE3", "Hall2", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE3", "RE8", "sbeo:HallSegment", 5, 10 , 1));

        hallSegment.add(new Edge("HE4", "POI3", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE4", "POI4", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE4", "Hall1", "sbeo:HallSegment", 5, 10 , 1));

        hallSegment.add(new Edge("HE5", "Hall1", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE5", "REw1", "sbeo:HallSegment", 5, 10 , 1));

        hallSegment.add(new Edge("HE6", "Hal1", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE6", "EE1", "sbeo:HallSegment", 5, 10 , 1));

        hallSegment.add(new Edge("HE7", "Hall3", "sbeo:HallSegment", 5, 10 , 2));
        hallSegment.add(new Edge("HE7", "REm1", "sbeo:HallSegment", 5, 10 , 1));

        hallSegment.add(new Edge("HE8", "Hall3", "sbeo:HallSegment", 5, 10 , 2));
        hallSegment.add(new Edge("HE8", "J10", "sbeo:HallSegment", 5, 10 , 1));

        hallSegment.add(new Edge("HE9", "Hall3", "sbeo:HallSegment", 5, 10 , 2));
        hallSegment.add(new Edge("HE9", "J11", "sbeo:HallSegment", 5, 10 , 3));

        hallSegment.add(new Edge("HE10", "Hall4", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE10", "J11", "sbeo:HallSegment", 5, 10 , 1));

        hallSegment.add(new Edge("HE11", "Hall4", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE11", "EE2", "sbeo:HallSegment", 5, 10 , 1));

        hallSegment.add(new Edge("HE12", "Hall4", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE12", "POI5", "sbeo:HallSegment", 5, 10 , 1));
        hallSegment.add(new Edge("HE12", "POI6", "sbeo:HallSegment", 5, 10 , 1));


        for (int i = 1; i <= 4; i++) {
            if(i==1) {
                for (int j = 0; j < corridorSegment.size(); j++) {
                    System.out.println(corridorSegment.get(j).toString());

                }
            }
            if(i==2) {
                for (int j = 0; j < poiSegment.size(); j++) {
                    System.out.println(poiSegment.get(j).toString());

                }            }
            if(i==3) {
                for (int j = 0; j < roomSegment.size(); j++) {
                    System.out.println(roomSegment.get(j).toString());

                }
            }
            if(i==4) {
                for (int j = 0; j < hallSegment.size(); j++) {
                    System.out.println(hallSegment.get(j).toString());

                }
            }
        }





//        ###Types###
//        PointOfInterestSegment
//        CorridorSegment
//        RoomSegment
//        HallSegment



        // Nodes
//        List<String> floorExitNodes = new ArrayList<String>();
//        Collections.addAll(floorExitNodes,
//                "EE3", "EE0", "EE1", "EE2");
//
////        for (int i = 0; i <=floorExitNodes.size(); i++) {
////            Node n = new Node(floorExitNodes.get(i), "FloorExit", 5, 10, idCounter);
////            idCounter++;
////        }
//
//
//        List<String> roomExitNodes = new ArrayList<String>();
//        Collections.addAll(roomExitNodes,
//                "REra2", "REra1",
//                "HE1", "HE2", "HE3", "HE4","HE5","HE6","HE7","HE8","HE9","HE10","HE11","HE12",
//                "REwc1", "REwc2",
//                "REc1",
//                "REm1");
//
////        for (int i = 0; i <=roomExitNodes.size(); i++) {
////            Node n = new Node(roomExitNodes.get(i), "FloorExit", 5, 10, idCounter);
////            idCounter++;
////        }
//
//        List<String> closedSpacesNodes = new ArrayList<String>();
//        for (int i = 1; i <= 31; i++) {
//            int number = i;
//            String name = "S"+number;
//            String rename = "RE"+number;
//            closedSpacesNodes.add(name);
//            roomExitNodes.add(rename);
//        }
//        for (int i = 1; i <= 4; i++) {
//            int number = i;
//            String name = "R"+number;
//            String rename = "REr"+number;
//            String rename2 = "REr"+number+"a";
//            closedSpacesNodes.add(name);
//            roomExitNodes.add(rename);
//            roomExitNodes.add(rename2);
//        }
//        Collections.addAll(closedSpacesNodes,
//                "Hall1", "Hall2", "Hall3", "Hall4",
//                "MO1",
//                "RA1", "RA2",
//                "WC1", "WC2",
//                "C1");
//
//        List<String> junctionNodes = new ArrayList<String>();
//        junctionNodes.add("J1");
//        junctionNodes.add("J2");
//        junctionNodes.add("J3");
//        junctionNodes.add("J4");
//        junctionNodes.add("J5");
//        junctionNodes.add("J6");
//        junctionNodes.add("J7");
//        junctionNodes.add("J8");
//        junctionNodes.add("J9");
//        junctionNodes.add("J10");
//        junctionNodes.add("J11");
//        junctionNodes.add("J12");
//
//        List<String> poiNodes = new ArrayList<String>();
//        poiNodes.add("POI1");
//        poiNodes.add("POI2");
//        poiNodes.add("POI3");
//        poiNodes.add("POI4");
//        poiNodes.add("POI5");
//        poiNodes.add("POI6");
//
//
//        int idCounter = 1;
//        for (int i = 1; i <= 5; i++) {
//            if(i==1) {
//                for (int j = 0; j < floorExitNodes.size(); j++) {
//                    Node n = new Node(floorExitNodes.get(j), "sbeo:FloorExit", 5, 10, idCounter);
//                    System.out.println(n.toString());
//                    idCounter++;
//                }
//            }
//            if(i==2) {
//                for (int j = 0; j < roomExitNodes.size(); j++) {
//                    Node n = new Node(roomExitNodes.get(j), "sbeo:RoomExit", 5, 10, idCounter);
//                    System.out.println(n.toString());
//                    idCounter++;
//                }
//            }
//            if(i==3) {
//                for (int j = 0; j < junctionNodes.size(); j++) {
//                    Node n = new Node(junctionNodes.get(j), "sbeo:Junction", 5, 10, idCounter);
//                    System.out.println(n.toString());
//                    idCounter++;
//                }
//            }
//            if(i==4) {
//                for (int j = 0; j < closedSpacesNodes.size(); j++) {
//                    Node n = new Node(closedSpacesNodes.get(j), "seas:Room", 5, 10, idCounter);
//                    System.out.println(n.toString());
//                    idCounter++;
//                }
//            }
//            if(i==5) {
//                for (int j = 0; j < poiNodes.size(); j++) {
//                    Node n = new Node(poiNodes.get(j), "sbeo:PointOfInterest ", 5, 10, idCounter);
//                    System.out.println(n.toString());
//                    idCounter++;
//                }
//            }

//        }


    }
}
