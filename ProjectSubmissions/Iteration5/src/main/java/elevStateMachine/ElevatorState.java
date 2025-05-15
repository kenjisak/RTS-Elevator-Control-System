package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;

import static otherResources.Constants.MAILIP;
import static otherResources.Constants.MAILPORT;

/**
 * Interface representing the states of an Elevator in a state machine.
 */
public class ElevatorState {
    /**
     * the entry point of each state, to be overridden by derived classes
     * @param context the Elevator context
     */
    public void setTimer(Elevator context){}

    /**
     * the exit point of each state, to be overridden by derived classes
     * @param context the Elevator context
     */
    public void timeout(Elevator context){}
    public void elevOperation(Elevator context, String action){
        System.out.println(context.getCurrentSystemTime() + "Elevator " + context.getCarNum() + " Operation: " + action);
    }
    public void displayState(Elevator context, String state){
        System.out.println("State: Elevator " + context.getCarNum() + state);
    }

    /** A reusable sleep method to minimize code duplication
     * @param seconds: how many seconds you want the thread to sleep for
     */
    public void sleep(int seconds) {
        while(seconds != 0){
            try {
                Thread.sleep(1000);
                //System.out.println(seconds);
                seconds -= 1;
            } catch (InterruptedException ignored) {}
        }
    }

    /** Reusable method for all states that checks the Elevators messages by sending a GET request to the Mailbox and receives a Message response
     * @param context: The Elevator object to be able to access its current State or other methods/member variables
     * @return: a Message it receives, either a valid Message object or a null if it has none in its mailbox
     */
    public Message checkMsgs(Elevator context){

        context.getUdp().sendGetRequest("Elevator" + context.getCarNum());
        Message m = context.getUdp().receiveMessage();
        context.getMsgsReceived().add(m);
        return m;
    }

    /** Reusable method for all states that sends a Message response to the Mailbox
     * @param m: The Message object the Elevator wants to respond with
     * @param context: The Elevator object to be able to access its current State or other methods/member variables
     */
    public void respondMsg(Message m, Elevator context){
        m.setFromWho("Elevator" + context.getCarNum());
        m.setToWho("Scheduler");

        context.getUdp().sendMessage(m,MAILPORT,MAILIP);
    }
}