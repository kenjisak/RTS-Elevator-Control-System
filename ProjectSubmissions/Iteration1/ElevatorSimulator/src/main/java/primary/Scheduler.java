package primary;

import java.util.ArrayList;

public class Scheduler implements Runnable{
    private ArrayList<Message> events;
    private ArrayList<Message> msgsReceived;

    public Scheduler(){
        events = new ArrayList<>();
        msgsReceived = new ArrayList<>();
    }

    public void readMessage(Message m){
        m.print();
    }

    public void addMessage(Message m){
        System.out.println("Scheduler received");
        m.print();
        events.add(m);
    }

    @Override
    public void run() {
        System.out.println("Scheduler is ready");
        Message message = null;
        while (true){
            //check if there are any messages in queue for scheduler, expecting message from floor
            message = Messaging.getInstance().get(EntityType.SCHED);
            if (message != null){
                msgsReceived.add(message);
                events.add(message);
                System.out.println("Scheduler received message from " + message.getFromWho()+ ", sending message scheduler -> elevator");
                message.print();
                //make a new object so that the metadata fromWho and toWho is not overwritten
                Message schedMessage = new Message(message);
                schedMessage.setFromWho(EntityType.SCHED);
                schedMessage.setToWho(EntityType.ELEVATOR);
                Messaging.getInstance().put(schedMessage);

            }
            //check if there are any messages in mailbox for scheduler, expecting message from elevator
            message = Messaging.getInstance().get(EntityType.SCHED);
            if (message != null){
                msgsReceived.add(message);
                events.add(message);
                System.out.println("Scheduler received message from " + message.getFromWho()+ ", sending message scheduler -> floor");
                //make a new object so that the metadata fromWho and toWho is not overwritten
                Message schedMessage = new Message(message);
                schedMessage.setFromWho(EntityType.SCHED);
                schedMessage.setToWho(EntityType.FLOOR);
                Messaging.getInstance().put(schedMessage);
                return;
            }

        }
    }

    public ArrayList<Message> getMsgsReceived(){ return msgsReceived; }
}
