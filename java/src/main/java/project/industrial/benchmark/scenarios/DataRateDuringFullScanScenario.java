package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.AbstractFullScanTask;
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

    private AbstractFullScanTask fullScan;
    private final long durationBetweenTwoAccesses;

    public DataRateDuringFullScanScenario(AbstractFullScanTask fullScan) {
        super("Data rate of object during a full scan");
        this.fullScan = fullScan;
        this.durationBetweenTwoAccesses = (long) (1.0/1000000.0);
    }

    @Override
    public void action() throws Exception {
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
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        AbstractFullScanTask getAll = new MeasureTimeForObjectAccessFullScanTask(sc);
        Scenario scenario = new DataRateDuringFullScanScenario(getAll);
        scenario.action();
    }
}
