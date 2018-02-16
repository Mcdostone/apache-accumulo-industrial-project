package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.*;
import project.industrial.benchmark.tasks.InfiniteGetByKeyRangeTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfiniteGetByKeyRangeScenario extends Scenario {

    protected static Logger logger = LoggerFactory.getLogger(InfiniteGetByKeyRangeScenario.class);
    private final KeyGeneratorStrategy keyGen;
    private Scanner[] scanners;
    private ExecutorService executorService;

    public InfiniteGetByKeyRangeScenario(Scanner[] scanner, KeyGeneratorStrategy keyGen) {
        super(InfiniteGetByKeyRangeScenario.class.getSimpleName());
        this.scanners = scanner;
        this.keyGen = keyGen;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void action() throws Exception {
        List<Callable<Object>> tasks = new ArrayList<>();
        for(int i = 0; i < this.scanners.length; i++) {
            tasks.add(new InfiniteGetByKeyRangeTask(
                    this.scanners[i],
                    MetricsManager.getMetricRegistry().timer(String.format("get_by_range.thread_%d", i)),
                    this.keyGen
            ));
        }
        logger.info("Invoke all tasks - " + tasks.size() + " threads");
        this.executorService.invokeAll(tasks);
    }

    @Override
    public void finish() {
        super.finish();
        this.executorService.shutdown();
    }

    public static void main(String[] args) throws Exception {
        KeyFileOpts opts = new KeyFileOpts();
        opts.parseArgs(InfiniteGetByKeyRangeScenario.class.getName(), args);
        Connector connector = opts.getConnector();

        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);
        sc.setRange(Range.exact("aa"));
        for (Map.Entry<Key, Value> entry : sc) { }

        Scanner[] scanners = new Scanner[2];
        for(int i = 0; i < scanners.length; i++)
            scanners[i] = connector.createScanner(opts.getTableName(), opts.auths);

        Scenario scenario;
        if(opts.keyFile == null)
            scenario = new InfiniteGetByKeyRangeScenario(scanners, new RandomKeyGeneratorStrategy());
        else
            scenario = new InfiniteGetByKeyRangeScenario(scanners, new KeyGeneratorFromFileStrategy(opts.keyFile));
        scenario.run();
        scenario.finish();
    }
}