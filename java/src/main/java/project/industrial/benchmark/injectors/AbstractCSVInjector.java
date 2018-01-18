package project.industrial.benchmark.injectors;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.MetricsManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractCSVInjector implements Injector {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCSVInjector.class);
    private Injector injector;
    private List<String> data;
    private String filename;


    public AbstractCSVInjector(BatchWriter bw, String filename) {
        Meter meter = MetricsManager.getMetricRegistry().meter("rate_injections");
        Counter count = MetricsManager.getMetricRegistry().counter("count_injections");
        this.injector = new InjectorWithMetrics(bw, meter, count);
        this.filename = filename;
        this.data = new ArrayList<>();
    }

    public void loadData() {
        logger.info(String.format("Load '%s' in memory", this.filename));
        BufferedReader reader;
        int countLine = 0;
        String line;
        try {
            reader = new BufferedReader(new FileReader(this.filename));
            while((line = reader.readLine()) != null) {
                line = line.substring(0, line.length() - 1);
                this.data.add(line);

                countLine++; }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(countLine + " rows has been loaded");

    }

    public void createMutationsFromData() {
        this.data.forEach(d -> this.injector.addMutations(this.parseLine(d)));
    }


    protected Mutation buildMutation(String key, String cf, String cq, String value) {
        Mutation m = new Mutation(key.trim());
        m.put(cf.trim(), cq.trim(), value.trim());
        return m;
    }

    @Override
    public int inject() throws MutationsRejectedException {
        return this.injector.inject();
    }

    @Override
    public void addMutation(Mutation mutation) {
        this.injector.addMutation(mutation);
    }

    @Override
    public void addMutations(Collection<Mutation> mutations) {
        this.injector.addMutations(mutations);
    }

    @Override
    public void close() throws MutationsRejectedException {
        this.injector.close();
    }

    @Override
    public void flush() throws MutationsRejectedException {
        this.injector.flush();
    }

    protected abstract List<Mutation> parseLine(String line);

}
