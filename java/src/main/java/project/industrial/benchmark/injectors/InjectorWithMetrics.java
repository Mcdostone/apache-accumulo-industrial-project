package project.industrial.benchmark.injectors;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import project.industrial.benchmark.core.MetricsManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author Yann Prono
 */
public class InjectorWithMetrics implements Injector {

    private final Injector injector;
    private Meter meter;


    public InjectorWithMetrics(BatchWriter bw) {
        super();
        this.injector = new SimpleInjector(bw);
        try {
            this.meter = MetricsManager.getMetricRegistry().meter( "rate_injections." + InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int inject(Mutation m) throws MutationsRejectedException {
        int count = this.injector.inject(m);
        this.meter.mark();
        return count;
    }

    @Override
    public int inject(List<Mutation> mutations) throws MutationsRejectedException {
        int count = this.injector.inject(mutations);
        this.meter.mark(count);
        return count;
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
