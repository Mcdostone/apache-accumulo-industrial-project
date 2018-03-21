package project.industrial.benchmark.core;

import java.util.List;


/**
 * Stratégie pour la génération d'un ROW ID
 *
 * @author Yann Prono
 */
public interface KeyGeneratorStrategy {

    public List<String> generateKeys(int nb);

    public String generateOne();

    public String[] getRange();
}
