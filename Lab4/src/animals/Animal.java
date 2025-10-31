package animals;

import java.util.Random;

public abstract class Animal {
    public String name;
    public int legs;

    public Animal(String name, int legs) {
        this.name = name;
        this.legs = legs;
    }

    public static Animal getRandomAnimal() {
        Random rand = new Random();
        String name = NameGenerator.generate();

        return switch (rand.nextInt(3)) {
            case 0 -> new Dog(name);
            case 1 -> new Parrot(name);
            case 2 -> new Snake(name);
            default -> null;
        };
    }

    public abstract String getDescription();
    public abstract void makeSound();
}