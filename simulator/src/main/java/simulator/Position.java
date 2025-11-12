package simulator;

import static java.lang.Math.*;

public class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void moveTo(Position destination, double speed, double deltaTime) {
        double displacement = speed * deltaTime;

        double deltaX = destination.x - this.x;
        double deltaY = destination.y - this.y;
        double deltaXY = sqrt(deltaX*deltaX + deltaY*deltaY);

        displacement = min(displacement, deltaXY);

        if (displacement == 0) return;

        double invDeltaXY = 1 / deltaXY;
        double stepX = displacement * deltaX * invDeltaXY;
        double stepY = displacement * deltaY * invDeltaXY;

        this.x += stepX;
        this.y += stepY;
    }
}
