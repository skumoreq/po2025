package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * <p>Represents a vehicle clutch component.</p>
 * <p>The clutch can be engaged or disengaged, which affects the gearbox operation.</p>
 */
public class Clutch extends Component{

    // region > Instance State

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
        return isEngaged ? "zaciśnięte" : "rozłączone";
    }
    // endregion

    // region > Control Methods

    public void engage() {
        isEngaged = true;
    }
    public void disengage() {
        isEngaged = false;
    }
    // endregion
}