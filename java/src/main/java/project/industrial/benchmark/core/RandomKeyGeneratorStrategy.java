package project.industrial.benchmark.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomKeyGeneratorStrategy implements KeyGeneratorStrategy {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    @Override
    public List<String> generateKeys(int nb) {
        Random r = new Random();
        return this.generateKeys(nb, r.nextInt(10));
    }

    private List<String> generateKeys(int nb, int length) {
        List<String> keys = new ArrayList<>();
        for(int i = 0; i < nb; i++) {
            keys.add(generateRandomKey(length));
        }
        return keys;
    }

    @Override
    public String generateOne() {
        return this.generateKeys(1, 10).get(0);
    }

    @Override
    public String[] getRange() {
        System.err.println("Don't call RandomKeyGeneratorStrategy.getRange()");
        return new String[]{"aaaaaaaaaa", "aaaaabbbbb"};
    }

    public static String generateRandomKey(int length) {
        StringBuilder key = new StringBuilder();
        for(int i = 0; i < length; i++) {
            Random randomGenerator = new Random();
            char ch = ALPHABET.charAt(randomGenerator.nextInt(ALPHABET.length()));
            key.append(ch);
        }
        return key.toString();
    }
}
