package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a clutch component.
 * <p>
 * Can be engaged or disengaged, affecting gearbox and engine operation.
 * </p>
 */
public class Clutch extends Component{

    // region > Instance Fields

    private boolean isEngaged;
    // endregion

    // region > Initialization

    public Clutch(String name, double weight, double price) {
        super(name, weight, price);

        isEngaged = true;
    }
    public Clutch(@NotNull Clutch clutch) {
        this(clutch.getName(), clutch.getWeight(), clutch.getPrice());
    }
    // endregion

    // region > Getters

    public boolean isEngaged() {
        return isEngaged;
    }
    // endregion

    // region > Display Methods

    public String getEngagementStatusDisplay() {
        return isEngaged ? "Zaciśnięte" : "Rozłączone";
    }
    // endregion

    // region > Control Methods

    /** Engages the clutch, mechanically coupling the engine and gearbox. */
    public void engage() {
        isEngaged = true;
    }
    /** Disengages the clutch, allowing gear changes. */
    public void disengage() {
        isEngaged = false;
    }
    // endregion
}
