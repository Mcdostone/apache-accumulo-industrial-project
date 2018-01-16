package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.AbstractFullScanTask;
import project.industrial.benchmark.tasks.MeasureTimeForObjectAccessFullScanTask;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This scenario checks the data rate during a full scan.
 * We should retrieve 1 000 000 objects per second during a full scan.
 * We assume that there are some objects stored in accumulo !
 * @author Yann Prono
 */
public class DataRateDuringFullScanScenario extends Scenario {

    private AbstractFullScanTask<List<Long>> fullScan;
    private final long durationBetweenTwoAccesses;

    public DataRateDuringFullScanScenario(AbstractFullScanTask<List<Long>> fullScan) {
        super("Data rate of object during a full scan");
        this.fullScan = fullScan;
        this.durationBetweenTwoAccesses = (long) (1.0/1000000.0);
    }

    @Override
    public void action() throws Exception {
        ScheduledFuture<List<Long>> futurMeasurements = this.executorService.schedule(this.fullScan, 0, TimeUnit.SECONDS);
        this.processMeasurements(futurMeasurements.get());
        this.executorService.shutdown();
        this.cut();
    }

    private void processMeasurements(List<Long> measurements) throws Exception {
        this.saveResultsInCSV(measurements.stream().map(String::valueOf).collect(Collectors.toList()));
        long countMeasurementsRespected = measurements.stream()
                .filter(measure -> measure <= this.durationBetweenTwoAccesses)
                .count();
        this.assertEquals(
                String.format("Every object access should be done in %d second", durationBetweenTwoAccesses),
                measurements.size(),
                countMeasurementsRespected
        );
    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        AbstractFullScanTask<List<Long>> getAll = new MeasureTimeForObjectAccessFullScanTask(sc);
        Scenario scenario = new DataRateDuringFullScanScenario(getAll);
        scenario.action();
    }
}
