package project.industrial.benchmark.scenarios;

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
import java.util.concurrent.*;

public class InfiniteGetByKeyScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(InfiniteGetByKeyScenario.class);
    private Scanner[] scanners;
    private ScheduledExecutorService executorService;
    private KeyGeneratorStrategy keyGeneratorStrategy;

    public InfiniteGetByKeyScenario(Scanner[] scanners, KeyGeneratorStrategy keyGeneratorStrategy) {
        super(InfiniteGetByKeyScenario.class);
        this.scanners = scanners;
        this.executorService = Executors.newScheduledThreadPool(scanners.length);
        this.keyGeneratorStrategy = keyGeneratorStrategy;
    }

    @Override
    public void action() throws InterruptedException {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i = 0; i<this.scanners.length;i++){
            tasks.add(new InfiniteGetByKeyTask(
                    this.scanners[i],
                    MetricsManager.getMetricRegistry().timer(String.format("get_by_key.thread_%d",i)),
                    this.keyGeneratorStrategy
            ));
        }
        this.executorService.invokeAll(tasks);
        logger.info("Invoke all tasks");
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

        Scanner[] scanners = new Scanner[20];
        for(int i = 0; i < 20; i++)
            scanners[i] = connector.createScanner(opts.getTableName(), opts.auths);

        Scenario scenario;
        if(opts.keyFile == null)
            scenario = new InfiniteGetByKeyScenario(scanners, new RandomKeyGeneratorStrategy());
        else
            scenario = new InfiniteGetByKeyScenario(scanners, new KeyGeneratorFromFileStrategy(opts.keyFile));
        scenario.run();
        scenario.finish();
    }

}