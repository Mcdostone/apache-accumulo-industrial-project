package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.*;
import project.industrial.benchmark.tasks.InfiniteGetByKeyTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfiniteGetByKeyScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(InfiniteGetByKeyScenario.class);
    private BatchScanner[] scanners;
    private ExecutorService executorService;
    private KeyGeneratorStrategy keyGeneratorStrategy;

    public InfiniteGetByKeyScenario(BatchScanner[] scanners, KeyGeneratorStrategy keyGeneratorStrategy) {
        super(InfiniteGetByKeyScenario.class);
        this.scanners = scanners;
        this.executorService = Executors.newFixedThreadPool(scanners.length);
        this.keyGeneratorStrategy = keyGeneratorStrategy;
    }

    @Override
    public void action() throws Exception {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i =0; i<this.scanners.length;i++){
            tasks.add(new InfiniteGetByKeyTask(
                    this.scanners[i],
                    MetricsManager.getMetricRegistry().meter(String.format("get_by_key.thread_%d",i)),
                    this.keyGeneratorStrategy
            ));
        }
        logger.info("Invoke all tasks");
        this.executorService.invokeAll(tasks);
    }


    public static void main(String[] args) throws Exception {
        KeyFileOpts opts = new KeyFileOpts();
        opts.parseArgs(InfiniteGetByKeyScenario.class.getName(), args);
        Connector connector = opts.getConnector();
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        // Initialisation
        sc.setRange(Range.exact("aa"));
        for (Map.Entry<Key, Value> entry : sc) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        BatchScanner[] scanners = new BatchScanner[10];
        for(int i = 0; i < 10; i++)
            scanners[i] = connector.createBatchScanner(opts.getTableName(), opts.auths, 1);

        Scenario scenario;
        if(opts.keyFile == null)
            scenario = new InfiniteGetByKeyScenario(scanners, new RandomKeyGeneratorStrategy());
        else
            scenario = new InfiniteGetByKeyScenario(scanners, new KeyGeneratorFromFileStrategy(opts.keyFile));
        scenario.run();
        scenario.finish();
    }

}