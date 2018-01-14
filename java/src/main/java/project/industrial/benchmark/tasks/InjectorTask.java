package project.industrial.benchmark.tasks;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.CSVInjector;
import project.industrial.benchmark.core.Injector;
import project.industrial.benchmark.core.Task;
import project.industrial.benchmark.main.Main;

public class InjectorTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final Opts opts;
    private BatchWriter bw;

    public InjectorTask(String[] args) throws Exception {
        this.opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        this.opts.parseArgs(Main.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        if(!connector.tableOperations().exists(opts.getTableName())) {
            logger.info("Creating table " + opts.getTableName());
            connector.tableOperations().create(opts.getTableName());
        }
        this.bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
    }

    @Override
    public void execute() throws Exception {
        Injector injector = new CSVInjector(bw, opts.csv);
        injector.prepareMutations();
        injector.inject();
    }

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--csv", required = true, description = "The CSV file containing data you want to store")
        String csv = null;
    }

    public static void main(String[] args) throws Exception {
        Task task = new InjectorTask(args);
        task.execute();
    }
}
