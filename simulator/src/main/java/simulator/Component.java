package simulator;

public abstract class Component {
    // «««Core Identity»»»
    private final String name;
    private final double weight;
    private final double price;

    // «««Initialization»»»
    public Component(String name, double weight, double price) {
        this.name = name;
        this.weight = weight;
        this.price = price;
    }



    // «««Accessors»»»
    public String getName() {
        return this.name;
    }
    public double getWeight() {
        return this.weight;
    }
    public double getPrice() {
        return this.price;
    }

    // «««Display Methods»»»
    public String getWeightText() {
        return String.format("%.2f kg", this.weight);
    }
    public String getPriceText() {
        return String.format("%.2f PLN", this.price);
    }
}
