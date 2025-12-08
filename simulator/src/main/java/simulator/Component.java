package simulator;

public abstract class Component {
    private final String name;
    private final double weight;
    private final double price;

    // Constructor
    public Component(String name, double weight, double price) {
        this.name = name;
        this.weight = weight;
        this.price = price;
    }

    // Basic getters
    public String getName() { return this.name; }
    public double getWeight() { return this.weight; }
    public double getPrice() { return this.price; }
}
