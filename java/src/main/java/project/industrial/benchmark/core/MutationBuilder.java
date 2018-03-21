package project.industrial.benchmark.core;

import org.apache.accumulo.core.data.Mutation;

import java.util.List;

/**
 * @author Yann Prono
 */
public interface MutationBuilder {

    public List<Mutation> build(String data);

}