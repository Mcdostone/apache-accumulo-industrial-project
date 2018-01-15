package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The data rate must be 1 000 000 objects per second during a full scan.
 * This rate is based on the time between two object accesses.
 *
 * @author Yann Prono
 */
public class MeasureTimeForObjectAccessFullScanTask extends AbstractFullScanTask<List<Long>> {

    private static final Logger logger = LoggerFactory.getLogger(MeasureTimeForObjectAccessFullScanTask.class);

    public MeasureTimeForObjectAccessFullScanTask(ScannerBase scanner) {
        super(scanner);
    }

    @Override
    /**
     * @return List of long, containing the duration for obtaining an object
     */
    public List<Long> call() throws Exception {
        List<Long> measures = new ArrayList<>();
        logger.info("Executing a full scan");
        long beginTime = System.nanoTime();
        for (Map.Entry<Key,Value> entry : this.scanner) {
            long currentTime = System.nanoTime();
            measures.add(currentTime - beginTime);
            beginTime = currentTime;
        }
        return measures;
    }

}
