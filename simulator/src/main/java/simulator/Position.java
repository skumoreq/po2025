package simulator;

import static java.lang.Math.sqrt;
import static java.lang.Math.min;

public class Position {
    // «««Dynamic State»»»
    private double x;
    private double y;

    // «««Initialization»»»
    public Position() {
        this(0.0, 0.0);
    }
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }



    // «««Accessors»»»
    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }



    // «««Control Methods»»»
    public boolean moveTo(Position destination, double speed, double deltaTime) {
        // Check for invalid parameters
        if (destination == null || speed <= 0.0 || deltaTime <= 0.0) return false;

        // Check if already at destination
        double dx = destination.x - this.x;
        double dy = destination.y - this.y;
        double distance = sqrt(dx * dx + dy * dy);
        if (distance == 0.0) return false;

        // Move towards the destination by at most speed * deltaTime
        double magnitude = min(speed * deltaTime, distance);

        // Update coordinates along the direction to the destination
        double inverseDistance = 1.0 / distance;
        this.x += magnitude * dx * inverseDistance;
        this.y += magnitude * dy * inverseDistance;
        return true;
    }
}
