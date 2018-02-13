package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.mapred.AccumuloOutputFormat;
import org.apache.accumulo.core.data.Mutation;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.List;

public class SimpleInjector implements Injector {

    private final BatchWriter bw;

    public SimpleInjector(BatchWriter bw) {
        this.bw = bw;
    }

    @Override
    public int inject(Mutation mutation) throws MutationsRejectedException {
        this.bw.addMutation(mutation);
        return 1;
    }


    @Override
    public int inject(List<Mutation> mutations) throws MutationsRejectedException {
        this.bw.addMutations(mutations);
        return mutations.size();
    }

    @Override
    public void close() throws MutationsRejectedException {
        this.bw.close();
    }

    @Override
    public void flush() throws MutationsRejectedException {
        this.bw.flush();
    }
}
