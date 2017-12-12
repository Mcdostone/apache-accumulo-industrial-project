package project.industrial;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.*;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Range;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.examples.InsertWithBatchWriter;
import project.industrial.mining.GeneralScan;
import org.apache.accumulo.core.data.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.accumulo.core.cli.BatchScannerOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.*;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Insert two identical values with a same timestamp
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
        // BatchScanner scanner = connector.createBatchScanner("my_table", opts.auths, 2);
        Scanner scan = connector.createScanner("my_table", opts.auths);
        // scan.setRange(new Range("harry","john"));
        //scan.fetchFamily("attributes");

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
            mut.put("e"+i,"er"+i,"0");
            bw1.addMutation(mut);
        }
        bw1.close();
        logger.info("Mutations have been flushed");
    }

    public static void main( String[] args) throws AccumuloException, AccumuloSecurityException, TableExistsException, TableNotFoundException {

        addDataTst(args);

        getVal(args);

        logger.info("Beginning");
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(InsertWithBatchWriter.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        if (!connector.tableOperations().exists(opts.getTableName()))
            connector.tableOperations().create(opts.getTableName());


        logger.info("beginning");
        GeneralScan.Opts opts1 = new GeneralScan.Opts();
        ScannerOpts sOpts = new ScannerOpts();
        opts1.parseArgs(GeneralScan.class.getName(), args, sOpts);
        // BatchScanner scanner = connector.createBatchScanner("my_table", opts.auths, 2);
        Scanner scan = connector.createScanner("my_table", opts.auths);
        // scan.setRange(new Range("harry","john"));
        //scan.fetchFamily("attributes");

        for (Entry<Key, Value> entry : scan) {
            String row = entry.getKey().getRow().toString();
            String cq = entry.getKey().getColumnQualifier().toString();
            String cf = entry.getKey().getColumnQualifier().toString();
            Value value = entry.getValue();
            Integer intVal = (Integer.parseInt(value.toString())+50);

            // logger.info( value.toString());
            mut=new Mutation(row);
            mut.put(cf, cq, intVal.toString());
            bw.addMutation(mut);
        }
        bw.close();
        getVal(args);





    }


    }
