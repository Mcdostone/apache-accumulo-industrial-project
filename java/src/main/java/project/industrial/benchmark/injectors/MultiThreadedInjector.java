package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

        IntStream.range(0, batchWriters.length).forEach(i -> this.injectors[i] = new InjectorWithMetrics(batchWriters[i]));
        this.executorService = new ScheduledThreadPoolExecutor(batchWriters.length);
    }

    private void updateIndex() {
        this.index = this.index == this.injectors.length - 1 ? 0 : index + 1;
    }

    @Override
    public int inject(Mutation mutation) throws MutationsRejectedException {
        return 0;
    }

    @Override
    public int inject(List<Mutation> mutations) throws MutationsRejectedException {
        return 0;
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
