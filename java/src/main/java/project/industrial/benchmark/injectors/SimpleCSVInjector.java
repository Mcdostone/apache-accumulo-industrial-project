package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.data.Mutation;

import java.util.ArrayList;
import java.util.List;

public class SimpleCSVInjector extends AbstractCSVInjector {

    private int countIndex;

    public SimpleCSVInjector(BatchWriter bw, String filename) {
        super(bw, filename);
        this.countIndex = 0;
    }

    @Override
    protected List<Mutation> parseLine(String line) {
        List<Mutation> mutations =  new ArrayList<>();
        Mutation m = new Mutation(String.valueOf(this.countIndex));
        this.countIndex++;
        m.put("ColumnFamily", "ColumnQualifier",line.substring(0, line.length() -1).trim());
        mutations.add(m);
        return mutations;
    }

}
