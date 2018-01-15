package project.industrial.benchmark.core;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV Injector reads the first column of a CSV file and prepare mutations for
 * the accumulo writer.
 *
 * @author Yann Prono
 */
public class CSVInjector implements Injector {

    private final BatchWriter bw;
    private String csvFile;
    private List<Mutation> mutations;
    private static final Logger logger = LoggerFactory.getLogger(CSVInjector.class);
    private MutationBuilderStrategy mutationBuilder;

    public CSVInjector(BatchWriter bw, String csvFile) {
        this.csvFile = csvFile;
        this.bw = bw;
        this.mutations = new ArrayList<>();
        this.mutationBuilder = new CSVMutationBuilderStrategy();
    }

    @Override
    public void prepareMutations() {
        logger.info("Prepare mutations from the CSV file");
        BufferedReader reader;
        int countLine = 0;
        String line;
        int totalChars = 0;
        try {
            reader = new BufferedReader(new FileReader(csvFile));
            while((line = reader.readLine()) != null) {
                line = line.substring(0, line.length() - 1);
                this.mutations.add(this.mutationBuilder.buildMutation(String.valueOf(countLine), line));
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
    public void setMutationBuilderStrategy(MutationBuilderStrategy builder) {
        this.mutationBuilder = builder;
    }

    public int inject() throws MutationsRejectedException {
        this.bw.addMutations(this.mutations);
        this.bw.flush();
        this.bw.close();
        return this.mutations.size();
    }

}
