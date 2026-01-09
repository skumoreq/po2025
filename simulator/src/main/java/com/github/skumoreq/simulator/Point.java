package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a point in 2D space with x and y coordinates.
 * <p>
 * Provides vector operations such as scaling, addition, distance and angle
 * calculation, moving towards another point based on speed and time interval.
 */
public class Point {

    // region ⮞ Instance Fields

    private double x;
    private double y;
    // endregion

    // region ⮞ Initialization

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

    // region ⮞ Getters & Setters

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

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
    // endregion

    // region ⮞ Vector Operations

    /**
     * @return {@code true} if this point has the same coordinates as the given
     * point.
     */
    public boolean equals(@NotNull Point point) {
        return x == point.x && y == point.y;
    }

    /**
     * @return Euclidean distance from the origin {@code (0,0)} to this point.
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * @return Squared Euclidean distance between this point and the given coordinates.
     */
    public double squaredDistanceTo(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;

        return dx * dx + dy * dy;
    }

    /**
     * @return Angle in degrees between this point and the given point, or
     * {@code Double.NaN} if the points overlap.
     */
    public double angleTo(@NotNull Point point) {
        if (equals(point)) return Double.NaN;

        return Math.toDegrees(Math.atan2(point.y - y, point.x - x));
    }

    /**
     * Adds the coordinates of the given point to this point.
     */
    public void add(@NotNull Point point) {
        x += point.x;
        y += point.y;
    }

    /**
     * Multiplies this point’s coordinates by the given scalar.
     */
    public void scale(double scalar) {
        x *= scalar;
        y *= scalar;
    }

    /**
     * Moves this point towards the target point by a distance determined from
     * the speed (km/h) and elapsed time (ms).
     * <p>
     * This method computes the travel distance in meters (using {@code speed *
     * interval / 3600.0}) before applying the unit scale. Movement is capped to
     * the remaining distance to avoid overshooting the target.
     *
     * @param target    the point to move towards
     * @param speed     speed in kilometers per hour (non-negative)
     * @param interval  elapsed time in milliseconds (non-negative)
     * @param unitScale the conversion factor from meters to units (positive)
     *
     * @return {@code true} if the point moved.
     * @throws IllegalArgumentException if speed or interval is negative, or if
     * unitScale is non-positive.
     */
    public boolean moveTowards(@NotNull Point target, double speed, long interval, double unitScale) {
        if (speed < 0.0)
            throw new IllegalArgumentException("Speed cannot be negative: %.2f km/h".formatted(speed));
        if (interval < 0L)
            throw new IllegalArgumentException("Time interval cannot be negative: %d ms".formatted(interval));
        if (unitScale <= 0.0)
            throw new IllegalArgumentException("Unit scale must be positive: %.2f".formatted(unitScale));

        if (equals(target) || speed == 0.0 || interval == 0L) return false;

        var direction = new Point(target.x - x, target.y - y);

        double distanceToTarget = direction.length();
        double distanceToMove = Math.min(unitScale * speed * interval / 3600.0, distanceToTarget);

        direction.scale(distanceToMove / distanceToTarget);
        add(direction);

        return true;
    }
    // endregion
}
