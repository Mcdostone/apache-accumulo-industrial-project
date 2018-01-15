package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Range;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.FullScanTask;

import java.util.concurrent.Executors;

public class SandboxScenario extends Scenario {

    private final FullScanTask getAllTask;

    public SandboxScenario(FullScanTask getAllTask) {
        super("Sandbox");
        this.getAllTask = getAllTask;
        this.executorService = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void action() throws Exception {
        logger.info("Before call");
        this.getAllTask.call();
        logger.info("After call");
        this.executorService.shutdown();
        this.cut();
    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);
        sc.setRange(new Range());

        FullScanTask getAll = new FullScanTask(sc);
        Scenario scenario = new SandboxScenario(getAll);
        scenario.action();
    }
}
