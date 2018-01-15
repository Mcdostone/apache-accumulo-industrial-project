package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import project.industrial.benchmark.core.CSVInjector;
import project.industrial.benchmark.core.Injector;
import project.industrial.benchmark.core.Scenario;

public class DataRateInjectionScenario extends Scenario {

    private Injector injector;

    public DataRateInjectionScenario(Injector injector) {
        super("Data rate injection");
        this.injector = injector;
        this.injector.prepareMutations();
    }

    @Override
    public void action() throws Exception {
        long begin = System.currentTimeMillis();
        this.injector.inject();
        long end = System.currentTimeMillis();
        this.assertMaxDuration(1000, begin, end);
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
