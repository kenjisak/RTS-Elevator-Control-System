package ButtonPackage;

/**
 * This class is a button inside the elevator car that represents a floor number request (1 to MAX_FLOORS)
 */
public class CarFloorNumberButton extends Button {
    private final int number;//floor button number in elevator
    public CarFloorNumberButton(int number){
        this.number  = number;
    }
    public int getNumber(){
        return this.number;
    }
}
