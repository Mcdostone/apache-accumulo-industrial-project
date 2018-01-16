package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import project.industrial.benchmark.injectors.CSVInjector;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.core.Scenario;

/**
 * This scenario checks the data rate injection in accumulo.
 * We should inject 80000 objects per second
 *
 * @author Yann Prono
 */
public class DataRateInjectionScenario extends Scenario {

    private final long nbInjectionsPerSecond;
    private Injector injector;

    public DataRateInjectionScenario(Injector injector) {
        this(injector, 80000);
    }

    public DataRateInjectionScenario(Injector injector, long nbInjectionsPerSecond) {
        super("Data rate injection");
        this.injector = injector;
        this.injector.prepareMutations();
        this.nbInjectionsPerSecond = nbInjectionsPerSecond;
    }

    @Override
    public void action() throws Exception {
        long begin = System.currentTimeMillis();
        int nbInjections = this.injector.inject();
        logger.info(String.format("%d objects currently injected", nbInjections));
        this.injector.close();
        long end = System.currentTimeMillis();

        long duration = end - begin;
        long currentInjectionsPerSecond = nbInjections * 1000 / duration;
        logger.info(String.format("Inserted %d in %d ms", nbInjections, duration));

        boolean checkNbInsertions = currentInjectionsPerSecond >= this.nbInjectionsPerSecond;
        this.assertTrue(String.format("Should insert %d object/s but have %d", this.nbInjectionsPerSecond, currentInjectionsPerSecond),
                checkNbInsertions);

        this.cut();
    }

    public static void main(String[] args) throws Exception {
        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());

        Injector injector = new CSVInjector(bw, opts.csv);
        Scenario scenario = new DataRateInjectionScenario(injector);
        scenario.action();
    }

}
