package schedStateMachine;

import allSystems.Scheduler;
import otherResources.Message;
import otherResources.MessageType;

import static otherResources.Constants.MAILIP;
import static otherResources.Constants.MAILPORT;

public class SchedulerState {

    public void setTimer(Scheduler context){}
    public void timeout(Scheduler context){}
    public void schedOperation(Scheduler context, String action){
        //System.out.println("Scheduler" + " Operation: " + action);
    }
    public void displayState(Scheduler context, String state){
        System.out.println("State: Scheduler " + state);
    }
    public void sleep(int seconds) {
        try {
            Thread.sleep(1000);
            System.out.println(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public Message checkMsgs(Scheduler context){
        context.getUdp().sendGetRequest("Scheduler");
        Message m = context.getUdp().receiveMessage();
        return m;
    }
    public void respondMsg(Scheduler context, Message m, String dest){
        m.setFromWho("Scheduler");
        m.setToWho(dest);
//        Mailbox.getInstance().put(m);
        context.getUdp().sendMessage(m,MAILPORT,MAILIP);
    }

    public void respondMsg(Scheduler context, Message m, String dest, String fromWho){
        m.setFromWho(fromWho);
        m.setToWho(dest);
//        Mailbox.getInstance().put(m);
        context.getUdp().sendMessage(m,MAILPORT,MAILIP);
    }
}


