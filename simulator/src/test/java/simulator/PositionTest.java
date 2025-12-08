package simulator;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class PositionTest {
    @org.junit.Test
    public void moveTo() {
        // Check for default values x=0, y=0
        Position position = new Position();
        assertEquals(0.0, position.getX(), 0.0);
        assertEquals(0.0, position.getY(), 0.0);

        // Check if constructor works correctly
        Position destination = new Position(100.0, 100.0);
        assertEquals(100.0, destination.getX(), 0.0);
        assertEquals(100.0, destination.getY(), 0.0);

        // Check if speed=0 or deltaTime=0 gives no displacement
        position.moveTo(destination, 0.0, 100.0);
        assertEquals(0.0, position.getX(), 0.0);
        assertEquals(0.0, position.getY(), 0.0);
        position.moveTo(destination, 100.0, 0.0);
        assertEquals(0.0, position.getX(), 0.0);
        assertEquals(0.0, position.getY(), 0.0);

        // Actual displacement tests
        position.moveTo(destination, 1.0, 0.5);
        assertEquals(0.353553, position.getX(), 1e-6);
        assertEquals(0.353553, position.getY(), 1e-6);
        position.moveTo(destination, 0.5, 2.0);
        assertEquals(1.060660, position.getX(), 1e-6);
        assertEquals(1.060660, position.getY(), 1e-6);
        position.moveTo(destination, 100.0, 1.0);
        assertEquals(71.771338, position.getX(), 1e-6);
        assertEquals(71.771338, position.getY(), 1e-6);

        // Check if position stops at destination
        position.moveTo(destination, 1.0, 100.0);
        assertEquals(100.0, position.getX(), 0.0);
        assertEquals(100.0, position.getY(), 0.0);

        // Check if negative speed and deltaTime are not allowed
        assertThrows(IllegalArgumentException.class, () -> position.moveTo(destination, -1.0, 0.0));
        assertThrows(IllegalArgumentException.class, () -> position.moveTo(destination, 0.0, -1.0));
    }
}