package simulator;

public class Clutch extends Component{
    // «««Runtime State»»»
    private boolean isEngaged;

    // «««Constructors»»»
    public Clutch(String name, double weight, double price) {
        super(name, weight, price);
        this.isEngaged = false;
    }



    // «««Basic Getters»»»
    public boolean getIsEngaged() {
        return this.isEngaged;
    }

    // «««String Representations»»»
    public String isEngagedToString() {
        return this.isEngaged ? "zaciśnięte" : "rozłączone";
    }



    // «««Action Methods»»»
    // !!! Not implemented yet !!!
    public void engage() {

    }
    public void disengage() {

    }
}
