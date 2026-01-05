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
     * @return {@code true} if the engine is running (rotational speed is at or
     * above idle).
     */
    public boolean isRunning() {
        return rpm >= RPM_IDLE;
    }

    /**
     * @return The maximum allowable rotational speed for this engine.
     */
    public double getMaxRpm() {
        return maxRpm;
    }

    /**
     * @return The current rotational speed.
     */
    public double getRpm() {
        return rpm;
    }
    // endregion

    // region ⮞ Control Methods

    /**
     * Simulates engine ignition by setting the rotational speed to the idle
     * value.
     */
    public boolean start(boolean torqueTransferActive) throws TorqueTransferActiveException {
        if (isRunning()) return false;
        if (torqueTransferActive) throw new TorqueTransferActiveException();

        rpm = RPM_IDLE;

        return true;
    }

    /**
     * Cuts the engine power, setting the rotational speed to {@code 0.0}.
     */
    public boolean stop() {
        if (!isRunning()) return false;

        rpm = 0.0;

        return true;
    }

    /**
     * Increases the rotational speed by defined step, not surpassing the
     * allowable maximum for this engine.
     */
    public boolean increaseRpm() {
        if (!isRunning()) return false;

        rpm += RPM_STEP;
        rpm = Math.min(rpm, maxRpm);

        return true;
    }

    /**
     * Decreases the rotational speed by defined step. Can result in engine
     * stall if RPM falls below the idle threshold.
     */
    public boolean decreaseRpm() throws EngineStalledException {
        if (!isRunning()) return false;

        rpm -= RPM_STEP;

        if (rpm < RPM_IDLE) {
            rpm = 0.0;
            throw new EngineStalledException();
        }

        return true;
    }

    /**
     * Synchronizes the rotational speed with a new gear ratio.
     * <p>
     * Simulates mechanical load by dropping rotational speed during upshifts
     * and creating a surge during downshifts.
     * <p>
     * Does not surpass the allowable maximum for this engine. Can result in
     * engine stall if RPM falls below the idle threshold.
     *
     * @param gearShiftDelta the difference between the previous and current
     *                       gear index
     */
    public boolean adjustRpmAfterGearChange(int gearShiftDelta) throws EngineStalledException {
        if (!isRunning()) return false;

        rpm += gearShiftDelta * RPM_IDLE;
        rpm = Math.min(rpm, maxRpm);

        if (rpm < RPM_IDLE) {
            rpm = 0.0;
            throw new EngineStalledException();
        }

        return true;
    }
    // endregion

    // region ⮞ Display Methods

    public @NotNull String getEngineStateDisplay() {
        return isRunning() ? UI_STATE_ON : UI_STATE_OFF;
    }

    public @NotNull String getMaxRpmDisplay() {
        return String.format(UI_FORMAT_RPM, maxRpm);
    }

    public @NotNull String getRpmDisplay() {
        return String.format(UI_FORMAT_RPM, rpm);
    }
    // endregion
}
