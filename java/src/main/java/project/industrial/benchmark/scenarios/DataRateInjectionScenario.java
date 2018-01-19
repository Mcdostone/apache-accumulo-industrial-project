package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.AbstractCSVInjector;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.PeopleCSVInjector;

/**
 * This scenario checks the data rate injection in accumulo.
 * We should inject 80000 objects per second
 *
 * @author Yann Prono
 */
public class DataRateInjectionScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(DataRateInjectionScenario.class);
    private Injector injector;

    public DataRateInjectionScenario(Injector injector) {
        super(DataRateInjectionScenario.class.getSimpleName());
        this.injector = injector;
    }

    @Override
    public void action() throws Exception {
        this.injector.inject();
        this.injector.close();
    }

    public static void main(String[] args) throws Exception {
        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());

        AbstractCSVInjector injector = new PeopleCSVInjector(bw, opts.csv);
        int nbLines = injector.loadData();

        int nbMutationsGenerations = (int) Math.ceil(5000000.0 / Long.valueOf(nbLines));
        logger.info(String.format("Multiply this file by %d => %d lines",nbMutationsGenerations, nbLines * nbMutationsGenerations));
        for(int generation = 1; generation <= nbMutationsGenerations; generation++) {
            logger.info("multiplication number " + generation);
            injector.createMutationsFromData();
        }

        logger.info(String.format(
                "6 attributes for each line => %d * %d = %d entries/mutations",
                nbLines * nbMutationsGenerations,
                6,
                nbLines * nbMutationsGenerations * 6)
        );

        Scenario scenario = new DataRateInjectionScenario(injector);
        scenario.run();
        scenario.finish();
    }

}
