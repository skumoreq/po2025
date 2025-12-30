package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a gearbox component.
 * <p>
 * Contains a clutch instance and gear ratios for each gear.
 * Stores the current and previous gear, allows shifting gears, and updates
 * the effective gear ratio.
 * </p>
 */
public class Gearbox extends Component {

    // region > Constants

    public static final int NUM_GEARS = 5;
    // endregion

    // region > Instance Fields

    private final Clutch clutch;
    private final double[] gearRatios;

    private int gear;
    private int previousGear;
    private double gearRatio;
    // endregion

    // region > Initialization

    public Gearbox(String name, double weight, double price, Clutch clutch, double[] gearRatios) {
        super(name, weight, price);
        this.clutch = new Clutch(clutch);
        this.gearRatios = gearRatios;

        gear = 0;
        previousGear = 0;
        gearRatio = 0.0;
    }
    public Gearbox(@NotNull Gearbox gearbox) {
        this(gearbox.getName(), gearbox.getWeight(), gearbox.getPrice(), gearbox.clutch, gearbox.gearRatios);
    }
    // endregion

    // region > Getters

    public Clutch getClutch() {
        return clutch;
    }
    public double[] getGearRatios() {
        return gearRatios;
    }
    public int getGear() {
        return gear;
    }
    public int getPreviousGear() {
        return previousGear;
    }
    public double getGearRatio() {
        return gearRatio;
    }
    // endregion

    // region > Display Methods

    public String getGearRatiosDisplay() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < gearRatios.length; i++) {
            stringBuilder.append(String.format("%.2f", gearRatios[i]));
            if (i != gearRatios.length - 1) stringBuilder.append("│");
        }
        return stringBuilder.toString();
    }
    public String getGearDisplay() {
        if (gear == 0) return "N";
        if (clutch.isEngaged()) return String.valueOf(gear);

        return switch (gear) {
            case 1 -> gear + "↑";
            case NUM_GEARS -> gear + "↓";
            default -> gear + "↑↓";
        };
    }
    // endregion

    // region > Helper Methods

    /** @return true if either the current gear is 0 or the clutch is disengaged */
    public boolean isInNeutral() {
        return gear == 0 || !clutch.isEngaged();
    }
    // endregion

    // region > Control Methods

    /** Updates the previous gear to the current gear. */
    public void updatePreviousGear() {
        previousGear = gear;
    }
    /** Resets the previous gear to neutral (0). */
    public void clearPreviousGear() {
        previousGear = 0;
    }

    /**
     * Updates the gear ratio to the appropriate value for the current gear.
     * Sets to 0.0 if the gearbox {@link #isInNeutral()}.
     */
    public void updateGearRatio() {
        gearRatio = isInNeutral() ? 0.0 : gearRatios[gear - 1];
    }

    /** Shifts the gear up by one, if not already at the highest gear. */
    public void shiftUp() {
        if (gear < NUM_GEARS) gear++;
    }
    /** Shifts the gear down by one, if not already in neutral. */
    public void shiftDown() {
        if (gear > 0) gear--;
    }
    // endregion
}
