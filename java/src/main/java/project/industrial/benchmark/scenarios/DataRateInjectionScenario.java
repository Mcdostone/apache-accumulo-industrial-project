package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.AbstractCSVInjector;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.SimpleCSVInjector;

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
        this.nbInjectionsPerSecond = nbInjectionsPerSecond;
    }

    @Override
    public void action() throws Exception {
        this.injector.inject();
        this.injector.close();
        this.cut();
    }

    public static void main(String[] args) throws Exception {
        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());

        AbstractCSVInjector injector = new SimpleCSVInjector(bw, opts.csv);
        injector.loadData();
        Scenario scenario = new DataRateInjectionScenario(injector);
        scenario.action();
    }

}
