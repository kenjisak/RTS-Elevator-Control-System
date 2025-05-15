package ButtonPackage;

public class FloorButton extends Button{//data structure for Elevator/Floor to use when sending Requests when "These are pressed"
    private final int flrNum;
    private final String direction;

    /**
     *
     * @param flrNum floor number at which this button is located
     * @param direction direction of the arrow of this button; "up" or "down"
     */
    public FloorButton(int flrNum, String direction){
        this.flrNum = flrNum;
        this.direction = direction.toLowerCase();
    }
    public int getFlrNum(){
        return this.flrNum;
    }
    public String getDirection(){
        return this.direction;
    }
}
