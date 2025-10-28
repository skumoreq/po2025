package zadania;

import animals.Animal;

public class Zoo {
    static void main() {
        Animal[] animals = new Animal[100];
        int sum_legs = 0;

        for (int i = 0; i < animals.length; i++) {
            Animal animal = Animal.getRandomAnimal();
            animals[i] = animal;

            if (animal == null) continue;

            sum_legs += animal.legs;
        }

        System.out.println(sum_legs);
    }
}
