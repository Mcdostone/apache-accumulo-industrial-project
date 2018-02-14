package project.industrial.benchmark.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class KeyGeneratorFromFileStrategy implements KeyGeneratorStrategy {

    private List<String> rowKeys;

    public KeyGeneratorFromFileStrategy(String csv) {
        try {
            rowKeys = Scenario.readRowKeysFromFile(csv);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
    public List<String> generateKeys(int nb, int length) {
        return null;
    }

    @Override
    public String generateOne() {
        int i = (int) (Math.random() * (this.rowKeys.size()));
        return this.rowKeys.get(i);
    }
}
