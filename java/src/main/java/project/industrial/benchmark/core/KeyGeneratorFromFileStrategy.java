package project.industrial.benchmark.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stratégie permettant d'obtenir une ROW ID
 * présent dans un fichier donné par l'utilisateur.
 *
 * @author Yann Prono
 */
public class KeyGeneratorFromFileStrategy implements KeyGeneratorStrategy {

    private List<String> rowKeys;

    public KeyGeneratorFromFileStrategy(String file) {
        try {
            rowKeys = readRowKeysFromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList readRowKeysFromFile(String filename) throws FileNotFoundException {
        ArrayList rk = new ArrayList();
        java.util.Scanner scan = new java.util.Scanner(new File(filename));
        while(scan.hasNext()) {
            String line = scan.nextLine();
            line = line.trim();
            rk.add(line);
        }
        return rk;
    }

    @Override
    public List<String> generateKeys(int nb) {
        List<String> list = new ArrayList();
        int max = this.rowKeys.size() < nb ? this.rowKeys.size() : nb;
        for (int j = 0; j < max; j++) {
            list.add(this.generateOne());
        }
        return list;
    }

    @Override
    public String generateOne() {
        int i = (int) (Math.random() * (this.rowKeys.size()));
        return this.rowKeys.get(i);
    }

    @Override
    public String[] getRange() {
        int i = (int) (Math.random() * (this.rowKeys.size() - 2000));
        return new String[]{this.rowKeys.get(i), this.rowKeys.get(i + 2000)};
    }
}
