package simulator;

public class Gearbox extends Component {
    private int numGears;
    private int currentGear;
    private int currentGearRatio;
    private Clutch clutch;

    public Gearbox(String name, double weight, double price, int numGears, Clutch clutch) {
        super(name, weight, price);
        this.numGears = numGears;
        currentGear = 0;
        currentGearRatio = 0;
        this.clutch = clutch;
    }

    public int getCurrentGear() {
        return currentGear;
    }

    public int getCurrentGearRatio() {
        return currentGearRatio;
    }

    public void shiftUp() {}

    public void shiftDown() {}
}
