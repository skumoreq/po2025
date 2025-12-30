package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a point in 2D space with x and y coordinates.
 * <p>
 * Provides basic vector operations such as scaling, addition, distance, angle
 * calculation, and moving towards another point based on speed and time interval.
 * </p>
 */
public class Point {

    // region > Instance Fields

    private double x;
    private double y;
    // endregion

    // region > Initialization

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Point(@NotNull Point point) {
        this(point.x, point.y);
    }
    public Point() {
        this(0.0, 0.0);
    }
    // endregion

    // region > Getters and Setters

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    // endregion

    // region > Display Methods

    public String getPointDisplay() {
        return String.format("x: %.0f\ny: %.0f", x, y);
    }
    // endregion

    // region > Vector Algebra Methods

    /** @return true if this point has the same coordinates as the given point */
    public boolean equals(@NotNull Point point) {
        return x == point.x && y == point.y;
    }

    /** @return Euclidean distance from the origin (0,0) to this point */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }
    /** @return angle in degrees between this point and the given point */
    public double angleTo(@NotNull Point point) {
        return Math.toDegrees(Math.atan2(point.y - y, point.x - x));
    }

    /** Multiplies this point’s coordinates by the given scalar. */
    public void scale(double scalar) {
        x *= scalar;
        y *= scalar;
    }

    /** Adds the coordinates of the given point to this point. */
    public void add(@NotNull Point point) {
        x += point.x;
        y += point.y;
    }

    /**
     * Moves this point towards the target point by a distance determined
     * from the speed (km/h) and elapsed time (ms).
     * <p>
     * Movement is capped to avoid overshooting the target.
     * </p>
     *
     * @param target the point to move towards
     * @param speed speed in kilometers per hour
     * @param interval elapsed time in milliseconds
     * @param distanceScale optional multiplier for the distance
     */
    public void moveTo(@NotNull Point target, double speed, double interval, double distanceScale) {
        if (equals(target) || speed <= 0.0 || interval <= 0.0) return;

        Point direction = new Point(target.x - x, target.y - y);

        double distanceToTarget = direction.length();
        double distanceToMove = Math.min(distanceScale * speed * interval / 3600.0, distanceToTarget);

        direction.scale(distanceToMove / distanceToTarget);
        add(direction);
    }
    /** @see #moveTo(Point, double, double, double)  */
    public void moveTo(@NotNull Point target, double speed, double interval) {
        moveTo(target, speed, interval, 1.0);
    }
    // endregion
}
