package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a vehicle gearbox component.</p>
 * <p>It tracks the current and previous gear, and the effective gear ratio.
 * Shifting gears and engaging/disengaging the clutch affects the gear ratio.</p>
 */
public class Gearbox extends Component {

    // region > Class Constants

    public static final int NUM_GEARS = 5;
    // endregion

    // region > Instance Identity

    private final Clutch clutch;
    private final double[] gearRatios;
    // endregion

    // region > Instance State

    private int gear;
    private int previousGear;
    private double gearRatio;
    // endregion

    // region > Initialization

    public Gearbox(String name, double weight, double price, Clutch clutch, double[] gearRatios) {
        super(name, weight, price);
        this.clutch = new Clutch(clutch);
        this.gearRatios = gearRatios;

        previousGear = 0;
        gear = 0;
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

    public boolean isInNeutral() {
        return gear == 0 || !clutch.isEngaged();
    }
    // endregion

    // region > Control Methods
    public void updatePreviousGear() {
        previousGear = gear;
    }
    public void clearPreviousGear() {
        previousGear = 0;
    }
    public void updateGearRatio() {
        gearRatio = isInNeutral() ? 0.0 : gearRatios[gear - 1];
    }
    public void shiftUp() {
        if (gear < NUM_GEARS) gear++;
    }
    public void shiftDown() {
        if (gear > 0) gear--;
    }
    // endregion
}