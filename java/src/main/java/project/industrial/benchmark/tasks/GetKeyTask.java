package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Task;

import java.util.Map;

public class GetKeyTask implements Task {

    private final String keyToSearch;
    private static final Logger logger = LoggerFactory.getLogger(GetKeyTask.class);

    private Scanner scanner;

    public GetKeyTask(Scanner scanner, String key) {
        this.scanner = scanner;
        this.keyToSearch = key;
    }

    @Override
    public Object call() throws Exception {
        logger.info(String.format("Looking for data with key '%s'", this.keyToSearch));
        scanner.setRange(Range.exact(this.keyToSearch));
        for (Map.Entry<Key,Value> entry : scanner)
                return entry;
        return null;
    }
}
