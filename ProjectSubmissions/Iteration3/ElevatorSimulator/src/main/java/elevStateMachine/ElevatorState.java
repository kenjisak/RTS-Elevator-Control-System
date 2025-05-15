package elevStateMachine;

import allSystems.Elevator;
import otherResources.Message;

import static otherResources.Constants.MAILIP;
import static otherResources.Constants.MAILPORT;

/**
 * Interface representing the states of an Elevator in a state machine.
 */
public class ElevatorState {
    public void setTimer(Elevator context){}
    public void timeout(Elevator context){}
    public void elevOperation(Elevator context, String action){
        System.out.println("Elevator " + context.getCarNum() + " Operation: " + action);
    }
    public void displayState(Elevator context, String state){
        System.out.println("State: Elevator " + context.getCarNum() + state);
    }
    public void sleep(int seconds) {
        while(seconds != 0){
            try {
                Thread.sleep(1000);
                //System.out.println(seconds);
                seconds -= 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public Message checkMsgs(Elevator context){

        context.getUdp().sendGetRequest("Elevator" + context.getCarNum());
        Message m = context.getUdp().receiveMessage();
        context.getMsgsReceived().add(m);
        return m;
    }

    public void respondMsg(Message m, Elevator context){
        m.setFromWho("Elevator" + context.getCarNum());
        m.setToWho("Scheduler");

        context.getUdp().sendMessage(m,MAILPORT,MAILIP);
    }
}