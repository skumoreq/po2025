package simulator;

public abstract class Component {
    private final String name;
    private final double weight;
    private final double price;

    // Constructor
    public Component(String name, double weight, double price) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' parameter must not be null nor empty");
        }
        if (weight <= 0.0) {
            throw new IllegalArgumentException("'weight' parameter must be positive, got: " + weight);
        }
        if (price <= 0.0) {
            throw new IllegalArgumentException("'price' parameter must be positive, got: " + price);
        }

        this.name = name;
        this.weight = weight;
        this.price = price;
    }

    // Basic getters
    public String getName() { return this.name; }
    public double getWeight() { return this.weight; }
    public double getPrice() { return this.price; }
}
