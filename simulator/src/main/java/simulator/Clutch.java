package simulator;

public class Clutch extends Component{
    // «««Dynamic State»»»
    private boolean isEngaged = true;

    // «««Initialization»»»
    public Clutch(String name, double weight, double price) {
        super(name, weight, price);
    }



    // «««Accessors»»»
    public boolean isEngaged() {
        return this.isEngaged;
    }

    // «««Display Methods»»»
    public String getEngagementStatusText() {
        return this.isEngaged ? "zaciśnięte" : "rozłączone";
    }



    // «««Control Methods»»»
    public void engage() {
        this.isEngaged = true;
    }
    public void disengage() {
        this.isEngaged = false;
    }
}
