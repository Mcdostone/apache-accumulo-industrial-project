package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;

import java.util.Collection;

public class SimpleInjectorWithMetric implements Injector {
    @Override
    public int inject() throws MutationsRejectedException {
        return 0;
    }

    @Override
    public void addMutation(Mutation mutation) {

    }

    @Override
    public void addMutations(Collection<Mutation> mutations) {

    }

    @Override
    public void close() throws MutationsRejectedException {

    }

    @Override
    public void flush() throws MutationsRejectedException {

    }
}
