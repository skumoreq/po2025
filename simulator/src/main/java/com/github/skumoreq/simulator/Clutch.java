package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a clutch car component.
 * <p>
 * Manages the connection between the engine and the transmission, controlling
 * the torque transfer between them.
 *
 * @see Transmission
 * @see Engine
 */
public class Clutch extends CarComponent {

    // region ⮞ Constants

    private static final String UI_STATE_ENGAGED = "Załączone";
    private static final String UI_STATE_DISENGAGED = "Rozłączone";
    // endregion

    // region ⮞ Instance Fields

    private boolean engaged;
    // endregion

    // region ⮞ Initialization

    public Clutch(@NotNull String name, double weight, double price) {
        super(name, weight, price);

        engaged = true;
    }

    /**
     * Creates a new Clutch based on an existing instance.
     */
    public Clutch(@NotNull Clutch clutch) {
        this(clutch.name, clutch.weight, clutch.price);
    }
    // endregion

    // region ⮞ Getters

    /**
     * @return {@code true} if the clutch allows the torque transfer from the
     * engine to the transmission.
     */
    public boolean isEngaged() {
        return engaged;
    }
    // endregion

    // region ⮞ Control Methods

    /**
     * Connects the engine to the transmission.
     */
    public boolean engage() {
        if (engaged) return false;

        engaged = true;

        return true;
    }

    /**
     * Disconnects the engine from the transmission.
     */
    public boolean disengage() {
        if (!engaged) return false;

        engaged = false;

        return true;
    }
    // endregion

    // region ⮞ Display Methods

    public @NotNull String getClutchStateDisplay() {
        return engaged ? UI_STATE_ENGAGED : UI_STATE_DISENGAGED;
    }
    // endregion
}
