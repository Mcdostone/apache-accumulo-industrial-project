package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.data.Range;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.GetByKeyTask;
import project.industrial.benchmark.tasks.ReaderTask;

import java.util.concurrent.Executors;

/**
 * Used to do some tests
 * @author Yann Prono
 */
public class SandboxScenario extends Scenario {

    private final ReaderTask task;

    public SandboxScenario(ReaderTask task) {
        super("Sandbox");
        this.task = task;
        this.executorService = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void action() throws Exception {
        ScannerBase s = this.task.call();
        this.showResults(s.iterator());
        Thread.sleep(5000);
        this.showResults(s.iterator());
        this.executorService.shutdown();
    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);
        sc.setRange(new Range());

        ReaderTask get = new GetByKeyTask(sc, "45678");
        Scenario scenario = new SandboxScenario(get);
    }
}
