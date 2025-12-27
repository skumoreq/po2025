package com.github.skumoreq.simulator;

import java.util.Random;

import org.jetbrains.annotations.NotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h3>Using JUnit 5</h3>
 * <p>Unit tests for {@link Point#moveTo(Point, double, double)}.</p>
 * <p>The tests verify the following behavior:</p>
 * <ul>
 *   <li>Correct linear movement along the X and Y axes</li>
 *   <li>Correct diagonal movement</li>
 *   <li>Early return when:
 *     <ul>
 *       <li>speed is non-positive</li>
 *       <li>time interval is non-positive</li>
 *       <li>the point already equals the target</li>
 *     </ul>
 *   </li>
 *   <li>Prevention of overshooting the target position</li>
 *   <li>Stability over repeated small movements without significant rounding error</li>
 * </ul>
 * <p>Most movement tests rely on the fact that a speed of {@code 18 km/h}
 * equals exactly {@code 5 m/s}, which results in a movement of {@code 5 meters}
 * over a {@code 1000 ms} interval.</p>
 */
class PointTest {

    // region > Test Constants and Fields

    private static final double DELTA_EXACT = 1e-14;
    private static final double DELTA_APPROX = 1e-7;

    private final Random random = new Random(2026L); // fixed seed for reproducibility

    private Point point;
    private Point target;
    // endregion

    // region > Helper Methods

    @BeforeEach
    void setUp() {
        point = new Point();
        target = new Point();
    }

    private double randomInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private void assertPointEquals(double expectedX, double expectedY, double delta) {
        assertAll("Point coordinates",
                () -> assertEquals(expectedX, point.getX(), delta, "X coordinate mismatch"),
                () -> assertEquals(expectedY, point.getY(), delta, "Y coordinate mismatch"));
    }
    private void assertPointEquals(@NotNull Point expected, double delta) {
        assertPointEquals(expected.getX(), expected.getY(), delta);
    }
    // endregion

    // region > Linear Movement Tests

    @RepeatedTest(500)
    void moveTo_linearMovementPositiveX() {
        double pointX = randomInRange(0.0, 1.0);

        target.setX(10.0);
        point.setX(pointX);

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(pointX + 5.0, 0.0, DELTA_EXACT);
    }
    @RepeatedTest(500)
    void moveTo_linearMovementNegativeX() {
        double pointX = randomInRange(-1.0, 0.0);

        target.setX(-10.0);
        point.setX(pointX);

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(pointX - 5.0, 0.0, DELTA_EXACT);
    }
    @RepeatedTest(500)
    void moveTo_linearMovementPositiveY() {
        double pointY = randomInRange(0.0, 1.0);

        target.setY(10.0);
        point.setY(pointY);

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(0.0, pointY + 5.0, DELTA_EXACT);
    }
    @RepeatedTest(500)
    void moveTo_linearMovementNegativeY() {
        double pointY = randomInRange(-1.0, 0.0);

        target.setY(-10.0);
        point.setY(pointY);

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(0.0, pointY - 5.0, DELTA_EXACT);
    }
    // endregion

    // region > Diagonal Movement Tests

    @ParameterizedTest
    @CsvSource({"5.0,5.0","5.0,-5.0","-5.0,-5.0","-5.0,5.0"})
    void moveTo_diagonalMovement(double targetX, double targetY) {
        target.setX(targetX);
        target.setY(targetY);

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(targetX / Math.sqrt(2.0), targetY / Math.sqrt(2.0), DELTA_EXACT);
    }
    // endregion

    // region > Early Return Tests

    @RepeatedTest(500)
    void moveTo_samePoint_noMovement() {
        target.setX(randomInRange(-1.0, 1.0));
        target.setY(randomInRange(-1.0, 1.0));
        point.setX(target.getX());
        point.setY(target.getY());

        Point copy = new Point(point);

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(copy, 0.0);
    }
    @RepeatedTest(500)
    void moveTo_invalidSpeed_noMovement() {
        double speed = randomInRange(-18.0, 0.0);

        target.setX(10.0);
        point.setX(randomInRange(-1.0, 1.0));
        point.setY(randomInRange(-1.0, 1.0));

        Point copy = new Point(point);

        point.moveTo(target, speed, 1000.0);
        assertPointEquals(copy, 0.0);
    }
    @RepeatedTest(500)
    void moveTo_invalidInterval_noMovement() {
        double interval = randomInRange(-1000.0, 0.0);

        target.setX(10.0);
        point.setX(randomInRange(-1.0, 1.0));
        point.setY(randomInRange(-1.0, 1.0));

        Point copy = new Point(point);

        point.moveTo(target, 18.0, interval);
        assertPointEquals(copy, 0.0);
    }
    // endregion

    // region > Overshoot Prevention Tests

    @RepeatedTest(500)
    void moveTo_overshootPrevention() {
        target.setX(randomInRange(-1.0, 1.0));
        target.setY(randomInRange(-1.0, 1.0));

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(target, 0.0);
    }
    // endregion

    // region > Stability Over Many Steps Tests

    @Test
    void moveTo_stableOverManySteps() {
        target.setX(10.0);
        point.setX(randomInRange(-1.0, 1.0));
        point.setY(randomInRange(-1.0, 1.0));

        Point copy = new Point(point);
        copy.moveTo(target, 18.0, 1000.0);

        for (int i = 0; i < 1000; i++) point.moveTo(target, 18.0, 1.0);
        assertPointEquals(copy, DELTA_APPROX);
    }
    // endregion
}