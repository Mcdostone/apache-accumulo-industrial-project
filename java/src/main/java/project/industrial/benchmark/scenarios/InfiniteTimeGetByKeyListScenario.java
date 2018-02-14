package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.*;
import project.industrial.benchmark.tasks.InfiniteGetByKeyListTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfiniteTimeGetByKeyListScenario extends Scenario {

    private final KeyGeneratorStrategy keyGen;
    private BatchScanner[] bscanners;
    private ExecutorService executorService;

    public InfiniteTimeGetByKeyListScenario(BatchScanner[] bscanners, KeyGeneratorStrategy keyGen) {
        super(InfiniteTimeGetByKeyListScenario.class.getSimpleName());
        this.bscanners = bscanners;
        this.keyGen = keyGen;
        this.executorService = Executors.newFixedThreadPool(bscanners.length);
    }

    @Override
    public void action() throws Exception {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i = 0; i < this.bscanners.length; i++) {
            tasks.add(new InfiniteGetByKeyListTask(
                    this.bscanners[i],
                    MetricsManager.getMetricRegistry().meter(String.format("get_by_list.thread_%d", i)),
                    this.keyGen
            ));
        }
        this.executorService.invokeAll(tasks);
    }


    public static void main(String[] args) throws Exception {
        KeyFileOpts opts = new KeyFileOpts();
        opts.parseArgs(InfiniteTimeGetByKeyListScenario.class.getName(), args);
        Connector connector = opts.getConnector();

        // init first connection
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);
        sc.setRange(Range.exact("aa"));
        for (Map.Entry<Key, Value> entry : sc) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        BatchScanner[] scanners = new BatchScanner[2];
        for(int i = 0; i < scanners.length; i++)
            scanners[i] = connector.createBatchScanner(opts.getTableName(), opts.auths, 1);

        Scenario scenario;
        if(opts.keyFile == null)
            scenario = new InfiniteTimeGetByKeyListScenario(scanners, new RandomKeyGeneratorStrategy());
        else
            scenario = new InfiniteTimeGetByKeyListScenario(scanners, new KeyGeneratorFromFileStrategy(opts.keyFile));
        scenario.run();
        scenario.finish();
    }
}