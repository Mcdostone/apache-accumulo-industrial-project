package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.CSVInjector;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.tasks.FullScanTask;
import project.industrial.benchmark.tasks.MeasureTimeForObjectAccessFullScanTask;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DataRateDuringFullScanScenario extends Scenario {

    private FullScanTask fullScan;
    private Injector injector;
    private final ScheduledExecutorService executorService;
    private final long durationBetweenTwoAccesses;

    public DataRateDuringFullScanScenario(Injector injector, FullScanTask fullScan) {
        super("Data rate of object during a full scan");
        this.injector = injector;
        this.fullScan = fullScan;
        this.executorService = new ScheduledThreadPoolExecutor(1);
        this.durationBetweenTwoAccesses = (long) (1.0/1000000.0);
    }

    @Override
    public void action() throws Exception {
        this.injector.prepareMutations();
        this.injector.inject();
        this.injector.close();

        ScheduledFuture futurMeasurements = this.executorService.schedule(this.fullScan, 0, TimeUnit.SECONDS);
        this.processMeasurements((List<Long>) futurMeasurements.get());
        this.executorService.shutdown();
        this.cut();
    }

    public void processMeasurements(List<Long> measurements) throws Exception {
        this.writeDataInFile(measurements);
        long countMeasurementsRespected = measurements.stream()
                .filter(measure -> measure <= this.durationBetweenTwoAccesses)
                .count();
        this.assertEquals(
                String.format("Every object access should be done in %d second", durationBetweenTwoAccesses),
                measurements.size(),
                countMeasurementsRespected
        );
    }

    private void writeDataInFile(List<Long> measurements) throws IOException {
        String filename = Paths.get(
                System.getProperty("user.dir"),
                DataRateDuringFullScanScenario.class.getSimpleName() + ".csv"
        ).toString();
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        measurements.stream().forEach(measurement -> {
            try { bw.write(String.valueOf(measurement) + "\n"); }
            catch (IOException e) { e.printStackTrace(); }
        });
        bw.close();
    }

    public static void main(String[] args) throws Exception {
        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        Injector injector = new CSVInjector(bw, opts.csv);
        FullScanTask getAll = new MeasureTimeForObjectAccessFullScanTask(sc);
        Scenario scenario = new DataRateDuringFullScanScenario(injector, getAll);
        scenario.action();
    }
}
