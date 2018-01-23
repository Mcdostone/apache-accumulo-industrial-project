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

    public int inject(Mutation mutation) throws MutationsRejectedException;

    public int inject(List<Mutation> mutations) throws MutationsRejectedException;

    public void close() throws MutationsRejectedException;

    public void flush() throws MutationsRejectedException;

}
