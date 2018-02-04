package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.ScannerBase;
import project.industrial.benchmark.core.MetricsManager;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.FullScanTask;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This scenario checks the data rate during a full scan.
 * We should retrieve 1 000 000 objects per second during a full scan.
 * We assume that there are some objects stored in accumulo !
 * @author Yann Prono
 */
public class DataRateDuringFullScanScenario extends Scenario {

    private FullScanTask fullScan;

    public DataRateDuringFullScanScenario(FullScanTask fullScan) {
        super(DataRateDuringFullScanScenario.class.getSimpleName());
        this.fullScan = fullScan;
    }

    @Override
    public void action() throws Exception {
    }

    private void processResults(ScannerBase entries) {
        this.countResults(entries.iterator());
    }

    public static void main(String[] args) throws Exception {

        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        FullScanTask getAll = new FullScanTask(sc);
        Scenario scenario = new DataRateDuringFullScanScenario(getAll);
        scenario.run();
        scenario.finish();
    }
}
