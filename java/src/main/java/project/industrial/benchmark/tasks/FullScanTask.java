package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.ScannerBase;

/**
 * Task exécutant in job MapReduce pour un full scann
 * Callable which performs a full scan. This callable returns the ScannerBase object.
 *
 * @author Yann Prono
 */
public class FullScanTask extends ReaderTask {

    public FullScanTask(ScannerBase scanner) {
        super(scanner);
    }

    @Override
    public ScannerBase call() {
        return this.scanner;
    }
}
