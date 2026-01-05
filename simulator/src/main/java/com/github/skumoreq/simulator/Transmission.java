package com.github.skumoreq.simulator;

import com.github.skumoreq.simulator.exception.ClutchEngagedException;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

/**
 * Represents the vehicle's transmission (gearbox) component.
 * <p>
 * Manages gear shifting logic and the connection to the wheels. It contains a
 * clutch instance to verify torque flow from the engine.
 * <p>
 * While in neutral, the wheels are disconnected from the transmission;
 * otherwise, torque is transferred through a set of predefined gear ratios.
 *
 * @see Clutch
 * @see Engine
 */
public class Transmission extends CarComponent {

    // region ⮞ Constants

    public static final String UI_SEPARATOR_RATIO = "|";
    public static final String UI_FORMAT_RATIO = "%.2f";
    private static final String UI_NEUTRAL_GEAR = "N";
    private static final String UI_SHIFT_UP = "↑";
    private static final String UI_SHIFT_DOWN = "↓";
    // endregion

    // region ⮞ Instance Fields

    private final @NotNull Clutch clutch;
    private final double @NotNull [] ratios;

    private int previousGear;
    private int gear;
    // endregion

    // region ⮞ Initialization

    public Transmission(
            @NotNull String name, double weight, double price,
            @NotNull Clutch clutch, double @NotNull [] ratios
    ) {
        super(name, weight, price);
        this.clutch = new Clutch(clutch);
        this.ratios = ratios.clone();

        previousGear = 0;
        gear = 0;
    }

    /**
     * Creates a new Transmission based on an existing instance.
     */
    public Transmission(@NotNull Transmission transmission) {
        this(transmission.name, transmission.weight, transmission.price, transmission.clutch, transmission.ratios);
    }
    // endregion

    // region ⮞ Getters

    /**
     * @return The transmission's clutch instance.
     */
    public @NotNull Clutch clutch() {
        return clutch;
    }

    /**
     * @return {@code true} if the wheels are disconnected from the transmission.
     */
    public boolean isInNeutral() {
        return gear == 0;
    }

    /**
     * @return {@code true} if the engine-wheels torque transfer isn't
     * interrupted by either the transmission being in neutral state or a
     * disengaged clutch.
     */
    public boolean isTorqueTransferred() {
        return !isInNeutral() && clutch.isEngaged();
    }

    /**
     * @return The total number of forward gears available in this transmission.
     */
    public int getGearCount() {
        return ratios.length;
    }

    /**
     * Positive value means downshifting, negative means upshifting.
     *
     * @return The number of gears changed in the last shift.
     */
    public int getGearShiftDelta() {
        return previousGear - gear;
    }

    /**
     * @return The ratio of the specified gear, regardless of clutch state.
     */
    public double getGearRatio(int gear) {
        if (gear <= 0 || gear > getGearCount()) return 0.0;
        return ratios[gear - 1];
    }

    /**
     * @return The effective torque ratio currently being transferred to the
     * wheels.
     */
    public double getEffectiveRatio() {
        return isTorqueTransferred() ? getGearRatio(gear) : 0.0;
    }
    // endregion

    // region ⮞ Control Methods

    /**
     * Updates the previous gear to the current gear.
     */
    public void updatePreviousGear() {
        previousGear = gear;
    }

    /**
     * Resets the previous gear to the neutral gear.
     */
    public void clearPreviousGear() {
        previousGear = 0;
    }

    /**
     * Shifts the gear up by one, if not already at the highest gear.
     */
    public boolean shiftUp() throws ClutchEngagedException {
        if (gear == getGearCount()) return false;
        if (clutch.isEngaged()) throw new ClutchEngagedException();

        gear++;

        return true;
    }

    /**
     * Shifts the gear down by one, if not already in the neutral gear.
     */
    public boolean shiftDown() throws ClutchEngagedException {
        if (gear == 0) return false;
        if (clutch.isEngaged()) throw new ClutchEngagedException();

        gear--;

        return true;
    }
    // endregion

    // region ⮞ Display Methods

    public @NotNull String getGearRatiosDisplay() {
        StringJoiner joiner = new StringJoiner(UI_SEPARATOR_RATIO);

        for (double ratio : ratios) {
            joiner.add(String.format(UI_FORMAT_RATIO, ratio));
        }

        return joiner.toString();
    }

    public @NotNull String getGearDisplay() {
        StringBuilder builder = new StringBuilder();

        builder.append(isInNeutral() ? UI_NEUTRAL_GEAR : String.valueOf(gear));

        if (!isInNeutral() && !clutch.isEngaged()) {
            if (gear > 1) builder.append(UI_SHIFT_DOWN);
            if (gear < getGearCount()) builder.append(UI_SHIFT_UP);
        }

        return builder.toString();
    }
    // endregion
}
