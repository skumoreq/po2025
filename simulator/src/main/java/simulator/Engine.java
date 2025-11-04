package simulator;

public class Engine extends Component{
    private int maxRPM;
    private int currentRPM;

    public Engine(String name, double weight, double price, int maxRPM) {
        super(name, weight, price);
        this.maxRPM = maxRPM;
        currentRPM = 0;
    }

    public void start() {}

    public void stop() {}

    public void increaseRPM() {}

    public void decreaseRPM() {}
}
