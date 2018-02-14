package project.industrial.benchmark.core;

import java.util.List;

public interface KeyGeneratorStrategy {

    public List<String> generateKeys(int nb);

    public String generateOne();

    public String[] getRange();
}
