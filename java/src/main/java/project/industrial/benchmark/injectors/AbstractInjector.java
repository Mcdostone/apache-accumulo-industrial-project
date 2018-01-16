package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.MutationBuilderStrategy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractInjector implements Injector {

    private static final Logger logger = LoggerFactory.getLogger(AbstractInjector.class);
    private final BatchWriter bw;
    private final String filename;
    private final MutationBuilderStrategy mutationBuilder;
    private List<String> data;
    private List<Mutation> mutations;


    public AbstractInjector(BatchWriter bw, MutationBuilderStrategy mutationBuilder, String filename) {
        this.bw = bw;
        this.mutationBuilder = mutationBuilder;
        this.filename = filename;
        this.loadData();
    }

    private void loadData() {
        logger.info("Load file in memory");
        BufferedReader reader;
        int countLine = 0;
        String line;
        try {
            reader = new BufferedReader(new FileReader(this.filename));
            while((line = reader.readLine()) != null) {
                line = line.substring(0, line.length() - 1);
                this.data.add(line);
                countLine++; }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(countLine + " rows has been loaded");
    }

    @Override
    public int inject() throws MutationsRejectedException {
        this.bw.addMutations(this.mutations);
        return this.mutations.size();
    }

    @Override
    public void prepareMutations() {
        this.mutations = this.data.stream()
                .map(this.mutationBuilder::buildMutation)
                .collect(Collectors.toList());
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
