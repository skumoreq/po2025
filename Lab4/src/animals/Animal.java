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
        String[] syllables = {
                "ba", "ko", "mi", "la", "da",
                "ru", "ti", "no", "sa", "pe",
                "fi", "mu", "ka", "lo", "vi",
                "ne", "gi", "ra", "so", "tu",
                "pa", "li", "bo", "zu", "me",
                "ki", "sa", "na", "do", "fu",
                "te", "ra", "mi", "jo", "vi",
                "ku", "la", "si", "po", "de",
                "ma", "no", "li", "ru", "fa",
                "bi", "so", "ka", "te", "nu"
        };

        int num_syllables = rand.nextInt(3) + 2;
        StringBuilder name = new StringBuilder();

        for (int i = 0; i < num_syllables; i++) {
            name.append(syllables[rand.nextInt(syllables.length)]);
        }

        return switch (rand.nextInt(3)) {
            case 0 -> new Dog(name.toString());
            case 1 -> new Parrot(name.toString());
            case 2 -> new Snake(name.toString());
            default -> null;
        };

    }

    public abstract String getDescription();
}