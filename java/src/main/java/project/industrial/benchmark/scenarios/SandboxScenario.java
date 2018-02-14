package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.core.Task;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.tasks.GetByKeyTask;

/**
 * Used to do some tests
 * @author Yann Prono
 */
public class SandboxScenario extends Scenario {

    public SandboxScenario(String name) {
        super(name);
    }

    @Override
    protected void action() throws Exception {

    }

    public static void main(String[] args) throws Exception {
        TimeGetByKeyScenario.Opts opts = new TimeGetByKeyScenario.Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        String rowKey = Scenario.askInput("Enter a rowKey: ");
        Task t = new GetByKeyTask(sc, rowKey);
        t.call();
    }
}