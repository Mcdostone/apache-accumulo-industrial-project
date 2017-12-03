package project.industrial;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;

public class ChoosePartitioning {

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--prefix", required = true, description = "Prefix to use for the rowID")
        String prefix = null;
    }

    public static Mutation createMutation(String prefix, int rowId) {
        Mutation m = new Mutation(new Text(String.format("%s_%d", prefix, rowId)));
        m.put(
                new Text("myColFam"),
                new Text("myColQual"),
                new ColumnVisibility("public"),
                System.currentTimeMillis(),
                new Value("coucou")
        );
        return m;
    }

    public static void main(String[] args) throws AccumuloSecurityException, AccumuloException, TableNotFoundException {
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(ChoosePartitioning.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        for(int i = 0; i < 100; i++)
            bw.addMutation(createMutation(opts.prefix, i));
        bw.close();
    }
}
