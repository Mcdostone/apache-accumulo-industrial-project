package project.industrial.benchmark.injectors;


import org.apache.accumulo.core.client.MutationsRejectedException;
import project.industrial.benchmark.core.MutationBuilderStrategy;

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

    public void close() throws MutationsRejectedException;

    public void flush() throws MutationsRejectedException;

}
