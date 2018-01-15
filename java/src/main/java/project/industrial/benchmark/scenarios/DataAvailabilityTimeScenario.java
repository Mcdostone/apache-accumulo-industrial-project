package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Range;
import project.industrial.benchmark.core.CSVInjector;
import project.industrial.benchmark.core.Injector;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.core.ScenarioNotRespectedException;
import project.industrial.benchmark.tasks.GetAllTask;
import project.industrial.benchmark.tasks.GetKeyTask;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DataAvailabilityTimeScenario extends Scenario {

    private final GetKeyTask getKeyTask;
    private final Injector injector;
    private final ScheduledExecutorService executorService;
    private final GetAllTask getAllTask;

    public DataAvailabilityTimeScenario(Injector injector, GetKeyTask getKeyTask, GetAllTask getAllTask) {
        super("Data availability time");
        this.injector = injector;
        this.getKeyTask = getKeyTask;
        this.getAllTask = getAllTask;
        this.executorService = Executors.newScheduledThreadPool(2);
        this.injector.prepareMutations();
    }

    @Override
    public void action() throws Exception {
        int countInsertions = this.injector.inject();
        int delayKey = 10 * 1000;
        int delayMining = 10;
        logger.info(String.format("Data is inserted, executing getKeyTask in %d ms and getAllTask in %d min", delayKey, delayMining));

        long begin = System.currentTimeMillis();
        ScheduledFuture futurKey = this.executorService.schedule(this.getKeyTask, delayKey, TimeUnit.MILLISECONDS);
        ScheduledFuture futurMining = this.executorService.schedule(this.getAllTask, delayMining, TimeUnit.MINUTES);
        this.testResultKey(begin, System.currentTimeMillis(), futurKey.get());
        this.testResultMining(countInsertions, (Integer) futurMining.get());
        this.executorService.shutdown();
        this.cut();
    }

    private void testResultMining(int expected, int nbInsertions) throws Exception {
        this.assertEquals(expected, nbInsertions, "The full scan doesn't retrieve all data inserted");
    }

    public void testResultKey(long begin, long end, Object o) throws Exception {
        if(o == null)
            throw new ScenarioNotRespectedException("data was not found");
        logger.info(o.toString());
        this.assertMaxDuration(10 * 1000, begin, end);
    }

    static class Opts extends InjectorOpts {
        @Parameter(names = "--key", required = true, description = "the rowId you want to to retrieve")
        String key = null;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);
        Scanner sc1 = connector.createScanner(opts.getTableName(), opts.auths);
        sc1.setRange(new Range());

        GetKeyTask getKey = new GetKeyTask(sc, opts.key);
        Injector injector = new CSVInjector(bw, opts.csv);
        GetAllTask getAll = new GetAllTask(sc1);
        Scenario scenario = new DataAvailabilityTimeScenario(injector, getKey, getAll);
        scenario.action();
    }
}
