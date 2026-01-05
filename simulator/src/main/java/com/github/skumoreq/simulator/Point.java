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

    public void set(@NotNull Point point) {
        set(point.x, point.y);
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
     * This method computes the travel distance in meters (using {@code speed /
     * 3600.0 * interval}) before applying the unit scale. Movement is capped to
     * the remaining distance to avoid overshooting the target.
     * <p>
     * The method returns early without moving if the speed or interval are
     * non-positive, or if the point is already at the target.
     *
     * @param target    the point to move towards
     * @param speed     speed in kilometers per hour
     * @param interval  elapsed time in milliseconds
     * @param unitScale the conversion factor from meters to units
     */
    public boolean moveTowards(@NotNull Point target, double speed, double interval, double unitScale) {
        if (equals(target) || speed <= 0.0 || interval <= 0.0) return false;

        Point direction = new Point(target.x - x, target.y - y);

        double distanceToTarget = direction.length();
        double distanceToMove = Math.min(unitScale * speed * interval / 3600.0, distanceToTarget);

        direction.scale(distanceToMove / distanceToTarget);
        add(direction);

        return true;
    }
    // endregion
}
