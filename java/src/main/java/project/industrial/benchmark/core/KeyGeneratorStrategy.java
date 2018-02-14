package project.industrial.benchmark.core;

import java.util.List;

public interface KeyGeneratorStrategy {

    public List<String> generateKeys(int nb);

    public List<String> generateKeys(int nb, int length);

    public String generateOne();
}
