package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Task;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GetByKeysListTask implements ReaderTask {

    private final BatchScanner scanner;
    private static final Logger logger = LoggerFactory.getLogger(FullScanTask.class);

    public GetByKeysListTask(BatchScanner scanner, List<Range> keys) {
        this.scanner = scanner;
    }

    @Override
    public Iterator<Map.Entry<Key, Value>> call() throws Exception {
        logger.info("Executing a get by keys list");
        return scanner.iterator();
    }

}
