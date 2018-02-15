package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import project.industrial.benchmark.core.PeopleMutationBuilder;
import project.industrial.benchmark.core.PeopleMutationBuilderWithCheck;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.InjectorWithMetrics;
import project.industrial.benchmark.tasks.CheckAvailability;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoopDataRateInjectionAndCheckAvailabilityScenario extends Scenario {

    private final String filename;
    private final Scanner scanner;
    private Injector injector;
    private ScheduledExecutorService executorService;

    public LoopDataRateInjectionAndCheckAvailabilityScenario(BatchWriter bw, String filename, Scanner sc) {
        super(LoopDataRateInjectionAndCheckAvailabilityScenario.class);
        this.filename = filename;
        this.injector = new InjectorWithMetrics(bw);
        this.scanner = sc;
        this.executorService =  Executors.newScheduledThreadPool(50);
    }

    @Override
    public void action() {
        PeopleMutationBuilderWithCheck builder = new PeopleMutationBuilderWithCheck(this.scanner, new PeopleMutationBuilder());
        builder.loopInjectFromCSV(this.filename, this.injector);
        this.executorService.scheduleAtFixedRate(new CheckAvailability(this.scanner),10, 10, TimeUnit.SECONDS);
        PeopleMutationBuilder.loopInjectFromCSV(this.filename, this.injector);
    }

    @Override
    public void finish() {
        try {
            this.injector.close();
            this.executorService.shutdown();
        } catch (MutationsRejectedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(LoopDataRateInjectionAndCheckAvailabilityScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw  = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scanner sc  = connector.createScanner(opts.getTableName(), opts.auths);
        Scenario scenario = new LoopDataRateInjectionAndCheckAvailabilityScenario(bw, opts.csv, sc);
        scenario.run();
        scenario.finish();
    }

}
