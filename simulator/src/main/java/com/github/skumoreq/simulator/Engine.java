package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a vehicle engine component.</p>
 * <p>The engine tracks current rpm, supports increasing and decreasing rpm, and
 * enforces a maximum rpm limit.</p>
 */
public class Engine extends Component{

    // region > Class Constants

    public static final double RPM_IDLE = 800.0;
    public static final double RPM_STEP = 100.0;
    // endregion

    // region > Instance Identity

    private final double maxRpm;
    // endregion

    // region > Instance State

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

    public void start() {
        rpm = RPM_IDLE;
    }
    public void stop() {
        rpm = 0.0;
    }
    public void increaseRpm() {
        rpm = Math.min(rpm + RPM_STEP, maxRpm);
    }
    public void decreaseRpm() {
        rpm -= RPM_STEP;
    }
    public void adjustRpmAfterGearChange(int previousGear, int gear) {
        rpm += (previousGear - gear) * RPM_IDLE;
    }
    // endregion
}