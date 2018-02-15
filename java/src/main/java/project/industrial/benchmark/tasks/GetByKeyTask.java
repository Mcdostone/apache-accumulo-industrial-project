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
import java.util.concurrent.Callable;

/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Yann Prono
 */
public class GetByKeyTask implements Callable<Map.Entry<Key, Value>> {

    private final String keyToSearch;
    private Scanner scanner;
    private static final Logger logger = LoggerFactory.getLogger(GetByKeyTask.class);

    public GetByKeyTask(Scanner scanner, String key) {
        this.scanner = scanner;
        this.keyToSearch = key;
        scanner.setRange(Range.exact(this.keyToSearch));
    }

    @Override
    public Map.Entry<Key, Value> call() {
        logger.info(String.format("Looking for data with key '%s'", this.keyToSearch));
        Iterator<Map.Entry<Key, Value>> iterator = this.scanner.iterator();
        while(iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}
