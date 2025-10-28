package animals;

public class Parrot extends Animal {
    public Parrot(String name) {
        super(name, 2);
    }

    public String getDescription() {
        return "Papuga " + this.name + " ma " + this.legs + " nogi.";
    }
}
