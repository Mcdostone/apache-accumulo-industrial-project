package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import project.industrial.benchmark.core.CSVInjector;
import project.industrial.benchmark.core.Injector;
import project.industrial.benchmark.core.Scenario;

public class DataRateInjectionScenario extends Scenario {

    private Injector injector;

    public DataRateInjectionScenario(Injector injector) {
        super("Data rate injection", 1000);
        this.injector = injector;
        this.injector.prepareMutations();
    }

    @Override
    public void action() throws Exception {
        long begin = System.currentTimeMillis();
        this.injector.inject();
        long end = System.currentTimeMillis();
        this.checkDuration(begin, end);
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Injector injector = new CSVInjector(bw, opts.csv);
        Scenario scenario = new DataRateInjectionScenario(injector);
        try {
            scenario.action();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--csv", required = true, description = "The CSV file containing data you want to store")
        String csv = null;
    }
}
