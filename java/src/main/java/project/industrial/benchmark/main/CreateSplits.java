package project.industrial.benchmark.main;

/**
 * Génére des splits sur deux charactères.
 *
 * @author Yann Prono
 */
public class CreateSplits {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static void main(String[] args) {
        for (int i = 0; i < ALPHABET.length(); i++) {
            char current = ALPHABET.charAt(i);
            for (int j = 0; j < ALPHABET.length(); j++) {
                System.out.printf("%c%c\n",current, ALPHABET.charAt(j));
            }
        }
    }
}
