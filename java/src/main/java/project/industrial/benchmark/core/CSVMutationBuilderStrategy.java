package project.industrial.benchmark.core;

import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;

/**
 * MutationBuilderStrategy for a CSV file;
 * For this class, the value of the row is the entire data
 */
public class CSVMutationBuilderStrategy implements MutationBuilderStrategy {

    private final String cq;
    private final String cf;


    public CSVMutationBuilderStrategy() {
        this.cf = "columnFamily";
        this.cq = "columnQualifier";
    }

    public CSVMutationBuilderStrategy(String cf, String cq) {
        this.cf = cf;
        this.cq = cq;
    }

    @Override
    public Mutation buildMutation(String data) {
        String key = String.format("%s_%s", data.split(" ")[0], data.split(" ")[1]);
        Mutation mutation = new Mutation(key);
        mutation.put(new Text(this.cf), new Text(this.cq), new ColumnVisibility(), new Value(data));
        return mutation;
    }
}
