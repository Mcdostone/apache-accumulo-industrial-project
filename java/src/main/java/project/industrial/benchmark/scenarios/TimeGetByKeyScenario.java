package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.ScannerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.GetByKeyTask;
import project.industrial.benchmark.tasks.ReaderTask;


/**
 * This scenario check the duration for getting an object with a given key.
 * The time for accessing to an object by key must be lower or equals than 100ms.
 * We assume that there are some objects stored in accumulo !
 *
 * @author Yann Prono
 */
public class TimeGetByKeyScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(TimeGetByKeyScenario.class);
    private Scanner scanner;
    private long maxDuration;
    private String key;

    public TimeGetByKeyScenario(Scanner scanner, String key) {
        super("Time for accessing to an object by key");
        this.scanner = scanner;
        this.maxDuration = 100;
        this.key = key;
    }

    @Override
    public void action() throws Exception {
        key = key.trim();
        ReaderTask t =  new GetByKeyTask(this.scanner, key);
        ScannerBase s = t.call();

        long begin = System.currentTimeMillis();
        int nbResults = this.countResults(s.iterator());
        long end = System.currentTimeMillis();

        this.showResults(s.iterator());

        boolean durationRespected = (end - begin) <= this.maxDuration;
        logger.info(String.format("Operation done in %d ms", end - begin));
        this.assertTrue(String.format("The access by key must finish under %d ms", maxDuration), durationRespected);
        this.assertTrue("Should return a object",1 >= nbResults);
    }

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--rowId", description = "RowId you want to retrieve")
        String rowId = null;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        if(opts.rowId == null)
            opts.rowId = Scenario.askInput("Key of object you want to retrieve:");

        Scenario scenario = new TimeGetByKeyScenario(sc, opts.rowId);
    }
}
