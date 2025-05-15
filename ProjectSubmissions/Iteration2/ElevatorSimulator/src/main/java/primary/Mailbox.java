package primary;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements a thread-safe messaging structure which organizes messages
 * by recipient and puts them in a corresponding queue.
 * Each message queue can be accessed through a HashMap of (String recipient, ArrayDeque<Message> queue)
 * They keys are: "Scheduler", "Elevator<i>", where i is the elevator number, and "Floor<j>", where j is the floor number
 * Each entity must register() so they can be allocated a message queue
 * The messages are added and extracted in a producer - consumer pattern
 */
public class Mailbox {
    private HashMap<String, ArrayDeque<Message>> messageQueueMap = new HashMap<>(); //map of queues,with key = hashCode of registered thread class and value = a queue of messages for that specific entity
    private static Mailbox instance = null;
    private Mailbox() {
    }
    /**
     * Thread-safe access method for the singleton instance of Mailbox. A single instance of Mailbox is initialized in this method.
     *
     * @return the instance of Mailbox
     */
    public synchronized static Mailbox getInstance(){
        if (instance == null){
            instance = new Mailbox();
        }
        return instance;
    }

    /**
     * registers a Scheduler, Floor or Elevator thread by allocating a new queue for its messages
     * @param key: Keys allowed are: "Scheduler", "Elevator<i>", where i is the elevator number, and "Floor<j>", where j is the floor number
     * @exception: throws exception if key is incorrect
     */
    public synchronized void register(String key){
        Set<String> allowedKeys = new HashSet<String>();
        allowedKeys.add("Scheduler");
        for (int i = 0; i < main.MAX_FLOORS; i++){
            allowedKeys.add("Floor"+i);
        }
        for (int i = 1; i <= main.MAX_ELEVATORS; i++){
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

        while (queue.isEmpty()){ //there is no message to be obtained
            return null;
//            try {
//                wait();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        }
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

}
