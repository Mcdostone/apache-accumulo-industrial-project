package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Task;

import java.util.Map;

public class FullScanTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(FullScanTask.class);
    protected ScannerBase scanner;

    public FullScanTask(ScannerBase scanner) {
        this.scanner = scanner;
    }

    @Override
    public Object call() throws Exception {
        int count = 0;
        logger.info("Executing a full scan");
        for (Map.Entry<Key,Value> entry : this.scanner)
            count++;
        return count;
    }

}
