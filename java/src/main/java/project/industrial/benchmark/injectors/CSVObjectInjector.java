package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.client.BatchWriter;

public class CSVObjectInjector extends AbstractInjector {

    public CSVObjectInjector(BatchWriter bw, String filename) {
        super(bw, new CSVObjectMutationBuilderStrategy(), filename);
    }

}
