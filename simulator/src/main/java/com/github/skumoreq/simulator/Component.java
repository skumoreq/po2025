package com.github.skumoreq.simulator;

/**
 * Base class for all {@link Car} components.
 * <p>
 * Each component has a name, weight (kg) and price (zł) field.
 * </p>
 */
public abstract class Component {

    // region > Instance Fields

    private final String name;
    private final double weight;
    private final double price;
    // endregion

    // region > Initialization

    public Component(String name, double weight, double price) {
        this.name = name;
        this.weight = weight;
        this.price = price;
    }
    // endregion

    // region > Getters

    public String getName() {
        return name;
    }
    public double getWeight() {
        return weight;
    }
    public double getPrice() {
        return price;
    }
    // endregion

    // region > Display Methods

    public String getWeightDisplay() {
        return String.format("%.1f kg", weight);
    }
    public String getPriceDisplay() {
        return String.format("%.2f zł", price);
    }
    // endregion
}
