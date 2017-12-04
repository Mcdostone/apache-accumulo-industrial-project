package project.industrial;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PartitioningBatchWriter {

    private final BatchWriter bw;
    private String prefix;
    

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


    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--prefix", required = true, description = "Prefix to use for the rowID")
        String prefix = null;
    }


    public static void readFileAndAddMutation(String filename, int firstLines, PartitioningBatchWriter bw) throws Exception {
        InputStream in = PartitioningBatchWriter.class.getResourceAsStream('/' + filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        reader.readLine();
        int currentLine = 0;
        while((line = reader.readLine()) != null && currentLine <= firstLines) {
            String person = line.split("\t")[1];
            currentLine++;
            bw.createMutation(Integer.toString(currentLine), "name", "cq", new ColumnVisibility(""), person);
        }
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(PartitioningBatchWriter.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        PartitioningBatchWriter writer = new PartitioningBatchWriter(opts.prefix, bw);
        readFileAndAddMutation("people.tsv", 50, writer);
    }

}
