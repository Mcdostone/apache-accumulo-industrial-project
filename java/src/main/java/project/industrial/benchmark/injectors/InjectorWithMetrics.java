package project.industrial.benchmark.injectors;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InjectorWithMetrics implements Injector {

    private static final Logger logger = LoggerFactory.getLogger(InjectorWithMetrics.class);
    private final Counter counter;
    private final Meter measureRate;
    private List<Mutation> mutations;
    private final BatchWriter bw;

    public InjectorWithMetrics(BatchWriter bw, Meter measureRate, Counter count) {
        this.bw = bw;
        this.mutations = new ArrayList<>();
        this.measureRate = measureRate;
        this.counter = count;
    }

    @Override
    public int inject() throws MutationsRejectedException {
        logger.info(String.format("Injecting %d mutations", this.mutations.size()));
        for(Mutation m: this.mutations) {
            this.measureRate.mark();
            this.bw.addMutation(m);
            this.counter.inc();
        }
        return (int) this.counter.getCount();
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
