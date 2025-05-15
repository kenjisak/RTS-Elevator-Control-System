package otherResources;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Constants {
//    public static final String FILE_PATH = "/inputFinal.txt";
    public static final String FILE_PATH = "/inputFinal.txt";
    public static int START_FLOOR = 1;//main floor/ground floor aka which floor elevator spawns on
    public static final int MAX_FLOORS = 22;
    public static int MAX_ELEVATORS = 4;
    public static int MAX_CAPACITY= 5;//number of passengers allowed at the same time

    public static int DOOR_TO_CLOSED_TIME = 3;//elevator time that simulate the doors closing (rounded mean from measurements, exact is 4.92s)
    public static int DOOR_TO_OPENED_TIME = 3;//rounded mean from measurements, exact is 4.02s
    public static int DOOR_STAYS_OPEN_TIME = 5; //time the door stays open per person
    public static int ELEVATOR_TRAVEL_TIME = 10;//rounded mean from measurements, exact is 1.64 between 1 floor
    public static int RESEND_WAITING_MSG = 10;//time scheduler should wait before trying to queue a request again
    // public static int DOOR_TO_CLOSED_TIME = 1;//elevator time that simulate the doors closing (rounded mean from measurements, exact is 4.92s)
    // public static int DOOR_TO_OPENED_TIME = 1;//rounded mean from measurements, exact is 4.02s
    // public static int DOOR_STAYS_OPEN_TIME = 2; //time the door stays open per person
    // public static int ELEVATOR_TRAVEL_TIME = 3;//rounded mean from measurements, exact is 1.64 between 1 floor
    // public static int RESEND_WAITING_MSG = 10;//time scheduler should wait before trying to queue a request again

    public static int DOOR_FAULT_TIME = 20; //elevator time that simulates a door fault, transient fault
    public static int STUCK_FAULT_TIME = 20; //elevator time that simulates a stuck fault


    //types of faults as read from file
    public final static int NO_FAULT = 0;
    public final static int SOFT_FAULT = 1;
    public final static int HARD_FAULT = 2;
    public static int MAILPORT = 10000;
    public static final InetAddress MAILIP;

    static {
        try {
            MAILIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    public static int BASEELEVPORT = 20000;//BASEELEVPORT + this.carNum
    public static final InetAddress ELEVIP;

    static {
        try {
            ELEVIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    public static final int SCHEDPORT = 30000;
    public static final InetAddress SCHEDIP;

    static {
        try {
            SCHEDIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    public static int BASEFLOORPORT = 40000;//BASEFLOORPORT + this.flrNum
    public static final InetAddress FLOORIP;

    static {
        try {
            FLOORIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
