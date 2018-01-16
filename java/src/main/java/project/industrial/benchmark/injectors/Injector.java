package project.industrial.benchmark.injectors;


import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;

import java.util.Collection;
import java.util.List;

/**
 * Interface representing an injector for accumulo.
 * @author Yann Prono
 */
public interface Injector {

    /**
     * inject all mutations to accumulo
     */
    public int inject() throws MutationsRejectedException;

    public void addMutation(Mutation mutation);

    public void addMutations(Collection<Mutation> mutations);

    public void close() throws MutationsRejectedException;

    public void flush() throws MutationsRejectedException;

}
