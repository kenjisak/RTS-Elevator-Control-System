package ElevatorPackage;

public class ArrivalSensor {
    private boolean elevatorHere = false;
    public ArrivalSensor(){

    }

    public boolean isElevatorHere() {
        return elevatorHere;
    }
    public void setElevatorHere(boolean value) {
        elevatorHere = value;
    }
}
