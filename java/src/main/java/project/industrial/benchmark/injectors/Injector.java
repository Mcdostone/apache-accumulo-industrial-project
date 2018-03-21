package project.industrial.benchmark.injectors;


import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;

import java.util.Collection;
import java.util.List;

/**
 * Interface représentant un injecteur de données.
 *
 * @author Yann Prono
 */
public interface Injector {

    int inject(Mutation mutation) throws MutationsRejectedException;

    int inject(List<Mutation> mutations) throws MutationsRejectedException;

    void close() throws MutationsRejectedException;

    void flush() throws MutationsRejectedException;

}
