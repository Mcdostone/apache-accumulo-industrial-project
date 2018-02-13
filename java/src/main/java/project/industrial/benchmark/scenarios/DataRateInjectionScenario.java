package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.PeopleMutationBuilder;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.InjectorWithMetrics;

/**
 * This scenario checks the data rate injection in accumulo.
 * We should inject 80000 objects per second
 *
 * @author Yann Prono
 */
public class DataRateInjectionScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(DataRateInjectionScenario.class);
    private final String filename;
    private Injector injector;

    public DataRateInjectionScenario(BatchWriter bw, String filename) {
        super(DataRateInjectionScenario.class.getSimpleName());
        this.filename = filename;
        this.injector = new InjectorWithMetrics(bw);
    }

    @Override
    public void action() throws Exception {
        PeopleMutationBuilder.injectFromCSV(this.filename, this.injector);
        this.injector.close();
    }

    public static void main(String[] args) throws Exception {
        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw  = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scenario scenario = new DataRateInjectionScenario(bw, opts.csv);
        scenario.run();
        scenario.finish();
    }

}
