package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.ScannerBase;
import project.industrial.benchmark.core.Task;


/**
 * Basic callable for performing full scan operations
 * @param <T> Data you want to return at the end of your operation
 * @author Yann Prono
 */
public abstract class AbstractFullScanTask<T> implements Task<T> {

    protected ScannerBase scanner;

    public AbstractFullScanTask(ScannerBase scanner) {
        this.scanner = scanner;
    }
}
