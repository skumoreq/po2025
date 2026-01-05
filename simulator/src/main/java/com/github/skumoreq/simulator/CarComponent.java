package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all car components.
 * <p>
 * Provides common attributes such as name, weight, and price. Provides
 * formatted string representations for usage in UI.
 *
 * @see Car
 */
public abstract class CarComponent {

    // region ⮞ Constants

    private static final String UI_FALLBACK_NAME = "Brak nazwy części";
    private static final String UI_FORMAT_WEIGHT = "%.1f kg";
    private static final String UI_FORMAT_PRICE = "%.2f zł";
    // endregion

    // region ⮞ Instance Fields

    protected final @NotNull String name;
    protected final double weight;
    protected final double price;
    // endregion

    // region ⮞ Initialization

    public CarComponent(@NotNull String name, double weight, double price) {
        this.name = name;
        this.weight = weight;
        this.price = price;
    }
    // endregion

    // region ⮞ Getters

    public double getWeight() {
        return weight;
    }

    public double getPrice() {
        return price;
    }
    // endregion

    // region ⮞ Display Methods

    public @NotNull String getNameDisplay() {
        return name.isBlank() ? UI_FALLBACK_NAME : name.trim();
    }

    public @NotNull String getWeightDisplay() {
        return String.format(UI_FORMAT_WEIGHT, weight);
    }

    public @NotNull String getPriceDisplay() {
        return String.format(UI_FORMAT_PRICE, price);
    }
    // endregion
}
