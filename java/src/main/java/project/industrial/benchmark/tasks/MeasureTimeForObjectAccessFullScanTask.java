package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This callable is useful for retrieving the duration between each object access during
 * a full scan.
 *
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
     * @return List of long, containing all durations between two object accesses
     */
    public List<Long> call() {
        List<Long> measures = new ArrayList<>();
        logger.info("Executing a full scan");
        long beginTime = System.nanoTime();
        for (Map.Entry<Key,Value> ignored : this.scanner) {
            long currentTime = System.nanoTime();
            measures.add(currentTime - beginTime);
            beginTime = currentTime;
        }
        return measures;
    }

}
