package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;
import project.industrial.benchmark.core.MutationBuilderStrategy;

/**
 * MutationBuilderStrategy for a CSV file;
 * F
 */
public class CSVObjectMutationBuilderStrategy implements MutationBuilderStrategy {

    @Override
    public Mutation buildMutation(String data) {
        String[] dataSplit = data.split(", ");
        return this.buildMutation(dataSplit[0].trim(), dataSplit[1].trim(), dataSplit[2].trim(), dataSplit[3].trim());
    }

    public Mutation buildMutation(String key, String cf, String cq, String value) {
        Mutation mutation = new Mutation(key);
        mutation.put(new Text(cf), new Text(cq), new ColumnVisibility(), new Value(value));
        return mutation;
    }

}
