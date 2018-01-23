package project.industrial.benchmark.injectors;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.MetricsManager;

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

    public InjectorWithMetrics(BatchWriter bw) {
        this(bw,
                MetricsManager.getMetricRegistry().meter("data_rate_injection"),
                MetricsManager.getMetricRegistry().counter("count_injections")
        );
    }

    @Override
    public int inject() {
        logger.info(String.format("Injecting %d mutations", this.mutations.size()));
        long begin = System.currentTimeMillis();
        this.mutations.forEach(m -> {
            try {
                this.measureRate.mark();
                this.bw.addMutation(m);
            }
            catch (MutationsRejectedException e) { e.printStackTrace(); }
            this.counter.inc();
        });
        System.out.println((System.currentTimeMillis() - begin));
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
