import allSystems.Elevator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static otherResources.Constants.*;

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
     * This method is used to test the constructor of the Elevator class in Elevator.java.
     * It verifies that the elevator object is not null and that the car number matches the expected value.
     *
     * Preconditions:
     * - The elevator object is a valid instance of the Elevator class.
     *
     * Postconditions:
     * - The elevator object is not null.
     * - The car number of the elevator object matches the expected value.
     *
     * Test Case:
     * 1. Create an instance of the Elevator class with a car number of 1.
     * 2. Verify that the elevator object is not null.
     * 3. Verify that the car number of the elevator object is 1.
     *
     * Example Usage:
     * Elevator elevator = new Elevator(1);
     * assertNotNull(elevator);
     * assertEquals(1, elevator.getCarNum());
     */
    @Test
    @DisplayName("Tests the elevator constructor in Elevator.java")
    void testElevatorConstructor() {
        assertNotNull(elevator);
        assertEquals(1, elevator.getCarNum());
    }

    /**
     * This method is used to test the moveUp() method in the Elevator class.
     * It verifies that the current floor of the elevator is incremented correctly after moving up.
     *
     * Preconditions:
     * - The elevator object is a valid instance of the Elevator class.
     * - The elevator is initially on a specific floor.
     *
     * Postconditions:
     * - The current floor of the elevator is incremented by the specified travel time.
     *
     * Test Case:
     * 1. Move the elevator up with a specified travel time of 1.
     * 2. Verify that the current floor of the elevator is incremented by 1.
     *
     * Usage Example:
     * Elevator elevator = new Elevator(1);
     * elevator.moveUp(1);
     * assertEquals(1, elevator.getCurrFloor());
     */
    @Test
    @DisplayName("Tests the moveUp() method in Elevator.java")
    void testMoveUp() {
        elevator.moveUp(1);
        assertEquals( START_FLOOR + 1, elevator.getCurrFloor());
    }

    /**
     * This method is used to test the moveDown() method in the Elevator class.
     * It verifies that the current floor of the elevator is decremented correctly after moving down.
     *
     * Preconditions:
     * - The elevator object is a valid instance of the Elevator class.
     * - The elevator is initially on a higher floor.
     *
     * Postconditions:
     * - The current floor of the elevator is decremented by the specified travel time.
     *
     * Test Case:
     * 1. Move the elevator up to a higher floor to ensure it is not at the ground floor.
     * 2. Verify that the current floor of the elevator is initially higher than the ground floor.
     * 3. Move the elevator down with a specified travel time of 1.
     * 4. Verify that the current floor of the elevator is decremented by 1.
     *
     * Example Usage:
     * Elevator elevator = new Elevator(1);
     * elevator.moveUp(1);
     * assertEquals(1, elevator.getCurrFloor());
     *
     * elevator.moveDown(1);
     * assertEquals(0, elevator.getCurrFloor());
     */
    @Test
    @DisplayName("Tests moveDown() method in Elevator.java")
    void testMoveDown() {
        elevator.moveUp(1);// Move up first to make sure elevator is not at ground floor
        assertEquals( START_FLOOR + 1, elevator.getCurrFloor());

        elevator.moveDown(1);
        assertEquals(START_FLOOR, elevator.getCurrFloor());
    }
}
