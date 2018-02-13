package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.PeopleMutationBuilderConcurrent;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.InjectorWithMetrics;

public class InjectorConcurrentScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(DataRateInjectionScenario.class);
    private final String filename;
    private Injector injector;
    private int start;
    private int end;

    public InjectorConcurrentScenario(BatchWriter bw, String filename, int start, int end) {
        super(InjectorConcurrentScenario.class.getSimpleName());
        this.filename = filename;
        this.injector = new InjectorWithMetrics(bw);
        this.start = start;
        this.end = end;
    }

    @Override
    protected void action() throws Exception {
        PeopleMutationBuilderConcurrent.buildFromCSV(this.filename, this.injector, this.start, this.end);
        this.injector.close();
    }

    public static class InjectorConcurrentOpts extends InjectorOpts {
        @Parameter(names = "--start", required = true)
        public int  start = 0;
        @Parameter(names = "--end", required = true)
        public int  end = 0;

    }

    public static void main(String[] args) throws Exception {
        InjectorConcurrentOpts opts = new InjectorConcurrentOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw  = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scenario scenario = new InjectorConcurrentScenario(bw, opts.csv, opts.start, opts.end);
        scenario.run();
        scenario.finish();
    }

}
