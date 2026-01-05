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

class PointTest {

    // region ⮞ Test Configuration

    // Random initialized with fixed seed for reproducibility.
    private static final Random TEST_RANDOM = new Random(2026L);

    private static final int TEST_RUNS = 200;
    private static final double TEST_DELTA = 1.0e-14;

    // Standard presets: Speed 18 km/h (5 m/s) over a 1000ms (1s) time interval
    // results in exactly 5 units of movement at 1.0 scale.
    private static final double TEST_SCALE = 1.0;
    private static final double TEST_SPEED = 18.0;
    private static final double TEST_INTERVAL = 1000.0;
    private static final double EXPECTED_STEP = 5.0;

    private Point point;
    private Point target;

    @BeforeEach
    void setUp() {
        point = new Point();
        target = new Point();
    }

    private double randomInRange(double min, double max) {
        return min + (max - min) * TEST_RANDOM.nextDouble();
    }

    private void moveTowardsTarget(@NotNull Point point, double speed, double interval) {
        point.moveTowards(target, speed, interval, TEST_SCALE);
    }

    private void moveWithPresets(@NotNull Point point) {
        moveTowardsTarget(point, TEST_SPEED, TEST_INTERVAL);
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

    // region ⮞ Axis-Aligned Movement

    @RepeatedTest(TEST_RUNS)
    void moveTowards_positiveXMovement() {
        double pointX = randomInRange(0.0, 1.0);

        target.setX(EXPECTED_STEP + 1.0);
        point.setX(pointX);

        moveWithPresets(point);
        assertPointEquals(pointX + EXPECTED_STEP, 0.0, TEST_DELTA);
    }

    @RepeatedTest(TEST_RUNS)
    void moveTowards_negativeXMovement() {
        double pointX = randomInRange(-1.0, 0.0);

        target.setX(-EXPECTED_STEP - 1.0);
        point.setX(pointX);

        moveWithPresets(point);
        assertPointEquals(pointX - EXPECTED_STEP, 0.0, TEST_DELTA);
    }

    @RepeatedTest(TEST_RUNS)
    void moveTowards_positiveYMovement() {
        double pointY = randomInRange(0.0, 1.0);

        target.setY(EXPECTED_STEP + 1.0);
        point.setY(pointY);

        moveWithPresets(point);
        assertPointEquals(0.0, pointY + EXPECTED_STEP, TEST_DELTA);
    }

    @RepeatedTest(TEST_RUNS)
    void moveTowards_negativeYMovement() {
        double pointY = randomInRange(-1.0, 0.0);

        target.setY(-EXPECTED_STEP - 1.0);
        point.setY(pointY);

        moveWithPresets(point);
        assertPointEquals(0.0, pointY - EXPECTED_STEP, TEST_DELTA);
    }
    // endregion

    // region ⮞ Diagonal Movement

    @ParameterizedTest
    @CsvSource({"1.0, 1.0", "1.0, -1.0", "-1.0, -1.0", "-1.0, 1.0"})
    void moveTowards_diagonalMovement(double signX, double signY) {
        target.setX(signX * EXPECTED_STEP);
        target.setY(signY * EXPECTED_STEP);

        moveWithPresets(point);

        // Origin moves towards a diagonal target. The total distance moved must
        // be 5 units. Therefore, each coordinate moves by: 5 / sqrt(2)
        assertPointEquals(signX * EXPECTED_STEP / Math.sqrt(2.0), signY * EXPECTED_STEP / Math.sqrt(2.0), TEST_DELTA);
    }
    // endregion

    // region ⮞ Edge Cases

    @RepeatedTest(TEST_RUNS)
    void moveTowards_targetOverlap() {
        target.set(randomInRange(-1.0, 1.0), randomInRange(-1.0, 1.0));
        point.set(target);

        Point point1 = new Point(point);

        moveWithPresets(point);
        assertPointEquals(point1, 0.0);
    }

    @RepeatedTest(TEST_RUNS)
    void moveTowards_speedInvalid() {
        double speed = randomInRange(-TEST_SPEED, 0.0);

        target.setX(EXPECTED_STEP);

        Point point1 = new Point(point);

        moveTowardsTarget(point, speed, TEST_INTERVAL);
        assertPointEquals(point1, 0.0);
    }

    @RepeatedTest(TEST_RUNS)
    void moveTowards_intervalInvalid() {
        double interval = randomInRange(-TEST_INTERVAL, 0.0);

        target.setX(EXPECTED_STEP);

        Point point1 = new Point(point);

        moveTowardsTarget(point, TEST_SPEED, interval);
        assertPointEquals(point1, 0.0);
    }
    // endregion

    // region ⮞ Overshoot Control

    @RepeatedTest(TEST_RUNS)
    void moveTowards_shouldNotOvershootTarget() {
        target.set(randomInRange(-1.0, 1.0), randomInRange(-1.0, 1.0));

        moveWithPresets(point);
        assertPointEquals(target, 0.0);
    }
    // endregion

    // region ⮞ Numerical Stability

    @Test
    void moveTowards_stableOverManySteps() {
        target.setX(EXPECTED_STEP + 1.0);
        point.set(randomInRange(-1.0, 1.0), randomInRange(-1.0, 1.0));

        Point point1 = new Point(point);

        moveWithPresets(point1);

        for (int i = 0; i < (int) TEST_INTERVAL; i++) {
            moveTowardsTarget(point, TEST_SPEED, 1.0);
        }

        // Multiple small additions in the loop can lead to floating-point drift.
        // A delta of 1e-7 ensures the test remains stable while maintaining high precision.
        assertPointEquals(point1, 1e-7);
    }
    // endregion
}
