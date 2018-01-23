package project.industrial.benchmark.injectors;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.MetricsManager;

import java.util.List;

public class InjectorWithMetrics implements Injector {

    private final Injector injector;
    private final Counter counter;
    private final Meter meter;


    public InjectorWithMetrics(BatchWriter bw, Meter meter, Counter counter) {
        super();
        this.injector = new SimpleInjector(bw);
        this.meter = meter;
        this.counter = counter;
    }

    public InjectorWithMetrics(BatchWriter bw) {
        this(bw,
                MetricsManager.getMetricRegistry().meter("rate_injections"),
                MetricsManager.getMetricRegistry().counter("count_injections")
        );
    }

    @Override
    public int inject(Mutation m) throws MutationsRejectedException {
        return this.injector.inject(m);
    }

    @Override
    public int inject(List<Mutation> mutations) throws MutationsRejectedException {
        return this.injector.inject(mutations);
    }

    @Override
    public void close() throws MutationsRejectedException {
        this.injector.close();
    }

    @Override
    public void flush() throws MutationsRejectedException {
        this.injector.flush();
    }

}
