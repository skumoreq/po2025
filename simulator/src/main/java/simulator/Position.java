package simulator;

import static java.lang.Math.sqrt;
import static java.lang.Math.min;

public class Position {
    private double x;
    private double y;

    // Constructors
    public Position() { this(0, 0); }
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Basic getters
    public double getX() { return this.x; }
    public double getY() { return this.y; }

    // Method moveTo calculates new coordinates of the position by calculating the displacment vector pointing towards
    // the destination; The magnitude of the vector is speed * deltaTime; Speed and deltaTime need to be a non-negative
    // values
    public void moveTo(Position destination, double speed, double deltaTime) {
        if (destination == null) {
            throw new IllegalArgumentException("'destination' parameter must not be null");
        }
        if (speed < 0.0) {
            throw new IllegalArgumentException("'speed' parameter must be non-negative, got: " + speed);
        }
        if (deltaTime < 0.0) {
            throw new IllegalArgumentException("'deltaTime' parameter must be non-negative, got: " + speed);
        }

        if (speed == 0.0 || deltaTime == 0.0) { return; } // magnitude is 0 so no displacement

        // Calculate distance to destination
        double dx = destination.x - this.x;
        double dy = destination.y - this.y;
        double distance = sqrt(dx * dx + dy * dy);

        if (distance == 0.0) { return; } // already at the destination

        // If distance is shorter than displacement's magnitude stop at the destination
        double magnitude = min(speed * deltaTime, distance);

        // (dx, dy) / distance gives us a unit vector which multiplied by the magnitude gets us the displacement vector
        double inverseDistance = 1.0 / distance;
        this.x += magnitude * dx * inverseDistance;
        this.y += magnitude * dy * inverseDistance;
    }
}
