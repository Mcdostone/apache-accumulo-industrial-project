package project.industrial.benchmark.scenarios;

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

public class InfiniteTimeGetByKeyRangeScenario extends Scenario {

    private final KeyGeneratorStrategy keyGen;
    private BatchScanner[] scanners;
    private ExecutorService executorService;

    public InfiniteTimeGetByKeyRangeScenario(BatchScanner[] scanners, KeyGeneratorStrategy keyGen) {
        super(InfiniteTimeGetByKeyRangeScenario.class.getSimpleName());
        this.scanners = scanners;
        this.keyGen = keyGen;
        this.executorService = Executors.newFixedThreadPool(this.scanners.length);
    }

    @Override
    public void action() throws Exception {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i = 0; i < this.scanners.length; i++) {
            tasks.add(new InfiniteGetByKeyRangeTask(
                    this.scanners[i],
                    MetricsManager.getMetricRegistry().meter(String.format("get_by_range.thread_%d", i)),
                    this.keyGen
            ));
        }
        System.out.println("before invokeAll");
        this.executorService.invokeAll(tasks);
        System.out.println("after invokeAll");
    }

    @Override
    public void finish() {
        super.finish();
        this.executorService.shutdown();
    }


    public static void main(String[] args) throws Exception {
        KeyFileOpts opts = new KeyFileOpts();
        opts.parseArgs(InfiniteTimeGetByKeyRangeScenario.class.getName(), args);
        Connector connector = opts.getConnector();

        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);
        sc.setRange(Range.exact("aa"));
        for (Map.Entry<Key, Value> entry : sc) { }

        BatchScanner[] scanners = new BatchScanner[2];
        for(int i = 0; i < scanners.length; i++)
            scanners[i] = connector.createBatchScanner(opts.getTableName(), opts.auths, 1);
        Scenario scenario;
        if(opts.keyFile == null)
            scenario = new InfiniteTimeGetByKeyRangeScenario(scanners, new RandomKeyGeneratorStrategy());
        else
            scenario = new InfiniteTimeGetByKeyRangeScenario(scanners, new KeyGeneratorFromFileStrategy(opts.keyFile));
        scenario.run();
        scenario.finish();
    }
}