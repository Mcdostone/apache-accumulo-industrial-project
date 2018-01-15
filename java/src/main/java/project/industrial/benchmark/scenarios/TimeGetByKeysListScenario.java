package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchScannerOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.GetByKeysListTask;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This scenario measures the duration for getting a set of objects
 * depending on a given list of keys.
 * For a list of 2000 keys, you should retrieve all associated objects in maximum 2 seconds
 * @author Yann Prono
 */
public class TimeGetByKeysListScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(TimeGetByKeysListScenario.class);
    private final GetByKeysListTask getByListTask;
    private final List<Range> keys;
    private long maxDuration;

    /**
     *
     * @param getByList Task corresponding to this scenario
     * @param keys The list of keys
     */
    public TimeGetByKeysListScenario(GetByKeysListTask getByList, List<Range> keys) {
        super("Time for getting objects by keys list");
        this.maxDuration = 2 * 1000;
        this.getByListTask = getByList;
        this.keys = keys;
    }

    @Override
    public void action() throws Exception {
        logger.info("Looking for objects where key belongs to the list");
        ScheduledFuture<ScannerBase> future =
                this.executorService.schedule(this.getByListTask, 0, TimeUnit.SECONDS);
        this.checkResults(future.get().iterator());
        this.saveResultsInCSV(future.get().iterator());
        this.cut();
    }

    private void checkResults(Iterator<Map.Entry<Key, Value>> iterator) throws Exception {
        int count = 0;
        logger.info("Check results");
        long begin = System.currentTimeMillis();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        long end = System.currentTimeMillis();
        boolean timeRespected = (end - begin) <= this.maxDuration;
        logger.info(String.format("Found %d objects", count));
        this.assertTrue(String.format("Get By keys list should be finished before %d", this.maxDuration), timeRespected);
        this.assertEquals(String.format("Should retrieve %d objects", this.keys.size()), this.keys.size(), count);
    }

    private static List<String> generateListOfKeys(int max, int nbGenerations) {
        Random rand = new Random();
        HashMap<Integer, Boolean> used = new HashMap<>();
        int i = 0;
        while(i < nbGenerations) {
            int randomKey = rand.nextInt(max);
            if(!used.containsKey(randomKey)) {
                used.put(randomKey, true);
                i++;
            }
        }
        return used.keySet().stream().map(String::valueOf).collect(Collectors.toList());
    }

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--maxRowId", description = "the last rowID in accumulo")
        int maxRowId = -1;
        @Parameter(names = "--nbKeys", description = "Size of the keys list")
        int nbKeys = 2000;
        @Parameter(names = "--key", description = "Keys of objects you want to retrieve")
        List<String> keys = null;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        BatchScannerOpts bsOpts = new BatchScannerOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bsOpts);
        Connector connector = opts.getConnector();
        if(opts.maxRowId == -1)
            opts.maxRowId = Integer.parseInt(askInput("What is the maximum rowID in accumulo?"));

        if(opts.keys == null) {
            logger.info(String.format("Generate %d random rowIds", opts.nbKeys));
            opts.keys = generateListOfKeys(opts.maxRowId, opts.nbKeys);
        }

        List<Range> keysRange = opts.keys.stream().map(Range::exact).collect(Collectors.toList());
        BatchScanner scanner = connector.createBatchScanner(opts.getTableName(), opts.auths, bsOpts.scanThreads);
        GetByKeysListTask getByList = new GetByKeysListTask(scanner, keysRange);
        Scenario scenario = new TimeGetByKeysListScenario(getByList, keysRange);
        scenario.action();
    }
}
