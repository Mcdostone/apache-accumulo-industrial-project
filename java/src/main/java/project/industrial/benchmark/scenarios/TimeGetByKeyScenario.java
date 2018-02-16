package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.GetByKeyTask;
import project.industrial.benchmark.tasks.ReaderTask;

import java.util.Map;
import java.util.concurrent.Callable;


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
        super("Time to access an object by key");
        this.scanner = scanner;
        this.maxDuration = 100;
        this.key = key;
    }

    @Override
    public void action() throws Exception {
        key = key.trim();
        Callable t =  new GetByKeyTask(this.scanner, key);
        t.call();
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

        sc.setRange(Range.exact("985"));
        for (Map.Entry<Key, Value> entry : sc) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        System.out.println("###### START HERE");

        if(opts.rowId == null)
            opts.rowId = Scenario.askInput("Key of object you want to retrieve:");

        String[] oneRow;
        oneRow= opts.rowId.split(",");
        long begin = System.currentTimeMillis();
        for (int i=0; i<oneRow.length;i++) {
            Scenario scenario = new TimeGetByKeyScenario(sc, oneRow[i]);
            scenario.run();
            scenario.finish();
        }
        long end = System.currentTimeMillis();
        long tpsTot = end - begin;
        float tpsMoy = (float) tpsTot/(oneRow.length);
        System.out.println("Nombre de requetes Get Unitaire effectuees = " + oneRow.length);
        System.out.println("Temps Moyen par requete = " + tpsMoy + "ms");
    }
}