package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleInjector implements Injector {

    private static final Logger logger = LoggerFactory.getLogger(SimpleInjector.class);
    private List<Mutation> mutations;
    private final BatchWriter bw;

    public SimpleInjector(BatchWriter bw) {
        this.bw = bw;
        this.mutations = new ArrayList<>();
    }

    @Override
    public int inject() throws MutationsRejectedException {
        logger.info(String.format("Injecting %d mutations", this.mutations.size()));
        int countInjections = 0;
        for(Mutation m: this.mutations) {
            this.bw.addMutation(m);
            countInjections++;
        }
        return countInjections;
    }

    @Override
    public void addMutation(Mutation mutation) {
        this.mutations.add(mutation);
    }

    @Override
    public void addMutations(Collection<Mutation> mutations) {
        this.mutations.addAll(mutations);
    }

    @Override
    public void close() throws MutationsRejectedException {
        this.bw.close();
    }

    @Override
    public void flush() throws MutationsRejectedException {
        this.bw.flush();
    }

}
