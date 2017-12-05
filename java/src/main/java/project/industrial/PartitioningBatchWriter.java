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

public class PartitioningBatchWriter {

    private final BatchWriter bw;
    private String prefix;
    private static Logger logger = Logger.getLogger(PartitioningBatchWriter.class);

    public PartitioningBatchWriter(String prefix, BatchWriter bw) {
        this.prefix = prefix;
        this.bw = bw;
    }

    public Mutation createMutation(String rowId, String cf, String cq, ColumnVisibility visibility, String value) throws MutationsRejectedException {
        Text row = new Text(String.format("%s_%s", this.prefix, rowId));
        Mutation m = new Mutation(row);
        m.put(cf, cq, visibility, value);
        this.bw.addMutation(m);
        return m;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void close() throws MutationsRejectedException {
        this.bw.close();
    }

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--prefix", required = true, description = "Prefix to use for the rowID")
        String prefix = null;
        @Parameter(names = "--size", description = "Number for rows you want to insert (one column for each row")
        int size = 10000;
    }

    public static int readFileAndAddMutation(String filename, int firstLines, PartitioningBatchWriter bw) throws Exception {
        logger.info("Going to Read the first "+ firstLines + " lines of " + filename);
        InputStream in = PartitioningBatchWriter.class.getResourceAsStream('/' + filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        reader.readLine();
        int currentLine = 1;
        logger.info(firstLines + " will be inserted");
        logger.info("Prefix for rowID: " + bw.getPrefix());
        while((line = reader.readLine()) != null && currentLine <= firstLines) {
            String person = line.split("\t")[1];
            currentLine++;
            bw.createMutation(Integer.toString(currentLine), "name", "cq", new ColumnVisibility(""), person);
        }

        return currentLine - 1;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(PartitioningBatchWriter.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        // Create the table if not exist
        if(!connector.tableOperations().exists(opts.getTableName()))
            connector.tableOperations().create(opts.getTableName());
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());

        PartitioningBatchWriter writer = new PartitioningBatchWriter(opts.prefix, bw);
        int nbInsertions = readFileAndAddMutation("people.tsv", opts.size, writer);
        writer.close();
        logger.info(nbInsertions + " rows has been inserted");

        logger.info("Reading data freshly inserted");
        Scanner scanner = connector.createScanner(opts.getTableName(),opts.auths);
        scanner.setRange(new Range());
        Printer.printAll(scanner.iterator());
        scanner.close();
    }

}
