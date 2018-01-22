package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.data.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Task that fetch objects with a list of keys.
 * @author Yann Prono
 */
public class GetByKeysListTask extends ReaderTask {

    private static final Logger logger = LoggerFactory.getLogger(FullScanTask.class);

    public GetByKeysListTask(BatchScanner scanner, List<Range> keys) {
        super(scanner);
        scanner.setRanges(keys);
    }

    @Override
    public ScannerBase call() {
        return this.scanner;
    }

}
