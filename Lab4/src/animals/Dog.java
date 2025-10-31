package animals;

public class Dog extends Animal {
    public Dog(String name) {
        super(name, 4);
    }

    public String getDescription() {
        return "Pies " + this.name + " ma " + this.legs + " nogi.";
    }

    public void makeSound() {
        System.out.println("hau hau");
    }
}
