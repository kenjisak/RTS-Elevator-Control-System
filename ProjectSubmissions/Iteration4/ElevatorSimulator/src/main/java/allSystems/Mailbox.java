package allSystems;

import lombok.Getter;
import lombok.Setter;
import otherResources.Message;
import udp.UDPMail;
import java.util.*;

import static otherResources.Constants.*;
/**
 * This class implements a thread-safe messaging structure which organizes messages
 * by recipient and puts them in a corresponding queue.
 * Each message queue can be accessed through a HashMap of (String recipient, ArrayDeque<Message> queue)
 * They keys are: "Scheduler", "Elevator<i>", where i is the elevator number, and "Floor<j>", where j is the floor number
 * Each entity must register() so they can be allocated a message queue
 * The messages are added and extracted in a producer - consumer pattern
 */
@Getter
@Setter
public class Mailbox implements Runnable{
    private HashMap<String, ArrayDeque<Message>> messageQueueMap = new HashMap<>(); //map of queues,with key = hashCode of registered thread class and value = a queue of messages for that specific entity
    private UDPMail udp;
    public Mailbox() { udp = new UDPMail(MAILPORT,this); }

    /**
     * registers a Scheduler, Floor or Elevator thread by allocating a new queue for its messages
     * @param key: Keys allowed are: "Scheduler", "Elevator<i>", where i is the elevator number, and "Floor<j>", where j is the floor number
     * @exception: throws exception if key is incorrect
     */
    public synchronized void register(String key){
        Set<String> allowedKeys = new HashSet<String>();
        allowedKeys.add("Scheduler");
        for (int i = 0; i < MAX_FLOORS; i++){
            allowedKeys.add("Floor"+i);
        }
        for (int i = 1; i <= MAX_ELEVATORS; i++){
            allowedKeys.add("Elevator"+i);
        }
        if (allowedKeys.contains(key)){
            messageQueueMap.put( key, new ArrayDeque<Message>());
        }
        else {
            try {
                throw new Exception("Incorrect key name for mailbox");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }
    }

    /**
     * Gets the first message from the queue for entity of a specific type (can be FLOOR, SCHED or ELEVATOR)
     * @param receiver : the type of the entity for which this message is intended
     * @return the first message in the entity's queue
     */
    public synchronized Message get(String receiver) {
        ArrayDeque<Message> queue = messageQueueMap.get(receiver);

//        while (queue.isEmpty()){
//            try {
//                wait();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
        //there is no message to be obtained
        if (queue.isEmpty()){ return null; }

        //message queue is no longer empty
        Message message = queue.pop();
        notifyAll();
        return message;

    }

    /**
     * Adds the message m to a specific queue based on the message's intended receiver
     * @param m message with metadata that includes a sender and a receiver
     */
    public synchronized void put(Message m) {
        ArrayDeque<Message> queue = messageQueueMap.get(m.getToWho());
        queue.add(m);
        //System.out.println("Message has been placed in the message queue for " + m.getToWho());
        notifyAll();
    }
    @Override
    public void run() {
        System.out.println("Mailbox is Ready");
        while(!Thread.currentThread().isInterrupted()){
            udp.receiveMessage();
        }
    }
    public static void main(String[] args)
    {
        Thread mailbox = new Thread(new Mailbox(),"Mailbox");
        mailbox.start();
    }
}
