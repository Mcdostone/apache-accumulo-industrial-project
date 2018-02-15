package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.*;
import project.industrial.benchmark.tasks.InfiniteGetByKeyRangeTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfiniteGetByKeyRangeScenario extends Scenario {

    private final KeyGeneratorStrategy keyGen;
    private BatchScanner scanner;
    private ExecutorService executorService;
    private int nb;

    public InfiniteGetByKeyRangeScenario(BatchScanner scanner, KeyGeneratorStrategy keyGen, int nb) {
        super(InfiniteGetByKeyRangeScenario.class.getSimpleName());
        this.scanner = scanner;
        this.keyGen = keyGen;
        this.nb = nb;
        this.executorService = Executors.newFixedThreadPool(2);
    }

    @Override
    public void action() throws Exception {
        List<Callable<Object>> tasks = new ArrayList<>();
        tasks.add(new InfiniteGetByKeyRangeTask(
                this.scanner,
                MetricsManager.getMetricRegistry().timer(String.format("get_by_range.thread_%d", this.nb)),
                this.keyGen
        ));
        this.executorService.invokeAll(tasks);
    }

    @Override
    public void finish() {
        super.finish();
        this.executorService.shutdown();
    }

    static class Opts extends KeyFileOpts {
        @Parameter(names = "--id", description = "number for the metric", required = true)
        int id = 0;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        opts.parseArgs(InfiniteGetByKeyRangeScenario.class.getName(), args);
        Connector connector = opts.getConnector();

        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);
        sc.setRange(Range.exact("aa"));
        for (Map.Entry<Key, Value> entry : sc) { }

        BatchScanner scanner = connector.createBatchScanner(opts.getTableName(), opts.auths, 1);
        Scenario scenario;
        if(opts.keyFile == null)
            scenario = new InfiniteGetByKeyRangeScenario(scanner, new RandomKeyGeneratorStrategy(), opts.id);
        else
            scenario = new InfiniteGetByKeyRangeScenario(scanner, new KeyGeneratorFromFileStrategy(opts.keyFile), opts.id);
        scenario.run();
        scenario.finish();
    }
}