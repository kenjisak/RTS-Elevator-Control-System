package primary;

import java.util.ArrayList;

public class main {

    private static final String FILE_PATH = "src/main/resources/input1Line.txt";
    public static final int MAX_FLOORS = 6;
    public static final int MAX_ELEVATORS = 2;

    public static void main(String[] args){

        ArrayList<Thread> floors = new ArrayList<>();

        Thread schedulerSubsystem = new Thread(new Scheduler());

        //initialize floors, one thread per floor, floor 0 is the ground floor
        for(int i = 0; i<MAX_FLOORS; i++){
            Thread floorSubsystem = new Thread(new Floor(FILE_PATH, i), "Floor " + i);
            floors.add(floorSubsystem);
        }

       //one elevator for now
        Thread elevatorSubsystem = new Thread(new Elevator(1),"Elevator Subsystem");

        //start the threads
        schedulerSubsystem.start();
        for (int i = 0; i< MAX_FLOORS; i++){
            floors.get(i).start();
        }
        elevatorSubsystem.start();
    }
}
