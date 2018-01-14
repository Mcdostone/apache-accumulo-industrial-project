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
        return this.buildMutation(key, this.cf, this.cq, data);
    }

    public Mutation buildMutation(String key, String cf, String cq, String value) {
        Mutation mutation = new Mutation(key);
        mutation.put(new Text(cf), new Text(cq), new ColumnVisibility(), new Value(value));
        return mutation;
    }

    @Override
    public Mutation buildMutation(String key, String value) {
        return this.buildMutation(key, this.cf, this.cq, value);
    }
}
