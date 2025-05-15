package otherResources;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Constants {
    public static final String FILE_PATH = "/input.txt";
    public static final int MAX_FLOORS = 6;
    public static final int MAX_ELEVATORS = 2;

    public static final int MAILPORT = 69;
    public static final InetAddress MAILIP;

    static {
        try {
            MAILIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    public static int BASEELEVPORT = 23;//BASEELEVPORT + this.carNum
    public static final InetAddress ELEVIP;

    static {
        try {
            ELEVIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    public static final int SCHEDPORT = 80;
    public static final InetAddress SCHEDIP;

    static {
        try {
            SCHEDIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    public static int BASEFLOORPORT = 70;//BASEFLOORPORT + this.flrNum
    public static final InetAddress FLOORIP;

    static {
        try {
            FLOORIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
