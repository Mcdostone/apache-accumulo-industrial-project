package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchScannerOpts;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.GetByKeysListTask;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TimeGetByKeysListScenario extends Scenario {

    private final GetByKeysListTask getByListTask;
    private long maxDuration;

    public TimeGetByKeysListScenario(GetByKeysListTask getByList) {
        super("Time for getting objects by keys list");
        this.maxDuration = 2 * 1000;
        this.getByListTask = getByList;
    }

    @Override
    public void action() throws Exception {
        ScheduledFuture<Iterator<Map.Entry<Key, Value>>> future =
                this.executorService.schedule(this.getByListTask, 0, TimeUnit.SECONDS);
        this.checkResults(future.get());
        this.cut();
    }

    private void checkResults(Iterator<Map.Entry<Key, Value>> iterator) {

    }

    public static List<Range> generateListOfKeys(int max, int nbGenerations) {
        List<Range> keys = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < nbGenerations; i++)
            keys.add(new Range(String.valueOf(rand.nextInt(max))));
        return keys;
    }

    static class Opts extends InjectorOpts {
        @Parameter(names = "--maxRowId", required = true, description = "the last rowID in accumulo")
        int maxRowId = 0;
        @Parameter(names = "--nbKeys", description = "Size of the keys list")
        int nbKeys = 2000;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        BatchScannerOpts bsOpts = new BatchScannerOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bsOpts);
        Connector connector = opts.getConnector();

        BatchScanner scanner = connector.createBatchScanner(opts.getTableName(), opts.auths, bsOpts.scanThreads);
        GetByKeysListTask getByList = new GetByKeysListTask(scanner, generateListOfKeys(opts.maxRowId, opts.nbKeys));

        Scenario scenario = new TimeGetByKeysListScenario(getByList);
        scenario.action();
    }
}
