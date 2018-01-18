package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import project.industrial.benchmark.core.MetricsManager;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.AbstractCSVInjector;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.PeopleCSVInjector;
import project.industrial.benchmark.injectors.SimpleCSVInjector;

/**
 * This scenario checks the data rate injection in accumulo.
 * We should inject 80000 objects per second
 *
 * @author Yann Prono
 */
public class DataRateInjectionScenario extends Scenario {

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
        // Always start by this !
        MetricsManager.initReporters(DataRateInjectionScenario.class);

        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());

        AbstractCSVInjector injector = new PeopleCSVInjector(bw, opts.csv);
        injector.loadData();
        injector.createMutationsFromData();
        injector.createMutationsFromData();
        injector.createMutationsFromData();
        injector.createMutationsFromData();
        Scenario scenario = new DataRateInjectionScenario(injector);
        scenario.run();
        scenario.finish();
    }

}
