package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

public class GetByKeyTask implements ReaderTask {

    private final String keyToSearch;
    private static final Logger logger = LoggerFactory.getLogger(GetByKeyTask.class);

    private Scanner scanner;

    public GetByKeyTask(Scanner scanner, String key) {
        this.scanner = scanner;
        this.keyToSearch = key;
        this.scanner.setRange(Range.exact(this.keyToSearch));
    }

    @Override
    public Iterator<Map.Entry<Key, Value>> call() throws Exception {
        logger.info(String.format("Looking for data with key '%s'", this.keyToSearch));
        return this.scanner.iterator();
    }
}
