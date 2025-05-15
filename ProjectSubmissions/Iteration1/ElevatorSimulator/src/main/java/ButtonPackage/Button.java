package ButtonPackage;

/**
 * Base class for all buttons
 */
public class Button {
    private boolean lamp = false; //false if off, true if on
    public Button (){
    }
    public boolean getLamp(){
        return this.lamp;
    }
}
