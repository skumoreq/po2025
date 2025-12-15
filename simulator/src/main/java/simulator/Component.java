package simulator;

public abstract class Component {
    // «««Core Identity»»»
    private final String name;
    private final double weight;
    private final double price;

    // «««Constructors»»»
    public Component(String name, double weight, double price) {
        this.name = name;
        this.weight = weight;
        this.price = price;
    }



    // «««Basic Getters»»»
    public String getName() {
        return this.name;
    }
    public double getWeight() {
        return this.weight;
    }
    public double getPrice() {
        return this.price;
    }

    // «««String Representations»»»
    public String weightToString() {
        return String.format("%.2f kg", this.weight);
    }
    public String priceToString() {
        return String.format("%.2f PLN", this.price);
    }
}
