package simulator;

public class Gearbox extends Component {
    // «««Class Constants»»»
    public static final int NUM_GEARS = 5;



    // «««Core Identity»»»
    private final Clutch clutch;
    private final double[] gearRatios;

    // «««Dynamic State»»»
    private int previousGear = 0;
    private int gear = 0;
    private double gearRatio = 0.0;

    // «««Initialization»»»
    public Gearbox(String name, double weight, double price, Clutch clutch, double[] gearRatios) {
        super(name, weight, price);

        // Clone clutch Object
        this.clutch = new Clutch(
                clutch.getName(),
                clutch.getWeight(),
                clutch.getPrice()
        );

        this.gearRatios = gearRatios;
    }



    // «««Accessors»»»
    public Clutch getClutch() {
        return this.clutch;
    }
    public double[] getGearRatios() {
        return this.gearRatios;
    }
    public int getPreviousGear() {
        return this.previousGear;
    }
    public int getGear() {
        return this.gear;
    }
    public double getGearRatio() {
        return this.gearRatio;
    }
    public boolean isInNeutral() {
        return this.gear == 0 || !this.clutch.isEngaged();
    }

    // «««Display Methods»»»
    public String getGearRatiosListText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.gearRatios.length; i++) {
            sb.append(String.format("%.1f", this.gearRatios[i]));
            if (i < this.gearRatios.length - 1) {
                sb.append("│");
            }
        }
        return sb.toString();
    }
    public String getGearText() {
        return switch (this.gear) {
            case 0 -> "N";
            case 1 -> "1↓";
            case NUM_GEARS -> NUM_GEARS + "↑";
            default -> String.valueOf(this.gear);
        };
    }



    // «««Control Methods»»»
    public void clearPreviousGear() {
        this.previousGear = 0;
    }
    public void storePreviousGear() {
        this.previousGear = this.gear;
    }
    public void updateGearRatio() {
        this.gearRatio = this.isInNeutral() ? 0.0 : this.gearRatios[this.gear - 1];
    }
    public void shiftUp() {
        if (gear < NUM_GEARS) {
            gear++;
        }
    }
    public void shiftDown() {
        if (gear > 0) {
            gear--;
        }
    }
}
