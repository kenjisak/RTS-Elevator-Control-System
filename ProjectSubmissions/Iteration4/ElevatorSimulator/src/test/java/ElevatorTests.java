import allSystems.Elevator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
public class ElevatorTests {
    Elevator elevator;
    @BeforeEach
    public void setUp(){
        elevator = new Elevator(1);
    }
    @AfterEach
    public void cleanUp(){
        elevator.getUdp().closePorts();
    }
    /**
     * Elevator.java tests
     */
    @Test
    @DisplayName("Tests the elevator constructor in Elevator.java")
    void testElevatorConstructor() {
        assertNotNull(elevator);
        assertEquals(1, elevator.getCarNum());
    }

    @Test
    @DisplayName("Tests the moveUp() method in Elevator.java")
    void testMoveUp() {
        elevator.moveUp(1);
        assertEquals( 1, elevator.getCurrFloor());
    }

    @Test
    @DisplayName("Tests moveDown() method in Elevator.java")
    void testMoveDown() {
        elevator.moveUp(1);// Move up first to make sure elevator is not at ground floor
        assertEquals( 1, elevator.getCurrFloor());

        elevator.moveDown(1);
        assertEquals(0, elevator.getCurrFloor());
    }
}
