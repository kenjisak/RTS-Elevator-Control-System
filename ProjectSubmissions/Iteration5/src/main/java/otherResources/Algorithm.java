package otherResources;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static otherResources.Constants.*;

/***
 * Class made to handle the scheduling of the elevators.
 * The Scheduler will have an array of Algorithm classes (one for each elevator).
 *
 */

@Getter
@Setter
public class Algorithm {
    private boolean up; // true is direction is up, false if down
//    private boolean movingUp;
    private int requestsCount; // count of stops the elevator is to go to
    private int currentFloor;
    private int queueNumber;
    private boolean outOfService;
    private boolean doorsOpen;
    private boolean doorsChanging;
    private boolean idle;

    private ArrayList<Queue> schedule; // array of queues. 4 queues alternating in direction
    public Algorithm () {
        this.up = true;
//        this.movingUp = true;
        this.requestsCount = 0;
        this.schedule = new ArrayList<>(4); // Array contain all queues
        this.queueNumber = 0;
        this.outOfService = false;
        this.idle = true;

        // Adding 4 queues, each one of 2 types -> "Going UP" or "Going DOWN"
        // The first queue is the queue that will be the current direction
        // Default set to UP direction
        // Each queue represents a direction the elevator will go,
        //    and each queue contains all the floors that the elevator
        //    will visit on that run.
        // Whenever the first queue becomes empty, it is moved to the
        //    back and the direction is switched.
        schedule.add(new PriorityQueue<Integer>()); // up
        schedule.add(new PriorityQueue<Integer>(Collections.reverseOrder())); // down
        schedule.add(new PriorityQueue<Integer>()); // up
        schedule.add(new PriorityQueue<Integer>(Collections.reverseOrder())); // down
        this.currentFloor = START_FLOOR;
    }

    /***
     * Internal method that moves "front" queue to the back.
     * Used when changing directions the elevator is traveling.
     */
    private void shift() {
        this.schedule.add(this.schedule.size() - 1, this.schedule.remove(0));
        this.up = !this.up;
        this.queueNumber++;
        if (requestsCount != 0 && this.schedule.get(0).isEmpty()) {
            this.shift();
        }
    }

    /***
     * Internal method that adds a floor to one of the 4 queues based on queueIndex
     * @param floorNum floor to visit
     * @param queueIndex which queue to add it to
     */
    private void offer(int floorNum, int queueIndex) {
        if (!this.schedule.get(queueIndex).contains(floorNum)) {
            this.schedule.get(queueIndex).offer(floorNum);
            this.requestsCount ++;
        }
    }

    /***
     * @return integer at the top of the "front" queue (removes it from queue)
     */
    private int poll() {
        return (int) this.schedule.get(0).poll();
    }

    /***
     * @return integer at the top of the "front" queue (DOES NOT remove it from queue)
     */
    public int peek() {
        return (this.schedule.get(0).isEmpty()) ? -1 : (int) this.schedule.get(0).peek();
    }

    /***
     * Method used to add floor requests to the queues.
     *
     * @param pickUp pick up floor
     * @param dropOff drop off floor
     * @return QueueNumber where the request has been added
     */
    public int add(int pickUp, int dropOff) {
        boolean elevatorDirectionUp = dropOff > pickUp; // if direction of elevator is up
        if (requestsCount == 0 && (this.up != elevatorDirectionUp)) { // if scheduler is empty and current queue is in wrong direction
            this.shift();
        }
        int queueIndex =  this.whichQueue(pickUp, dropOff);
        this.offer(pickUp, queueIndex - this.queueNumber); // add both requests
        this.offer(dropOff, queueIndex - this.queueNumber); // note, both requests must go on same route direction
//        this.printQueues();
        return queueIndex;
    }

    /***
     * Will remove floor from top of queue is current floor is the same as removed number.
     * "remove if elevator has arrived to destination floor"
     * @return the floor the elevator just arrived at
     *  or  -1 if the elevator has not arrived
     */
    public int pop() {
        if (this.requestsCount == 0) {
            return -1;
        }
        if (currentFloor == this.peek()) { //if it arrived
            int number = this.poll(); //it removes the arrived from the queue
            this.requestsCount --;
            if (this.schedule.get(0).isEmpty()) {
                this.shift();
            }
            return number;
        }
        return -1;
    }

    /***
     * @param pickUp pick up floor
     * @param dropOff drop off floor
     * @return if the 2 floors are already scheduled
     */
    public boolean containsBoth (int pickUp, int dropOff) {
        boolean containsPickUp = this.schedule.get(0).contains(pickUp);
        boolean containsDropOff = this.schedule.get(0).contains(dropOff);
        boolean sameDir = this.up == (dropOff > pickUp);
        return containsDropOff && containsPickUp & sameDir;
    }

    /***
     * @param pickUp pick up floor
     * @param dropOff drop off floor
     * @return if the one of the floors are already scheduled
     */
    public boolean containsOne (int pickUp, int dropOff) {
        boolean containsPickUp = this.schedule.get(0).contains(pickUp);
        boolean containsDropOff = this.schedule.get(0).contains(dropOff);
        boolean sameDir = this.up == (dropOff > pickUp);
        return (containsDropOff || containsPickUp) & sameDir;
    }

    /***
     * @param pickUp pick up floor
     * @param dropOff drop off floor
     * @return if both floors could be reach by elevator on current run
     */
    public boolean onTheWay(int pickUp, int dropOff) {
        boolean elevatorDirectionUp = dropOff > pickUp;
        boolean pickUpOTW = elevatorDirectionUp ? pickUp >= this.peek() : pickUp <= this.peek();
        boolean dropOffOTW = elevatorDirectionUp ? dropOff >= this.peek() : dropOff <= this.peek();
        return pickUpOTW && dropOffOTW;
    }

    /***
     * @param pickUp pick up floor
     * @param dropOff drop off floor
     * @return which queue the incoming request would be in
     */
    public int whichQueue(int pickUp, int dropOff) {
        boolean elevatorDirectionUp = dropOff > pickUp; // if direction of elevator is up
        int queueIndex; // which queue to add floor to
        if (elevatorDirectionUp != this.up) { // if request direction is opposite to elevator direction
            queueIndex = 1;
        } else if (this.peek() == -1 || onTheWay(pickUp, dropOff)) { // else if both requests are on rout on current run
            queueIndex = 0;
        } else {
            queueIndex = 2;
        }
        return queueIndex+this.queueNumber;
    }

    /***
     * @param pickUp pick up floor
     * @param dropOff drop off floor
     * @return what would be the total floors traveled if floors where added to current direction
     */
    public int theoTravel(int pickUp, int dropOff) {
        boolean elevatorDirectionUp = dropOff > pickUp;
        return elevatorDirectionUp ? dropOff - this.peek() : this.peek() - dropOff;
    }

    /***
     * prints the schedule of the elevator
     */
    public void printQueues() {
        System.out.print(
                "[ "
                + (up ? "U" : "D") + this.schedule.get(0)
                + ", "
                + (up ? "D" : "U") + this.schedule.get(1)
                + ", "
                + (up ? "U" : "D") + this.schedule.get(2)
                + ", "
                + (up ? "D" : "U") + this.schedule.get(3)
                + " ]"
        );
        System.out.println(" - Count: " + this.requestsCount);
    }

    public static void main(String[] args) {
        PriorityQueue<Integer> a = new PriorityQueue<>();
        a.add(1);
        a.add(5);
        a.add(2);
        a.add(8);
        a.add(6);
        System.out.println(Arrays.toString(a.toArray()));
        System.out.println(a.toArray()[a.size()-1]);
        System.out.println(a.peek());
    }
}