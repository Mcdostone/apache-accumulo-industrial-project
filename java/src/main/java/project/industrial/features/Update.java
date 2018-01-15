package project.industrial.features;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.examples.InsertWithBatchWriter;
import project.industrial.features.mining.GeneralScan;
import java.util.Map.Entry;


/**
 * Add and update 50K data in a same row.
 * @author Samia Benjida
 */

public class Update {

    static Mutation mut;
    private static final Logger logger = LoggerFactory.getLogger(AddTwice.class);

    public static void getVal(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException{
        logger.info("beginning");
        GeneralScan.Opts opts = new GeneralScan.Opts();
        ScannerOpts sOpts = new ScannerOpts();
        opts.parseArgs(GeneralScan.class.getName(), args, sOpts);
        Connector connector = opts.getConnector();
        Scanner scan = connector.createScanner("my_table", opts.auths);
        for (Entry<Key, Value> entry : scan) {
            String row = entry.getKey().getRow().toString();
            String cq = entry.getKey().getColumnQualifier().toString();
            String cf = entry.getKey().getColumnQualifier().toString();
            Value value = entry.getValue();
            logger.info( value.toString());
        }
    }

    public static void addDataTst(String[] args) throws AccumuloException, AccumuloSecurityException, TableExistsException, TableNotFoundException {

        logger.info("Beginning");
        ClientOnRequiredTable opts1 = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts1 = new BatchWriterOpts();
        opts1.parseArgs(InsertWithBatchWriter.class.getName(), args, bwOpts1);
        Connector connector1 = opts1.getConnector();
        BatchWriter bw1 = connector1.createBatchWriter(opts1.getTableName(), bwOpts1.getBatchWriterConfig());
        if (!connector1.tableOperations().exists(opts1.getTableName()))
            connector1.tableOperations().create(opts1.getTableName());
        for(Integer i=0;i<50000;i++){
            mut=new Mutation(new Text("testUpdate"));
            mut.put("colFamily"+i,"colQualifier"+i,"0");
            bw1.addMutation(mut);
        }
        bw1.close();
        logger.info("Mutations have been flushed");
    }

    public static void main( String[] args) throws AccumuloException, AccumuloSecurityException, TableExistsException, TableNotFoundException {

        logger.info("Loading data");
        addDataTst(args);
        logger.info("Content");
        getVal(args);

        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(InsertWithBatchWriter.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        if (!connector.tableOperations().exists(opts.getTableName()))
            connector.tableOperations().create(opts.getTableName());

        logger.info("Adding 50 to each value");
        GeneralScan.Opts opts1 = new GeneralScan.Opts();
        ScannerOpts sOpts = new ScannerOpts();
        opts1.parseArgs(GeneralScan.class.getName(), args, sOpts);
        Scanner scan = connector.createScanner("my_table", opts.auths);
        for (Entry<Key, Value> entry : scan) {
            String row = entry.getKey().getRow().toString();
            String cq = entry.getKey().getColumnQualifier().toString();
            String cf = entry.getKey().getColumnQualifier().toString();
            Value value = entry.getValue();
            Integer intVal = (Integer.parseInt(value.toString())+50);
            mut=new Mutation(row);
            mut.put(cf, cq, intVal.toString());
            bw.addMutation(mut);
        }
        bw.close();
        logger.info("New Values");
        getVal(args);
    }
}
