package primary;

import java.util.ArrayList;

public class Elevator implements Runnable{
    private final int carNum;
    private int currFloor;
    private ArrayList<Message> msgsReceived;
    private ElevatorState currentState;
    public Elevator(int carNum){
        this.currentState = new idle();//initial state on start up
        this.carNum = carNum;
        this.currFloor = 0;//0 is main floor

        msgsReceived = new ArrayList<>();
        Mailbox.getInstance().register("Elevator"+this.carNum);//make a new queue in the mailbox for this particular elevator
    }
    public int getCarNum() {
        return carNum;
    }
    public void setState(ElevatorState currentState) {
        this.currentState = currentState;
    }
    public ElevatorState getCurrentState(){
        return this.currentState;
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
        try {//so prints dont get jumbled up with floor and scheduler, TODO: remove this later
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\n" + Thread.currentThread().getName() + " has started.");
        currentState.SetTimer(this);//call entry of Idle state on start up.
    }
    public ArrayList<Message> getMsgsReceived(){ return msgsReceived; }
    public int getCurrFloor() { return this.currFloor; }
}
