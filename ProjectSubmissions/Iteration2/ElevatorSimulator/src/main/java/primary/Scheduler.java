package primary;

import java.util.ArrayList;

public class Scheduler implements Runnable{
    private ArrayList<Message> events;
    private ArrayList<Message> msgsReceived;
    private SchedulerState currentState;

    public Message currentMsg;

    public Scheduler(){
        currentState = new checkingMessages();
        events = new ArrayList<>();
        msgsReceived = new ArrayList<>();
        Mailbox.getInstance().register("Scheduler");//this allocates a message queue in the Mailbox for the scheduler
    }

    public void readMessage(Message m){
        m.print();
    }

    public void addMessage(Message m){
        System.out.println("Scheduler received");
        m.print();
        events.add(m);
    }
    public void setState(SchedulerState currentState) {
        this.currentState = currentState;
    }
    public SchedulerState getCurrentState(){
        return this.currentState;
    }

    public MovingDirection determineMovingDirection(int currFloor, int destFloor){
        MovingDirection direction;

        if(currFloor < destFloor){
            direction = MovingDirection.UP;
        } else {
            direction = MovingDirection.DOWN;
        }

        return direction;
    }

    @Override
    public void run() {
        System.out.println("Scheduler is ready");
        currentState.SetTimer(this);

    }

    public ArrayList<Message> getMsgsReceived(){ return msgsReceived; }
}