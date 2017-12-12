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
        logger.info("IN");

        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(InsertWithBatchWriter.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        if (!connector.tableOperations().exists(opts.getTableName()))
            connector.tableOperations().create(opts.getTableName());

        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Mutation mut1= new Mutation(new Text("azerty2"));
        Mutation mut2 = new Mutation(new Text("azerty2"));
        Mutation mut3 = new Mutation(new Text("azerty2"));
        mut1.put("CF1","CQ1",123456, "foo");
        mut2.put("CF1","CQ1",123456, "foo2");
        mut3.put("CF1","CQ1",123456, "foo3");
        bw.addMutation(mut1);
        bw.addMutation(mut2);
        bw.addMutation(mut3);
        bw.close();
        System.out.println("IN3");
        logger.info("Mutations have been flushed");
    }
}
