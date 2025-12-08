package simulator;

// Speed (km/h) ≈ (RPM × 0.032) / Gear Ratio
// Gear 1 (4.2:1), Gear 2 (2.5:1), Gear 3 (1.6:1), Gear 4 (1.0:1), Gear 5 (0.8:1)

public class Gearbox extends Component {
    private static final int NUM_GEARS = 5;

    private final Clutch clutch;
    private int currentGear;
    private int currentGearRatio;

    // Constructor
    public Gearbox(String name, double weight, double price, Clutch clutch) {
        super(name, weight, price);

        if (clutch == null) {
            throw new IllegalArgumentException("'clutch' parameter must not be null");
        }

        this.clutch = clutch;
        this.currentGear = 0;
        this.currentGearRatio = 0;
    }

    // Basic getters
    public Clutch getClutch() { return this.clutch; }
    public int getCurrentGear() { return this.currentGear; }
    public int getCurrentGearRatio() { return this.currentGearRatio; }

    // Gearbox control methods - not yet implemented
    public void shiftUp() { throw new UnsupportedOperationException("Not implemented yet"); }
    public void shiftDown() { throw new UnsupportedOperationException("Not implemented yet"); }
}
