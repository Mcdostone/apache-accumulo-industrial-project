package project.industrial.benchmark.main;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--csv", required = true, description = "The CSV file containing data you want to store")
        String csv = null;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(Main.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        if(!connector.tableOperations().exists(opts.getTableName())) {
            logger.info("Creating table " + opts.getTableName());
            connector.tableOperations().create(opts.getTableName());
        }

        /*BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Injector injector = new CSVValueInjector(bw, opts.csv);
        injector.prepareMutations();
        injector.inject();*/
    }
}
