package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.client.BatchWriter;

/**
 * CSV Injector reads the first column of a CSV file to create a list of mutations for
 * the accumulo writer.
 *
 * @author Yann Prono
 */
public class CSVValueInjector extends AbstractInjector {


    /**
     * @param bw The BatchWriter that inject objects
     * @param csvFile Filename of the CSV file
     */
    public CSVValueInjector(BatchWriter bw, String csvFile) {
        super(bw, new CSVValueMutationBuilderStrategy(), csvFile);
    }
}
