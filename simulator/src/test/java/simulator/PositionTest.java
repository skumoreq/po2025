package simulator;

import static org.junit.Assert.*;

public class PositionTest {
    @org.junit.Test public void moveTo() {
        // Check for default values x=0, y=0
        Position position = new Position();
        assertEquals(0.0, position.getX(), 0.0);
        assertEquals(0.0, position.getY(), 0.0);

        // Check if constructor works correctly
        Position destination = new Position(100.0, 100.0);
        assertEquals(100.0, destination.getX(), 0.0);
        assertEquals(100.0, destination.getY(), 0.0);

        // Check if invalid params give no displacement
        assertFalse(position.moveTo(null, 1.0, 1.0));
        assertEquals(0.0, position.getX(), 0.0);
        assertEquals(0.0, position.getY(), 0.0);

        assertFalse(position.moveTo(destination, -1.0, -1.0));
        assertEquals(0.0, position.getX(), 0.0);
        assertEquals(0.0, position.getY(), 0.0);

        assertFalse(position.moveTo(destination, 0.0, 1.0));
        assertEquals(0.0, position.getX(), 0.0);
        assertEquals(0.0, position.getY(), 0.0);

        assertFalse(position.moveTo(destination, 1.0, 0.0));
        assertEquals(0.0, position.getX(), 0.0);
        assertEquals(0.0, position.getY(), 0.0);

        // Actual displacement tests
        assertTrue(position.moveTo(destination, 1.0, 0.5));
        assertEquals(0.353553, position.getX(), 1e-6);
        assertEquals(0.353553, position.getY(), 1e-6);

        assertTrue(position.moveTo(destination, 2.0, 0.5));
        assertEquals(1.060660, position.getX(), 1e-6);
        assertEquals(1.060660, position.getY(), 1e-6);

        assertTrue(position.moveTo(destination, 1.0, 100.0));
        assertEquals(71.771338, position.getX(), 1e-6);
        assertEquals(71.771338, position.getY(), 1e-6);

        // Check if position stops at destination
        assertTrue(position.moveTo(destination, 100.0, 1.0));
        assertEquals(100.0, position.getX(), 0.0);
        assertEquals(100.0, position.getY(), 0.0);

        // Check if no further displacement allowed
        assertFalse(position.moveTo(destination, 1.0, 1.0));
        assertEquals(100.0, position.getX(), 0.0);
        assertEquals(100.0, position.getY(), 0.0);
    }
}
