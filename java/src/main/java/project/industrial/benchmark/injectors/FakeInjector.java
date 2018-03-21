package project.industrial.benchmark.injectors;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import project.industrial.benchmark.core.MetricsManager;

import java.util.List;

/**
 * FakeInjector est utilisé pour mesurer le temps pour la lecture du fichier CSV + la transformation des données en mutations.
 *
 * @author Yann Prono
 */
public class FakeInjector implements Injector {

    private final Counter counter;
    private final Meter meter;

    public FakeInjector() {
        this.counter = MetricsManager.getMetricRegistry().counter("count_call_inject_method");
        this.meter = MetricsManager.getMetricRegistry().meter("rate_call_inject_method");
    }

    @Override
    public int inject(Mutation mutation) {
        this.counter.inc();
        this.meter.mark();
        return 0;
    }

    @Override
    public int inject(List<Mutation> mutations) {
        this.counter.inc();
        this.meter.mark();
        return 0;
    }

    @Override
    public void close() { }

    @Override
    public void flush() { }
}
