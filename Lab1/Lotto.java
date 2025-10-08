import java.util.Random;
import java.util.HashSet;

public class Lotto {
    public static void main(String[] args) {
        Random random = new Random();
        HashSet<Integer> set = new HashSet<>();
        while (set.size() < 6) {
            int number = random.nextInt(49) + 1;
            set.add(number);
        }
        System.out.println(set);
    }
}
