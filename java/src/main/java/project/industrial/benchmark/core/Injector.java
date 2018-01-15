package project.industrial.benchmark.core;


import org.apache.accumulo.core.client.MutationsRejectedException;

/**
 * Interface representing an injector for accumulo.
 * @author Yann Prono
 */
public interface Injector {

    /**
     * inject all mutations to accumulo
     */
    public int inject() throws MutationsRejectedException;

    /**
     * Prepare a list of mutations before the injection
     */
    public void prepareMutations();

    /**
     * Set the mutationBuilder with a new one.
     * @param builder
     */
    public void setMutationBuilderStrategy(MutationBuilderStrategy builder);
}
