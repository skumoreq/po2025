package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a point in 2D space with x and y coordinates.</p>
 * <p>Provides basic vector operations such as scaling, addition, distance, angle calculation,
 * and moving towards another point with a specified speed over a time interval.</p>
 */
public class Point {

    // region > Instance State

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

    public boolean equals(@NotNull Point point) {
        return x == point.x && y == point.y;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }
    public double angleTo(@NotNull Point point) {
        return Math.toDegrees(Math.atan2(point.y - y, point.x - x));
    }

    public void scale(double scalar) {
        x *= scalar;
        y *= scalar;
    }
    public void add(@NotNull Point point) {
        x += point.x;
        y += point.y;
    }
    /**
     * <p>Moves this point towards the target point by a distance in meters
     * determined from the speed and elapsed time.</p>
     * <p>The movement is constrained so that the point will not overshoot the target.</p>
     *
     * @param target point to move towards
     * @param speed in kilometers per hour
     * @param interval in milliseconds
     */
    public void moveTo(@NotNull Point target, double speed, double interval) {
        if (equals(target) || speed <= 0.0 || interval <= 0.0) return;

        Point direction = new Point(target.x - x, target.y - y);

        double distanceToTarget = direction.length();
        double distanceToMove = Math.min(speed * interval / 3600.0, distanceToTarget);

        direction.scale(distanceToMove / distanceToTarget);
        add(direction);
    }
    // endregion
}