package animals;

import java.util.Random;

public class NameGenerator {
    public static Random rand = new Random();

    public static char[] vowels = {'a', 'e', 'i', 'o', 'u', 'y'};
    public static char[] consonants = {
            'b', 'c', 'd', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'p',
            'r', 's', 't', 'w', 'z'
    };
    public static String[] patterns = {
            "CVC", "CV", "CVCV", "CVV",
            "CVVC", "VCV", "VC", "CVCC"
    };

    public static double[] probNumPatterns = {0.1, 0.5, 0.3, 0.1};
    public static double[] probPattern = {0.25, 0.05, 0.2, 0.1, 0.15, 0.15, 0.05, 0.05};

    public static int getNumPatterns() {
        double chance = rand.nextDouble();
        double cumulative = 0.0;

        for (int i = 0; i < probNumPatterns.length; i++) {
            cumulative += probNumPatterns[i];

            if (chance < cumulative) return (i + 1);
        }

        return probNumPatterns.length;
    }

    public static String getPattern() {
        double chance = rand.nextDouble();
        double cumulative = 0.0;

        for (int i = 0; i < probPattern.length; i++) {
            cumulative += probPattern[i];

            if (chance < cumulative) return patterns[i];
        }

        return patterns[probPattern.length - 1];
    }

    public static String fillPattern(String pattern) {
        StringBuilder filledPattern = new StringBuilder();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);

            if (c == 'V') {
                filledPattern.append(vowels[rand.nextInt(vowels.length)]);
            } else if (c == 'C') {
                filledPattern.append(consonants[rand.nextInt(consonants.length)]);
            }
        }

        return filledPattern.toString();
    }

    public static String generate() {
        StringBuilder pattern = new StringBuilder();
        int numPatterns = getNumPatterns();

        for (int i = 0; i < numPatterns; i++) {
            pattern.append(getPattern());
        }

        return fillPattern(pattern.toString());
    }
}
