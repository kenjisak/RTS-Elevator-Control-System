package allSystems;

import elevStateMachine.*;
import lombok.Getter;
import lombok.Setter;
import otherResources.Message;
import udp.UDP;
import java.util.ArrayList;

import static otherResources.Constants.*;
@Getter
@Setter
public class Elevator implements Runnable{
    private final int carNum;
    private int currFloor;
    private ArrayList<Message> msgsReceived;
    private ElevatorState currentState;
    private UDP udp;
    private int ELEVPORT;
    public Elevator(int carNum){
        this.currentState = new Idle();//initial state on start up
        this.carNum = carNum;
        this.currFloor = 0;//0 is main floor

        msgsReceived = new ArrayList<>();

        this.ELEVPORT = BASEELEVPORT + this.carNum;
        udp = new UDP(ELEVPORT);
        udp.register("Elevator" + this.carNum);
    }
    public void moveUp(int travelTime){
        while(travelTime != 0){
            try {
                Thread.sleep(1000);
                System.out.println(travelTime);
                travelTime -= 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.currFloor++;
        System.out.println("Elevator " + carNum + ": Moved Up to Floor " + currFloor);
    }
    public void moveDown(int travelTime){
        while(travelTime != 0){
            try {
                Thread.sleep(1000);
                System.out.println(travelTime);
                travelTime -= 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.currFloor--;
        System.out.println("Elevator " + carNum + ": Moved Down to Floor " + currFloor);
    }
    @Override
    public void run(){
        System.out.println("\n" + Thread.currentThread().getName() + " has started.");
        currentState.setTimer(this);//call entry of Idle state on start up.
    }
    public static void main(String[] args){
        ArrayList<Thread> elevatorSubsystem = new ArrayList<>();

        //initialize elevators
        for(int i = 1; i< MAX_ELEVATORS + 1; i++){
            Thread elevators = new Thread(new Elevator(i), "Elevator " + i);
            elevatorSubsystem.add(elevators);
        }
        //start the threads
        for (int i = 0; i< MAX_ELEVATORS; i++){
            elevatorSubsystem.get(i).start();
        }

        //one elevator for now
//        Thread elevatorSubsystem = new Thread(new Elevator(1),"Elevator Subsystem");
//        elevatorSubsystem.start();
    }
}
