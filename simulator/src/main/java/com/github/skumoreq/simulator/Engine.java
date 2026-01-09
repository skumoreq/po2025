package com.github.skumoreq.simulator;

import com.github.skumoreq.simulator.exception.EngineStalledException;
import com.github.skumoreq.simulator.exception.TorqueTransferActiveException;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an engine car component.
 * <p>
 * Responsible for managing the rotational speed (RPM), simulating engine
 * ignition states, and handling RPM adjustments during gear transitions.
 *
 * @see Clutch
 * @see Transmission
 */
public class Engine extends CarComponent {

    // region ⮞ Constants

    private static final String UI_STATE_ON = "Włączony";
    private static final String UI_STATE_OFF = "Wyłączony";
    private static final String UI_FORMAT_RPM = "%.0f obr./min";

    private static final double RPM_IDLE = 800.0;
    private static final double RPM_STEP = 100.0;
    // endregion

    // region ⮞ Instance Fields

    private final double maxRpm;
    private double rpm;
    // endregion

    // region ⮞ Initialization

    public Engine(@NotNull String name, double weight, double price, double maxRpm) {
        super(name, weight, price);
        this.maxRpm = maxRpm;

        rpm = 0.0;
    }

    /**
     * Creates a new Engine based on an existing instance.
     */
    public Engine(@NotNull Engine engine) {
        this(engine.name, engine.weight, engine.price, engine.maxRpm);
    }
    // endregion

    // region ⮞ Getters

    /**
     * @return {@code true} if the engine is running (RPM is at or above idle).
     */
    public boolean isRunning() {
        return rpm >= RPM_IDLE;
    }

    /**
     * @return The maximum allowable RPM for this engine.
     */
    public double getMaxRpm() {
        return maxRpm;
    }

    /**
     * @return The current RPM.
     */
    public double getRpm() {
        return rpm;
    }
    // endregion

    // region ⮞ Helper Methods

    /**
     * Verifies if the current RPM is sufficient to keep the engine running.
     * If RPM falls below the idle threshold, the engine is forced to stop.
     */
    private void checkEngineStall() throws EngineStalledException {
        if (rpm < RPM_IDLE) {
            rpm = 0.0;
            throw new EngineStalledException();
        }
    }
    // endregion

    // region ⮞ Control Methods

    /**
     * Simulates engine ignition by setting the RPM to the idle value.
     */
    public boolean start(boolean torqueTransferActive) throws TorqueTransferActiveException {
        if (isRunning()) return false;
        if (torqueTransferActive) throw new TorqueTransferActiveException();

        rpm = RPM_IDLE;

        return true;
    }

    /**
     * Cuts the engine power, setting the RPM to {@code 0.0}.
     */
    public boolean stop() {
        if (!isRunning()) return false;

        rpm = 0.0;

        return true;
    }

    /**
     * Increases the RPM by a scaled step, not surpassing the allowable maximum
     * for this engine.
     *
     * @param intensity scaling factor from {@code 0.0} to {@code 1.0}
     */
    public boolean increaseRpm(double intensity) {
        if (intensity < 0.0)
            throw new IllegalArgumentException("Intensity cannot be negative: %.2f".formatted(intensity));

        if (!isRunning() || intensity == 0.0) return false;

        rpm += RPM_STEP * Math.min(intensity, 1.0);
        rpm = Math.min(rpm, maxRpm);

        return true;
    }

    /**
     * Decreases the RPM by a scaled step. Can result in engine stall if RPM
     * falls below the idle threshold.
     *
     * @param intensity scaling factor from {@code 0.0} to {@code 1.0}
     */
    public boolean decreaseRpm(double intensity) throws EngineStalledException {
        if (intensity < 0.0)
            throw new IllegalArgumentException("Intensity cannot be negative: %.2f".formatted(intensity));

        if (!isRunning() || intensity == 0.0) return false;

        rpm -= RPM_STEP * Math.min(intensity, 1.0);
        checkEngineStall();

        return true;
    }

    /**
     * Synchronizes the RPM with a new gear ratio.
     * <p>
     * Simulates mechanical load by dropping RPM during upshifts and creating a
     * surge during downshifts.
     * <p>
     * Does not surpass the allowable maximum for this engine. Can result in
     * engine stall if RPM falls below the idle threshold.
     *
     * @param gearShiftDelta the difference between the current and previous
     *                       gear index
     */
    public boolean adjustRpmAfterGearChange(int gearShiftDelta, double dropFactor) throws EngineStalledException {
        if (!isRunning()) return false;

        rpm *= Math.pow(dropFactor, gearShiftDelta);
        rpm = Math.min(rpm, maxRpm);
        checkEngineStall();

        return true;
    }
    // endregion

    // region ⮞ Display Methods

    public @NotNull String getEngineStateDisplay() {
        return isRunning() ? UI_STATE_ON : UI_STATE_OFF;
    }

    public @NotNull String getMaxRpmDisplay() {
        return UI_FORMAT_RPM.formatted(maxRpm);
    }

    public @NotNull String getRpmDisplay() {
        return UI_FORMAT_RPM.formatted(rpm);
    }
    // endregion
}
