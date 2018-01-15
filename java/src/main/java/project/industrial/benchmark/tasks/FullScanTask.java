package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.ScannerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Callable which performs a full scan. This callable returns the ScannerBase object.
 *
 * @author Yann Prono
 */
public class FullScanTask extends AbstractFullScanTask<ScannerBase> {

    private static final Logger logger = LoggerFactory.getLogger(FullScanTask.class);

    public FullScanTask(ScannerBase scanner) {
        super(scanner);
    }

    @Override
    public ScannerBase call() {
        logger.info("executing full scan");
        return this.scanner;
    }
}
