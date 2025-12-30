package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit 5 tests for {@link Point#moveTo(Point, double, double)}.
 * <p>
 * Verifies correct movement behavior including linear and diagonal motion,
 * early-return conditions, overshoot prevention, and numerical stability
 * across repeated small steps.
 * </p>
 * <p>
 * Most tests assume that 18 km/h == 5 m/s, resulting in a 5 m movement over
 * a 1000 ms interval.
 * </p>
 */
class PointTest {

    private final Random random = new Random(2026L); // fixed seed for reproducibility

    private Point point;
    private Point target;

    @BeforeEach
    void setUp() {
        point = new Point();
        target = new Point();
    }

    // region > Helper Methods

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
        assertPointEquals(pointX + 5.0, 0.0, 1.0E-14);
    }
    @RepeatedTest(500)
    void moveTo_linearMovementNegativeX() {
        double pointX = randomInRange(-1.0, 0.0);

        target.setX(-10.0);
        point.setX(pointX);

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(pointX - 5.0, 0.0, 1.0E-14);
    }
    @RepeatedTest(500)
    void moveTo_linearMovementPositiveY() {
        double pointY = randomInRange(0.0, 1.0);

        target.setY(10.0);
        point.setY(pointY);

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(0.0, pointY + 5.0, 1.0E-14);
    }
    @RepeatedTest(500)
    void moveTo_linearMovementNegativeY() {
        double pointY = randomInRange(-1.0, 0.0);

        target.setY(-10.0);
        point.setY(pointY);

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(0.0, pointY - 5.0, 1.0E-14);
    }
    // endregion

    // region > Diagonal Movement Tests

    @ParameterizedTest
    @CsvSource({"5.0,5.0","5.0,-5.0","-5.0,-5.0","-5.0,5.0"})
    void moveTo_diagonalMovement(double targetX, double targetY) {
        target.setX(targetX);
        target.setY(targetY);

        point.moveTo(target, 18.0, 1000.0);
        assertPointEquals(targetX / Math.sqrt(2.0), targetY / Math.sqrt(2.0), 1.0E-14);
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
        assertPointEquals(copy, 1e-7);
    }
    // endregion
}
