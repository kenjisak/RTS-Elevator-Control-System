package primary;

import ButtonPackage.FloorButton;
import ElevatorPackage.ArrivalSensor;

/**
 * This class represents one floor with its number
 * 2 buttons upBtn and downBtn at which an elevator can be summoned by user
 * 1 arrival sensor to detect the presence of an elevator
 */

public class Floor implements Runnable{
    private final int flrNum;
    private FloorButton upBtn,downBtn;
    private String directionLamp;//up or down //TODO not sure if we need this, on diagram in assignment specs but does not appear in text explanation
    private Scheduler scheduler;
    private ArrivalSensor arrivalSensor;
    public Floor(int flrNum, Scheduler scheduler){
        this.flrNum = flrNum;
        this.directionLamp = null;
        this.scheduler = scheduler;
    }
    public void run() {
        System.out.println(Thread.currentThread().getName() + " has started.");
    }
}

