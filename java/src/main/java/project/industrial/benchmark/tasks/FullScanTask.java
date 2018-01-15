package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Task;

import java.util.Iterator;
import java.util.Map;

public class FullScanTask extends AbstractFullScanTask<Iterator<Map.Entry<Key, Value>>> {

    private static final Logger logger = LoggerFactory.getLogger(FullScanTask.class);
    protected ScannerBase scanner;

    public FullScanTask(ScannerBase scanner) {
        super(scanner);
    }

    @Override
    public Iterator<Map.Entry<Key, Value>> call() throws Exception {
        return this.scanner.iterator();
    }
}
