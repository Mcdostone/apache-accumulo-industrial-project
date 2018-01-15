package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Range;
import project.industrial.benchmark.injectors.CSVInjector;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.core.ScenarioNotRespectedException;
import project.industrial.benchmark.tasks.FullScanTask;
import project.industrial.benchmark.tasks.GetByKeyTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DataAvailabilityTimeScenario extends Scenario {

    private final GetByKeyTask getByKeyTask;
    private final Injector injector;
    private final FullScanTask getAllTask;

    public DataAvailabilityTimeScenario(Injector injector, GetByKeyTask getByKeyTask, FullScanTask getAllTask) {
        super("Data availability time", 2);
        this.injector = injector;
        this.getByKeyTask = getByKeyTask;
        this.getAllTask = getAllTask;
        this.injector.prepareMutations();
    }

    @Override
    public void action() throws Exception {
        int countInsertions = this.injector.inject();
        this.injector.close();
        int delayKey = 10 * 1000;
        int delayMining = 1000 * 20;

        logger.info(String.format("Data is inserted, executing getByKeyTask in %d ms and getAllTask in %d ms", delayKey, delayMining));
        long begin = System.currentTimeMillis();
        ScheduledFuture futurKey = this.executorService.schedule(this.getByKeyTask, delayKey, TimeUnit.MILLISECONDS);
        ScheduledFuture futurMining = this.executorService.schedule(this.getAllTask, delayMining, TimeUnit.MILLISECONDS);

        this.testResultKey(begin, System.currentTimeMillis(), futurKey.get());
        this.testResultMining(countInsertions, (Integer) futurMining.get());

        this.executorService.shutdown();
        this.cut();
    }

    private void testResultMining(int expected, int nbInsertions) throws Exception {
        this.assertEquals("Should retrieve all data inserted", expected, nbInsertions);
    }

    public void testResultKey(long begin, long end, Object o) throws Exception {
        if(o == null)
            throw new ScenarioNotRespectedException("data was not found");

        long duration = end - begin;
        logger.info(String.format("Data found in %d ms", duration));
        logger.info(o.toString());
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

        GetByKeyTask getKey = new GetByKeyTask(sc, opts.key);
        Injector injector = new CSVInjector(bw, opts.csv);
        FullScanTask getAll = new FullScanTask(sc1);
        Scenario scenario = new DataAvailabilityTimeScenario(injector, getKey, getAll);
        scenario.action();
    }
}
