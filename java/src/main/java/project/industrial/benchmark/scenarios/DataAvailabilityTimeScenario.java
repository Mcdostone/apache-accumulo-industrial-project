package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import project.industrial.benchmark.core.CSVInjector;
import project.industrial.benchmark.core.Injector;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.core.ScenarioNotRespectedException;
import project.industrial.benchmark.tasks.GetKeyTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DataAvailabilityTimeScenario extends Scenario {

    private final GetKeyTask getKeyTask;
    private final Injector injector;
    private final ScheduledExecutorService executorService;

    public DataAvailabilityTimeScenario(Injector injector, GetKeyTask getKeyTask) {
        super("Data availability time");
        this.injector = injector;
        this.getKeyTask = getKeyTask;
        this.executorService = Executors.newScheduledThreadPool(1);
        this.injector.prepareMutations();
    }

    @Override
    public void action() throws Exception {
        this.injector.inject();
        int delayKey = 10 * 1000;
        logger.info(String.format("Data is inserted, executing getKeyTask in %d ms", delayKey));
        long begin = System.currentTimeMillis();
        ScheduledFuture futur = this.executorService.schedule(this.getKeyTask, delayKey, TimeUnit.MILLISECONDS);
        this.testResultKey(begin, System.currentTimeMillis(), futur.get());
        this.executorService.shutdown();
    }

    public void testResultKey(long begin, long end, Object o) throws Exception {
        if(o == null)
            throw new ScenarioNotRespectedException("data was not found");
        logger.info(o.toString());
        this.checkMaxDuration(10 * 1000, begin, end);
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

        GetKeyTask getKey = new GetKeyTask(sc, opts.key);
        Injector injector = new CSVInjector(bw, opts.csv);
        Scenario scenario = new DataAvailabilityTimeScenario(injector, getKey);
        scenario.action();
    }
}
