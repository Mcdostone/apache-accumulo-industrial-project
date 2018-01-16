package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Task;
import project.industrial.benchmark.injectors.CSVValueInjector;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.scenarios.ConcurrentActionsScenario;
import project.industrial.benchmark.scenarios.InjectorOpts;


/**
 * This task executes the injector as needed to inject
 */
public class InjectorLoopTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(InjectorLoopTask.class);
    private final long maxInjections;
    private long countInjections;
    private Injector injector;


    public InjectorLoopTask(Injector injector) {
        this(injector, 0,60000000000L);
    }

    public InjectorLoopTask(Injector injector, long maxInjections) {
        this(injector, 0,maxInjections);
    }

    public InjectorLoopTask(Injector injector, long nbInjections, long maxInjections) {
        this.injector = injector;
        this.countInjections = nbInjections;
        this.maxInjections = maxInjections;
    }

    @Override
    public Object call() throws Exception {
        while (this.countInjections < this.maxInjections) {
            this.injector.prepareMutations();
            this.countInjections += this.injector.inject();
            logger.info(String.format("%d objects currently injected", this.countInjections));
        }
        return null;
    }

    public long getMaxInjections() {
        return this.maxInjections;
    }

    public long getCountInjections() {
        return this.countInjections;
    }

    public static void main(String[] args) throws Exception {
        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(ConcurrentActionsScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());

        Injector injector = new CSVValueInjector(bw, opts.csv);
        InjectorLoopTask injectorTask = new InjectorLoopTask(injector);
        injectorTask.call();
    }
}
