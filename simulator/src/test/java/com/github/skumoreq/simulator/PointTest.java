package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    // region ⮞ Test Configuration

    // Random initialized with fixed seed for reproducibility.
    private static final Random TEST_RANDOM = new Random(2026L);

    private static double nextSignedUnitDouble() {
        return TEST_RANDOM.nextDouble(-1.0, 1.0);
    }

    private static final int TEST_RUNS = 500;
    private static final double TEST_DELTA = 1.0e-14;

    // Standard presets:
    // speed 216 km/h (60 m/s) over a 1000 ms (1 s) time interval
    // results in exactly 600-unit movement at 10.0 scale.
    private static final double DEFAULT_SPEED = 216.0;
    private static final long DEFAULT_INTERVAL = 1000L;
    private static final double DEFAULT_UNIT_SCALE = 10.0;
    private static final double EXPECTED_STEP = 600.0;

    private @NotNull Point point;
    private @NotNull Point target;

    @BeforeEach
    void setUp() {
        point = new Point();
        target = new Point();
    }

    private boolean performMove(@NotNull Point point, double speed, long interval, double unitScale) {
        return point.moveTowards(target, speed, interval, unitScale);
    }

    private boolean performDefaultMove(@NotNull Point point) {
        return performMove(point, DEFAULT_SPEED, DEFAULT_INTERVAL, DEFAULT_UNIT_SCALE);
    }

    private void assertPointEquals(double expectedX, double expectedY, double delta) {
        assertAll(
                "Point coordinates",
                () -> assertEquals(expectedX, point.getX(), delta, "X coordinate mismatch"),
                () -> assertEquals(expectedY, point.getY(), delta, "Y coordinate mismatch")
        );
    }

    private void assertPointEquals(@NotNull Point expected, double delta) {
        assertPointEquals(expected.getX(), expected.getY(), delta);
    }
    // endregion

    // region ⮞ Contract & Validation

    @Test
    void moveTowards_throwsIllegalArgumentException() {
        assertAll(
                "Parameter validation",
                () -> assertThrows(IllegalArgumentException.class,
                        () -> performMove(point, -1.0, DEFAULT_INTERVAL, DEFAULT_UNIT_SCALE),
                        "Should throw for negative speed"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> performMove(point, DEFAULT_SPEED, -1L, DEFAULT_UNIT_SCALE),
                        "Should throw for negative interval"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> performMove(point, DEFAULT_SPEED, DEFAULT_INTERVAL, 0.0),
                        "Should throw for zero unitScale"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> performMove(point, DEFAULT_SPEED, DEFAULT_INTERVAL, -1.0),
                        "Should throw for negative unitScale")
        );
    }

    @Test
    void moveTowards_returnsFalse_alreadyAtTarget() {
        target.set(1.0, 1.0);
        point.set(1.0, 1.0);

        assertFalse(performDefaultMove(point));
    }

    @Test
    void moveTowards_returnsFalse_zeroSpeed() {
        target.set(1.0, 1.0);

        assertFalse(performMove(point, 0.0, DEFAULT_INTERVAL, DEFAULT_UNIT_SCALE));
    }

    @Test
    void moveTowards_returnsFalse_zeroInterval() {
        target.set(1.0, 1.0);

        assertFalse(performMove(point, DEFAULT_SPEED, 0L, DEFAULT_UNIT_SCALE));
    }

    @Test
    void moveTowards_returnsTrue_onSuccessfulMove() {
        target.set(1.0, 1.0);

        assertTrue(performDefaultMove(point));
    }
    // endregion

    // region ⮞ Directional Movement

    @RepeatedTest(TEST_RUNS)
    void moveTowards_positiveX() {
        double startX = nextSignedUnitDouble();

        target.setX(EXPECTED_STEP + 1.0);
        point.setX(startX);

        performDefaultMove(point);
        assertPointEquals(startX + EXPECTED_STEP, 0.0, TEST_DELTA);
    }

    @RepeatedTest(TEST_RUNS)
    void moveTowards_negativeX() {
        double startX = nextSignedUnitDouble();

        target.setX(-EXPECTED_STEP - 1.0);
        point.setX(startX);

        performDefaultMove(point);
        assertPointEquals(startX - EXPECTED_STEP, 0.0, TEST_DELTA);
    }

    @RepeatedTest(TEST_RUNS)
    void moveTowards_positiveY() {
        double startY = nextSignedUnitDouble();

        target.setY(EXPECTED_STEP + 1.0);
        point.setY(startY);

        performDefaultMove(point);
        assertPointEquals(0.0, startY + EXPECTED_STEP, TEST_DELTA);
    }

    @RepeatedTest(TEST_RUNS)
    void moveTowards_negativeY() {
        double startY = nextSignedUnitDouble();

        target.setY(-EXPECTED_STEP - 1.0);
        point.setY(startY);

        performDefaultMove(point);
        assertPointEquals(0.0, startY - EXPECTED_STEP, TEST_DELTA);
    }

    @ParameterizedTest
    @CsvSource({"1.0, 1.0", "1.0, -1.0", "-1.0, -1.0", "-1.0, 1.0"})
    void moveTowards_diagonal(double signX, double signY) {
        target.setX(signX * EXPECTED_STEP);
        target.setY(signY * EXPECTED_STEP);

        performDefaultMove(point);

        // Point moves diagonally (45°) towards a target.
        // The total distance moved must be EXPECTED_STEP.
        // Therefore, each coordinate moves by EXPECTED_STEP / √2.
        double expectedCoordinateStep = EXPECTED_STEP / Math.sqrt(2.0);

        assertPointEquals(signX * expectedCoordinateStep, signY * expectedCoordinateStep, TEST_DELTA);
    }
    // endregion

    // region ⮞ Boundary Conditions & Stability

    @RepeatedTest(TEST_RUNS)
    void moveTowards_preventsOvershoot() {
        target.set(nextSignedUnitDouble(), nextSignedUnitDouble());

        performDefaultMove(point);
        assertPointEquals(target, 0.0);
    }

    @Test
    void moveTowards_maintainsPrecisionOverManySteps() {
        target.setX(EXPECTED_STEP + 1.0);
        point.set(nextSignedUnitDouble(), nextSignedUnitDouble());

        var referencePoint = new Point(point);

        performDefaultMove(referencePoint);

        for (int i = 0; i < DEFAULT_INTERVAL; i++)
            performMove(point, DEFAULT_SPEED, 1L, DEFAULT_UNIT_SCALE);

        // Multiple small additions in the loop can lead to floating-point drift.
        // A delta of 1.0e-7 ensures the test remains stable while maintaining high precision.
        assertPointEquals(referencePoint, 1.0e-7);
    }
    // endregion
}
