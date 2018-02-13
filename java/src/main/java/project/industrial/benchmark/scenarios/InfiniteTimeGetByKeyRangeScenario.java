package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.MetricsManager;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.InfiniteGetByKeyListTask;
import project.industrial.benchmark.tasks.InfiniteGetByKeyRangeTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfiniteTimeGetByKeyRangeScenario extends Scenario {

    private BatchScanner[] bscanners;
    private ExecutorService executorService;
    private List<String> rowKeys;

    public InfiniteTimeGetByKeyRangeScenario(BatchScanner[] bscanners, List<String> rowKeys) {
        super(InfiniteTimeGetByKeyRangeScenario.class.getSimpleName());
        this.bscanners = bscanners;
        this.rowKeys = rowKeys;
        this.executorService = Executors.newFixedThreadPool(bscanners.length);
    }

    @Override
    public void action() throws Exception {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i = 0; i < this.bscanners.length; i++) {
            tasks.add(new InfiniteGetByKeyRangeTask(
                    this.bscanners[i],
                    rowKeys,
                    MetricsManager.getMetricRegistry().meter(String.format("get_by_range.thread_%d", i))));
        }
        this.executorService.invokeAll(tasks);
    }

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--csv", required = true, description = "CSV with RowId you want to retrieve")
        String csv = null;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        opts.parseArgs(InfiniteTimeGetByKeyRangeScenario.class.getName(), args);
        Connector connector = opts.getConnector();

        // init first connection
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);
        sc.setRange(Range.exact("aa"));
        for (Map.Entry<Key, Value> entry : sc) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        BatchScanner[] bscanners = new BatchScanner[2];
        for(int i = 0; i < bscanners.length; i++)
            bscanners[i] = connector.createBatchScanner(opts.getTableName(), opts.auths, 1);

        List<String> rowKeys = Scenario.readRowKeysFromFile(opts.csv);
        Scenario scenario = new InfiniteTimeGetByKeyRangeScenario(bscanners, rowKeys);

        scenario.run();
        scenario.finish();
    }
}