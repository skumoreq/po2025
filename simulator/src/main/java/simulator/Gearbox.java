package simulator;

public class Gearbox extends Component {
    // «««Class Constants»»»
    public static final int NUM_GEARS = 5;



    // «««Core Identity»»»
    private final Clutch clutch;
    private final double[] gearRatios;

    // «««Runtime State»»»
    private int currentGear;
    private double currentGearRatio;

    // «««Constructors»»»
    public Gearbox(String name, double weight, double price, Clutch clutch, double[] gearRatios) {
        super(name, weight, price);

        // Clone clutch Object
        this.clutch = new Clutch(
                clutch.getName(),
                clutch.getWeight(),
                clutch.getPrice()
        );

        this.gearRatios = gearRatios;
        this.currentGear = 0;
        this.currentGearRatio = 0.0;
    }



    // «««Basic Getters»»»
    public Clutch getClutch() {
        return this.clutch;
    }
    public double[] getGearRatios() {
        return this.gearRatios;
    }
    public int getCurrentGear() {
        return this.currentGear;
    }
    public double getCurrentGearRatio() {
        return this.currentGearRatio;
    }

    // «««String Representations»»»
    public String gearRatiosToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.gearRatios.length; i++) {
            sb.append(String.format("%.1f", this.gearRatios[i]));
            if (i < this.gearRatios.length - 1) {
                sb.append("│");
            }
        }
        return sb.toString();
    }
    public String currentGearToString() {
        return switch (this.currentGear) {
            case 0 -> "N";
            case 1 -> "1↓";
            case 5 -> "5↑";
            default -> String.format("%d", this.currentGear);
        };
    }
    public String currentGearRatioToString() {
        return String.format("%.1f", this.currentGearRatio);
    }



    // «««Action Methods»»»
    // !!! Not implemented yet !!!
    public void shiftUp() {

    }
    public void shiftDown() {

    }
}
