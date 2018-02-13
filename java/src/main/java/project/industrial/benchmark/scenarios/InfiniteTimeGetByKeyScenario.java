package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;

import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.MetricsManager;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.InfiniteGetByKeyTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfiniteTimeGetByKeyScenario extends Scenario {

    private Scanner[] scanners;
    private ExecutorService executorService;
    public static ArrayList rowKeys = new ArrayList();

    public InfiniteTimeGetByKeyScenario(Scanner[] scanners) {
        super(InfiniteTimeGetByKeyScenario.class.getSimpleName());
        this.scanners = scanners;
        this.executorService = Executors.newFixedThreadPool(scanners.length);
    }

    @Override
    public void action() throws Exception {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i =0; i<this.scanners.length;i++){
            tasks.add(new InfiniteGetByKeyTask(
                    this.scanners[i],
                    rowKeys,
                    MetricsManager.getMetricRegistry().meter(String.format("get_by_key.thread_%d",i))));
        }
            this.executorService.invokeAll(tasks);
    }

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--csv", description = "CSV with RowId you want to retrieve")
        String csv = null;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        opts.parseArgs(InfiniteTimeGetByKeyScenario.class.getName(), args);
        Connector connector = opts.getConnector();
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        // Initialisation
        sc.setRange(Range.exact("aa"));
        for (Map.Entry<Key, Value> entry : sc) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        Scanner[] scanners = new Scanner[10];
        for(int i = 0; i < 10; i++)
            scanners[i] = connector.createScanner(opts.getTableName(), opts.auths);

        Scenario scenario = new InfiniteTimeGetByKeyScenario(scanners);
        if(opts.csv == null)
            opts.csv = Scenario.askInput("Key of object you want to retrieve:");
        rowKeys = Scenario.readRowKeysFromFile(opts.csv);
        scenario.run();
        scenario.finish();
    }

}