package simulator;

import static java.lang.Math.sqrt;
import static java.lang.Math.min;

public class Position {
    // «««Runtime State»»»
    private double x;
    private double y;

    // «««Constructors»»»
    public Position() {
        this(0.0, 0.0);
    }
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }



    // «««Basic Getters»»»
    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }



    // «««Action Methods»»»
    public boolean moveTo(Position destination, double speed, double deltaTime) {
        if (destination == null || speed <= 0.0 || deltaTime <= 0.0) { // invalid params
            return false;
        }

        // Calculate distance to destination
        double dx = destination.x - this.x;
        double dy = destination.y - this.y;
        double distance = sqrt(dx * dx + dy * dy);

        if (distance == 0.0) { // already at the destination
            return false;
        }

        // If distance is shorter than displacement's magnitude stop at the destination
        double magnitude = min(speed * deltaTime, distance);

        // (dx, dy) / distance gives a unit vector which multiplied by the magnitude makes the displacement vector
        double inverseDistance = 1.0 / distance;
        this.x += magnitude * dx * inverseDistance;
        this.y += magnitude * dy * inverseDistance;

        return true;
    }
}
