package project.industrial;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Enables the user to write some data into accumulo
 * by adding a given prefix for all rows ID inserted.
 * The row ID will follow the following format: "PREFIX_ROWID"
 *
 * @author Yann Prono, mcdostone
 */
public class PartitioningBatchWriter {

    private final BatchWriter bw;
    private String prefix;
    private static Logger logger = Logger.getLogger(PartitioningBatchWriter.class);

    /**
     * @param prefix Prefix used for row ID.
     * @param bw the BatchWriter created by a connector
     */
    public PartitioningBatchWriter(String prefix, BatchWriter bw) {
        this.prefix = prefix;
        this.bw = bw;
    }

    /**
     * Create a mutation with the prefix for the rowID.
     * Prefix and rowID are separated by an '_'.
     * @param rowId
     * @param cf
     * @param cq
     * @param visibility
     * @param value
     * @return
     * @throws MutationsRejectedException
     */
    public Mutation createMutation(String rowId, String cf, String cq, ColumnVisibility visibility, String value) throws MutationsRejectedException {
        Text row = new Text(String.format("%s_%s", this.prefix, rowId));
        Mutation m = new Mutation(row);
        m.put(cf, cq, visibility, value);
        this.bw.addMutation(m);
        return m;
    }

    /**
     * @return The prefix for this BatchWriter
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * @throws MutationsRejectedException
     */
    public void close() throws MutationsRejectedException {
        this.bw.close();
    }


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
        // Create a BatchWriter thanks to connector
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());


        PartitioningBatchWriter writer = new PartitioningBatchWriter(opts.prefix, bw);
        String filename = "people.tsv";
        logger.info("Going to Read the first "+ opts.size + " lines of " + filename);
        InputStream in = PartitioningBatchWriter.class.getResourceAsStream('/' + filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        // Skip header
        reader.readLine();
        int currentLine = 1;
        logger.info(opts.size + " will be inserted");
        logger.info("Prefix for rowID: " + writer.getPrefix());
        while((line = reader.readLine()) != null && currentLine <= opts.size) {
            String person = line.split("\t")[1];
            currentLine++;
            writer.createMutation(Integer.toString(currentLine), "name", "cq", new ColumnVisibility(""), person);
        }
        // Last mutations are flushed
        writer.close();
        logger.info(currentLine - 1 + " rows has been inserted");

        // Read data now
        logger.info("Reading data freshly inserted");
        Scanner scanner = connector.createScanner(opts.getTableName(),opts.auths);
        scanner.setRange(new Range());
        Printer.printAll(scanner.iterator());
        scanner.close();
    }

}
