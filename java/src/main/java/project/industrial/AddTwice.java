package project.industrial;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Mutation;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.examples.InsertWithBatchWriter;

/**
 * Insert two identical values with a same timestamp
 * @author Samia Benjida
 */

public class AddTwice {

    private static final Logger logger = LoggerFactory.getLogger(AddTwice.class);

    public static void main( String[] args) throws AccumuloException, AccumuloSecurityException, TableExistsException, TableNotFoundException{
        logger.info("Beginning");
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(InsertWithBatchWriter.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        BatchWriter bw1 = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        BatchWriter bw2 = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        BatchWriter bw3 = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        if (!connector.tableOperations().exists(opts.getTableName()))
            connector.tableOperations().create(opts.getTableName());
        Mutation mut1= new Mutation(new Text("testAdd"));
        Mutation mut2 = new Mutation(new Text("testAdd"));
        Mutation mut3 = new Mutation(new Text("testAdd2"));
        Mutation mut4 = new Mutation(new Text("testAdd2"));
        mut1.put("CF1","CQ1",123456, "foo9");
        bw1.addMutation(mut1);
        bw1.close();
        mut2.put("CF1","CQ1",123456, "foo2");
        bw2.addMutation(mut2);
        mut3.put("CF1","CQ1",123456, "foo3");
        mut4.put("CF1","CQ1",123456, "foo5");
        bw3.addMutation(mut3);
        bw3.addMutation(mut4);
        bw3.close();
        bw2.close();
        logger.info("Mutations have been flushed");
    }
}