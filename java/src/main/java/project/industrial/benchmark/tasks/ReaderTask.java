package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.ScannerBase;
import project.industrial.benchmark.core.Task;

/**
 * Basic class for task which reads data in accumulo cluster
 *
 * @author Yann Prono
 */
public abstract class ReaderTask implements Task<ScannerBase> {

    protected final ScannerBase scanner;

    public ReaderTask(ScannerBase scanner) {
        this.scanner = scanner;
    }

    public ScannerBase getScanner() {
        return this.scanner;
    }
}
