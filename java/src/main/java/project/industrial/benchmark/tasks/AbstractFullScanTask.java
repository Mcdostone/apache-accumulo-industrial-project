package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.ScannerBase;
import project.industrial.benchmark.core.Task;

public abstract class AbstractFullScanTask<T> implements Task<T> {

    protected ScannerBase scanner;

    public AbstractFullScanTask(ScannerBase scanner) {
        this.scanner = scanner;
    }
}
