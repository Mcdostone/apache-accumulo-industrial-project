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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CSV Injector reads the first column of a CSV file and prepare mutations for
 * the accumulo writer.
 *
 * @author Yann Prono
 */
public class CSVInjector implements Injector {

    private static final Logger logger = LoggerFactory.getLogger(CSVInjector.class);
    private final BatchWriter bw;
    private String csvFile;
    private List<Mutation> mutations;
    private MutationBuilderStrategy mutationBuilder;
    private List<String> data;

    public CSVInjector(BatchWriter bw, String csvFile) {
        this.csvFile = csvFile;
        this.bw = bw;
        this.mutations = new ArrayList<>();
        this.mutationBuilder = new CSVMutationBuilderStrategy();
        this.data = new ArrayList<>();
        this.loadFileInMemory();
    }

    private void loadFileInMemory() {
        logger.info("Load file in memory");
        BufferedReader reader;
        int countLine = 0;
        String line;
        int totalChars = 0;
        try {
            reader = new BufferedReader(new FileReader(csvFile));
            while((line = reader.readLine()) != null) {
            line = line.substring(0, line.length() - 1);
            this.data.add(line);
            totalChars += line.length();
            countLine++;
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(countLine + " rows has been loaded");
        logger.info("Average of chars: " + totalChars / countLine);
    }

    @Override
    public void prepareMutations() {
        logger.info("Prepare mutations");
        this.mutations = this.data.stream()
                .map(line -> this.mutationBuilder.buildMutation(line))
                .collect(Collectors.toList());
        logger.info(String.format("%d mutations has been created", this.mutations.size()));
    }

    @Override
    public void setMutationBuilderStrategy(MutationBuilderStrategy builder) {
        this.mutationBuilder = builder;
    }

    public int inject() throws MutationsRejectedException {
        this.bw.addMutations(this.mutations);
        return this.mutations.size();
    }

    public void flush() throws MutationsRejectedException {
        this.bw.flush();
    }

    public void close() throws MutationsRejectedException {
        this.bw.close();
    }

}
