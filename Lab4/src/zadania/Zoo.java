package zadania;

import animals.Animal;

public class Zoo {
    public Animal[] animals;

    public Zoo(int num_animals) {
        animals = new Animal[num_animals];

        for (int i = 0; i < num_animals; i++) {
            animals[i] = Animal.getRandomAnimal();
        }
    }

    public int getNumLegs() {
        int numLegs = 0;
        for (Animal animal : animals)  numLegs += animal.legs;
        return numLegs;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        for (Animal animal : animals) {
            str.append(animal.getDescription());
            str.append('\n');
        }

        return str.toString();
    }

    static void main() {
        Zoo zoo = new Zoo(10);
        System.out.println(zoo);
        System.out.println("Łączna liczba nóg: " + zoo.getNumLegs());

        for (Animal animal : zoo.animals) {
            animal.makeSound();
        }
    }
}
