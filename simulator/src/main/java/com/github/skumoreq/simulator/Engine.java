package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an engine component.
 * <p>
 * Handles engine RPM behavior including starting, stopping,
 * increasing, decreasing, and adjusting RPM after gear changes.
 * </p>
 */
public class Engine extends Component{

    // region > Constants

    public static final double RPM_IDLE = 800.0;
    public static final double RPM_STEP = 100.0;
    // endregion

    // region > Instance Fields

    private final double maxRpm;

    private double rpm;
    // endregion

    // region > Initialization

    public Engine(String name, double weight, double price, double maxRpm) {
        super(name, weight, price);
        this.maxRpm = maxRpm;

        rpm = 0.0;
    }
    public Engine(@NotNull Engine engine) {
        this(engine.getName(), engine.getWeight(), engine.getPrice(), engine.maxRpm);
    }
    // endregion

    // region > Getters

    public double getMaxRpm() {
        return maxRpm;
    }
    public double getRpm() {
        return rpm;
    }
    // endregion

    // region > Display Methods

    public String getMaxRpmDisplay() {
        return String.format("%.0f RPM", maxRpm);
    }
    public String getRpmDisplay() {
        return String.format("%.0f RPM%s", rpm, (rpm > maxRpm * 0.9) ? " ⚠" : "");
    }
    // endregion

    // region > Control Methods

    /** Sets the engine RPM to the {@link #RPM_IDLE} value. */
    public void start() {
        rpm = RPM_IDLE;
    }
    /** Stops the engine by setting RPM to 0. */
    public void stop() {
        rpm = 0.0;
    }

    /** Increases the RPM by a fixed {@link #RPM_STEP}, not exceeding {@link #maxRpm}. */
    public void increaseRpm() {
        rpm = Math.min(rpm + RPM_STEP, maxRpm);
    }
    /** Decreases the RPM by a fixed {@link #RPM_STEP}. */
    public void decreaseRpm() {
        rpm -= RPM_STEP;
    }

    /**
     * Adjusts RPM after a gear change by adding (previousGear - gear) × {@link #RPM_IDLE}.
     * <p>
     * Increasing gear lowers RPM; decreasing gear raises RPM proportionally.
     * </p>
     */
    public void adjustRpmAfterGearChange(int previousGear, int gear) {
        rpm += (previousGear - gear) * RPM_IDLE;
    }
    // endregion
}
