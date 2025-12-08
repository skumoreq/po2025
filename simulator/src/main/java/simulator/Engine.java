package simulator;

public class Engine extends Component{
    private static final int MAX_RPM = 7000;

    private int currentRPM;

    // Constructor
    public Engine(String name, double weight, double price) {
        super(name, weight, price);
        this.currentRPM = 0;
    }

    // Basic getters
    public int getCurrentRPM() { return this.currentRPM; }

    // Engine control methods - not yet implemented
    public void start() { throw new UnsupportedOperationException("Not implemented yet"); }
    public void stop() { throw new UnsupportedOperationException("Not implemented yet"); }
    public void increaseRPM() { throw new UnsupportedOperationException("Not implemented yet"); }
    public void decreaseRPM() { throw new UnsupportedOperationException("Not implemented yet"); }
}
