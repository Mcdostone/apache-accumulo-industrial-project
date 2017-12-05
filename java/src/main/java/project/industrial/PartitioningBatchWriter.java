package project.industrial;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import project.industrial.injectors.PeopleInjector;
import project.industrial.injectors.PrefixRowIdStrategy;

/**
 * Enables the user to write some data into accumulo
 * by adding a given prefix for all rows ID inserted.
 * The row ID will follow the following format: "PREFIX_ROWID"
 *
 * @author Yann Prono, mcdostone
 */
public class PartitioningBatchWriter {

    /**
     * Options supported for this class
     */
    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--prefix", required = true, description = "Prefix to use for the rowID")
        String prefix = null;
        @Parameter(names = "--size", description = "Number for rows you want to insert (one column for each row")
        int size = 10000;
    }

    /**
     * Steps of this method are:
     * - Read the file people.tsv
     * - for the N first lines, insert the name of each person into accumulo with a prefix for the Row ID
     * - After insertion, read all data freshly inserted
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Init the connection with accumulo
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(PartitioningBatchWriter.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        // Create the table if not exist
        if(!connector.tableOperations().exists(opts.getTableName()))
            connector.tableOperations().create(opts.getTableName());

        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        PeopleInjector injector = new PeopleInjector(bw);
        injector.setRowIdStrategy(new PrefixRowIdStrategy(opts.prefix));
        injector.insert();
    }

}
