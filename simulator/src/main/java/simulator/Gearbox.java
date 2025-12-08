package simulator;

// Speed (km/h) ≈ (RPM × 0.032) / Gear Ratio
// Gear 1 (4.2:1), Gear 2 (2.5:1), Gear 3 (1.6:1), Gear 4 (1.0:1), Gear 5 (0.8:1)

public class Gearbox extends Component {
    private final Clutch clutch;
    private final int numGears;
    private int currentGear;
    private int currentGearRatio;

    // Constructor
    public Gearbox(String name, double weight, double price, Clutch clutch) {
        super(name, weight, price);
        this.clutch = clutch;
        this.numGears = 5;
        this.currentGear = 0;
        this.currentGearRatio = 0;
    }

    // Basic getters
    public Clutch getClutch() { return this.clutch; }
    public int getNumGears() { return this.numGears; }
    public int getCurrentGear() { return this.currentGear; }
    public int getCurrentGearRatio() { return this.currentGearRatio; }

    // Gearbox control methods - not yet implemented
    public void shiftUp() {}
    public void shiftDown() {}
}
