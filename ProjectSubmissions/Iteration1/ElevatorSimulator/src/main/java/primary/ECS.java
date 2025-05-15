package primary;

import ElevatorPackage.Elevator;

public class ECS {

    private static final String FILE_PATH = "src/main/resources/input.txt";

    public static void main(String[] args){

        Thread schedulerSubsystem = new Thread(new Scheduler());
        Thread floorSubsystem = new Thread(new FloorSubsystemSimulation(FILE_PATH));
        Thread elevatorSubsystem = new Thread(new Elevator(0),"Elevator Subsystem");

        schedulerSubsystem.start();
        floorSubsystem.start();
        elevatorSubsystem.start();

    }
}
