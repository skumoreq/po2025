import java.util.ArrayList;
import java.util.Random;

public class Lotto {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Random random = new Random();

        ArrayList<Integer> random_numbers = new ArrayList<>();

        while (random_numbers.size() < 6) {
            int random_number = random.nextInt(49) + 1;

            if (random_numbers.contains(random_number)) continue;

            random_numbers.add(random_number);
        }

        int iterations = 0;

        while (true) {
            iterations++;

            ArrayList<Integer> picked_numbers = new ArrayList<>();

            while (picked_numbers.size() < 6) {
                int random_number = random.nextInt(49) + 1;

                if (picked_numbers.contains(random_number)) continue;

                picked_numbers.add(random_number);
            }

            picked_numbers.retainAll(random_numbers);

            if (picked_numbers.size() == 6) break;
        }

        System.out.println("Liczba losowań: " + iterations);
        System.out.println("Czas działania: " + (System.currentTimeMillis() - start) + "ms");

//        ArrayList<Integer> picked_numbers = new ArrayList<>();
//
//        for (String picked_number : args) {
//            picked_numbers.add(Integer.parseInt(picked_number));
//        }
//
//        System.out.println("Twoje typy: " + picked_numbers);
//
//        ArrayList<Integer> random_numbers = new ArrayList<>();
//        Random random = new Random();
//
//        while (random_numbers.size() < 6) {
//            int random_number = random.nextInt(49) + 1;
//
//            if (random_numbers.contains(random_number)) continue;
//
//            random_numbers.add(random_number);
//        }
//
//        System.out.println("Wylosowane liczby: " + random_numbers);
//
//        random_numbers.retainAll(picked_numbers);
//
//        int num_of_hits = random_numbers.size();
//
//        System.out.println("Liczba trafień: " + num_of_hits);
    }
}
