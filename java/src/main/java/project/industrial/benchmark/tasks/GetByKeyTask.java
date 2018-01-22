package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Yann Prono
 */
public class GetByKeyTask extends ReaderTask {

    private final String keyToSearch;
    private static final Logger logger = LoggerFactory.getLogger(GetByKeyTask.class);

    public GetByKeyTask(Scanner scanner, String key) {
        super(scanner);
        this.keyToSearch = key;
        scanner.setRange(Range.exact(this.keyToSearch));
    }

    @Override
    public ScannerBase call() {
        logger.info(String.format("Looking for data with key '%s'", this.keyToSearch));
        Iterator<Map.Entry<Key, Value>> iterator = this.scanner.iterator();
        while(iterator.hasNext()) {
            iterator.next();
        }
        return this.scanner;
    }
}
