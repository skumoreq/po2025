package com.github.skumoreq.simulator;

/**
 * <p>Base class for vehicle components.</p>
 * <p>Stores common properties such as name, weight, and price.</p>
 */
public abstract class Component {

    // region > Instance Identity

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
        return String.format("%.2f kg", weight);
    }
    public String getPriceDisplay() {
        return String.format("%.2f zł", price);
    }
    // endregion
}