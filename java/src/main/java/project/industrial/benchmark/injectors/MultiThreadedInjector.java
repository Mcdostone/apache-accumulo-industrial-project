package project.industrial.benchmark.injectors;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import project.industrial.benchmark.core.MetricsManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultiThreadedInjector implements Injector {

    private int index;
    private ScheduledExecutorService executorService;
    private Injector[] injectors;

    public MultiThreadedInjector(BatchWriter[] batchWriters) {
        this.index = 0;
        this.injectors = new Injector[batchWriters.length];
        Meter m = MetricsManager.getMetricRegistry().meter("rate_injection");
        Counter c = MetricsManager.getMetricRegistry().counter("count_injection");
        IntStream.range(0, batchWriters.length).forEach(i -> this.injectors[i] = new InjectorWithMetrics(batchWriters[i], m, c));
        this.executorService = new ScheduledThreadPoolExecutor(batchWriters.length);
    }

    @Override
    public int inject() throws MutationsRejectedException {
        try {
            this.executorService.invokeAll(
                    Arrays.stream(injectors).map(injector -> new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            return injector.inject();
                        }
                    }).collect(Collectors.toList()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void updateIndex() {
        this.index = this.index == this.injectors.length - 1 ? 0 : index + 1;
    }

    @Override
    public void addMutation(Mutation mutation) {
        this.injectors[this.index].addMutation(mutation);
        this.updateIndex();
    }

    @Override
    public void addMutations(Collection<Mutation> mutations) {
        this.injectors[this.index].addMutations(mutations);
        this.updateIndex();
    }

    @Override
    public void close() throws MutationsRejectedException {
        Arrays.stream(this.injectors).forEach(injector -> {
            try {
                injector.close();
            } catch (MutationsRejectedException e) {
                e.printStackTrace();
            }
        });
        this.executorService.shutdown();
    }

    @Override
    public void flush() throws MutationsRejectedException {
        Arrays.stream(this.injectors).forEach(injector -> {
            try {
                injector.flush();
            } catch (MutationsRejectedException e) {
                e.printStackTrace();
            }
        });
    }

}
