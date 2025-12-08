package simulator;

public class Engine extends Component{
    private final int maxRPM;
    private int currentRPM;

    // Constructor
    public Engine(String name, double weight, double price) {
        super(name, weight, price);
        this.maxRPM = 7000;
        this.currentRPM = 0;
    }

    // Basic getters
    public int getMaxRPM() { return this.maxRPM; }
    public int getCurrentRPM() { return this.currentRPM; }

    // Engine control methods - not yet implemented
    public void start() {}
    public void stop() {}
    public void increaseRPM() {}
    public void decreaseRPM() {}
}
