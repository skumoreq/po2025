package simulator;

import static org.junit.Assert.*;

public class PositionTest {

    @org.junit.Test
    public void moveTo() {
        Position position = new Position(0.0, 0.0);
        Position destination = new Position(1.0, 1.0);

        position.moveTo(destination, 0.0, 100.0);
        assertEquals(0.0, position.getX(), 1e-6);
        assertEquals(0.0, position.getY(), 1e-6);

        position.moveTo(destination, 100.0, 0.0);
        assertEquals(0.0, position.getX(), 1e-6);
        assertEquals(0.0, position.getY(), 1e-6);

        position.moveTo(destination, 1.0, 0.5);
        assertEquals(0.353553, position.getX(), 1e-6);
        assertEquals(0.353553, position.getY(), 1e-6);

        position.moveTo(destination, 0.5, 1.0);
        assertEquals(0.707106, position.getX(), 1e-6);
        assertEquals(0.707106, position.getY(), 1e-6);

        position.moveTo(destination, -1.0, 1.0);
        assertEquals(0.0, position.getX(), 1e-6);
        assertEquals(0.0, position.getY(), 1e-6);

        position.moveTo(destination, 100.0, 1.0);
        assertEquals(1.0, position.getX(), 1e-6);
        assertEquals(1.0, position.getY(), 1e-6);

        position.moveTo(destination, 1.0, 100.0);
        assertEquals(1.0, position.getX(), 1e-6);
        assertEquals(1.0, position.getY(), 1e-6);
    }
}