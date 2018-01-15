package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.core.Task;
import project.industrial.benchmark.tasks.GetByKeyTask;
import project.industrial.benchmark.tasks.ReaderTask;

import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 *
 * The time for accessing to an object by key must be lower or equals than 100ms.
 *
 * @author Yann Prono
 */
public class TimeGetByKeyScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(TimeGetByKeyScenario.class);
    private Scanner scanner;
    private long maxDuration;

    public TimeGetByKeyScenario(Scanner scanner) {
        super("Time for accessing to an object by key");
        this.scanner = scanner;
        this.maxDuration = 100;
    }

    @Override
    public void action() throws Exception {
        java.util.Scanner sc = new java.util.Scanner(System.in);
        System.out.print("> Key of object you want to retrieve: ");
        String key = sc.nextLine();
        key = key.trim();
        ReaderTask t =  new GetByKeyTask(this.scanner, key);
        Iterator i = t.call();
        long begin = System.currentTimeMillis();
        int nbResults = this.countResults(i);
        long end = System.currentTimeMillis();

        boolean durationRespected = (end - begin) <= this.maxDuration;
        logger.info(String.format("Operation done in %d ms", end - begin));
        this.assertTrue(String.format("The access by key must finish under %d ms", maxDuration), durationRespected);
    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable ();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        Scenario scenario = new TimeGetByKeyScenario(sc);
        scenario.action();
    }
}
