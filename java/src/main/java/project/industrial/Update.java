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

public class Update {

    static Mutation mut;
    private static final Logger logger = LoggerFactory.getLogger(AddTwice.class);

    public static void main( String[] args) throws AccumuloException, AccumuloSecurityException, TableExistsException, TableNotFoundException {
        logger.info("Beginning");
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(InsertWithBatchWriter.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        if (!connector.tableOperations().exists(opts.getTableName()))
            connector.tableOperations().create(opts.getTableName());



        for(Integer i=0;i<50;i++){
            mut=new Mutation(new Text("testUpdate"));
            mut.put("e"+i,"er"+i,"0");
            bw.addMutation(mut);
        }
        bw.close();
        logger.info("Mutations have been flushed");
    }
}