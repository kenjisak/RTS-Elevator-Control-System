package otherResources;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Constants {
    public static final String FILE_PATH = "/input.txt";
    public static final int MAX_FLOORS = 6;
    public static final int MAX_ELEVATORS = 2;
    public final static int DOOR_TO_CLOSED_TIME = 5;//elevator time that simulate the doors closing (rounded mean from measurements, exact is 4.92s)
    public final static int DOOR_TO_OPENED_TIME = 4;//rounded mean from measurements, exact is 4.02s
    public final static int ELEVATOR_TRAVEL_TIME = 2;//rounded mean from measurements, exact is 1.64 between 1 floor


    public final static int DOOR_FAULT_TIME = 5; //elevator time that simulates a door fault
    public final static int STUCK_FAULT_TIME = 5; //elevator time that simulates a stuck fault


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
