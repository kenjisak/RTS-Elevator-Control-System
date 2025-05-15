package primary;

import java.util.ArrayDeque;
import java.util.HashMap;

/**
 * This class implements a thread-safe messaging structure which organizes messages
 * by recipient and puts them in a corresponding queue.
 * Each message queue can be accessed through a HashMap of (EntityType recipient, ArrayDeque<Message> queue)
 * The messages are added and extracted in a producer - consumer pattern

 */
public class Messaging {
    private final HashMap<EntityType, ArrayDeque<Message>> messageQueueMap; //map of queues,with key = entity type and value = a queue of messages for that specific entity
    private static Messaging instance = null;
    private Messaging() {
        messageQueueMap = new HashMap<>();
        messageQueueMap.put(EntityType.FLOOR, new ArrayDeque<Message>());
        messageQueueMap.put(EntityType.ELEVATOR, new ArrayDeque<Message>());
        messageQueueMap.put(EntityType.SCHED, new ArrayDeque<Message>());
    }
    /**
     * Thread-safe access method for the singleton instance of Messaging. A single instance of Messaging is initialized in this method.
     *
     * @return the instance of Messaging
     */
    public synchronized static Messaging getInstance(){
        if (instance == null){
            instance = new Messaging();
        }
        return instance;
    }

    /**
     * Gets the first message from the queue for entity of a specific type (can be FLOOR, SCHED or ELEVATOR)
     * @param receiver : the type of the entity for which this message is intended
     * @return the first message in the entity's queue
     */
    public synchronized Message get(EntityType receiver) {
        ArrayDeque<Message> queue = messageQueueMap.get(receiver);

        while (queue.isEmpty()){ //there is no message to be obtained
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
    public synchronized void put(Message m) { //
        ArrayDeque<Message> queue = messageQueueMap.get(m.getToWho());
        queue.add(m);
        System.out.println("Message has been placed in the message queue for " + m.getToWho());
        notifyAll();
    }

}
