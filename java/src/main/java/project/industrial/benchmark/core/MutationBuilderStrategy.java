package project.industrial.benchmark.core;

import org.apache.accumulo.core.data.Mutation;
import project.industrial.benchmark.injectors.RowIdBuilderStrategy;

/**
 * This interface define a Strategy for creating a new mutation for accumulo
 * @author Yann Prono
 */
public interface MutationBuilderStrategy {

    public Mutation buildMutation(String data);

    public Mutation buildMutation(String key, String cf, String cq, String value);
}


