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

public class LoopDataRateInjectionScenario extends Scenario {

    private final String filename;
    private Injector injector;

    public LoopDataRateInjectionScenario(BatchWriter bw, String filename) {
        super(LoopDataRateInjectionScenario.class);
        this.filename = filename;
        this.injector = new InjectorWithMetrics(bw);
    }

    @Override
    public void action() throws Exception {
        PeopleMutationBuilder.loopInjectFromCSV(this.filename, this.injector);
        this.injector.close();
    }

    public static void main(String[] args) throws Exception {
        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(LoopDataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw  = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scenario scenario = new LoopDataRateInjectionScenario(bw, opts.csv);
        scenario.run();
        scenario.finish();
    }

}
