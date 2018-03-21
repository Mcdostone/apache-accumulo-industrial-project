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

/**
 * Scénario éxecutant de manière infinie 2 GET BY LIST en parallèle.
 *
 * @author Yann Prono
 */
public class InfiniteGetByKeyListScenario extends Scenario {

    private final KeyGeneratorStrategy keyGen;
    private BatchScanner[] bscanners;
    private ExecutorService executorService;

    public InfiniteGetByKeyListScenario(BatchScanner[] bscanners, KeyGeneratorStrategy keyGen) {
        super(InfiniteGetByKeyListScenario.class.getSimpleName());
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
                    MetricsManager.getMetricRegistry().timer(String.format("get_by_list.thread_%d", i)),
                    this.keyGen
            ));
        }
        this.executorService.invokeAll(tasks);
    }


    public static void main(String[] args) throws Exception {
        KeyFileOpts opts = new KeyFileOpts();
        opts.parseArgs(InfiniteGetByKeyListScenario.class.getName(), args);
        Connector connector = opts.getConnector();

        // init first connection
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);
        sc.setRange(Range.exact("aa"));
        for (Map.Entry<Key, Value> entry : sc) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        // Instancie le nombre de GET BY LIST en parallèle
        BatchScanner[] scanners = new BatchScanner[2];
        for(int i = 0; i < scanners.length; i++)
            scanners[i] = connector.createBatchScanner(opts.getTableName(), opts.auths, 10);

        Scenario scenario;
        if(opts.keyFile == null)
            scenario = new InfiniteGetByKeyListScenario(scanners, new RandomKeyGeneratorStrategy());
        else
            scenario = new InfiniteGetByKeyListScenario(scanners, new KeyGeneratorFromFileStrategy(opts.keyFile));
        scenario.run();
        scenario.finish();
    }
}