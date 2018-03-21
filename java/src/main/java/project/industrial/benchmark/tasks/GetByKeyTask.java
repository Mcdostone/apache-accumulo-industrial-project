package project.industrial.benchmark.tasks;

import com.codahale.metrics.Timer;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.MetricsManager;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * Callable exécutant une opération de GET BY KEY
 *
 * @author Yann Prono
 */
public class GetByKeyTask implements Callable<Map.Entry<Key, Value>> {

    private final String keyToSearch;
    private final Timer timer;
    private Scanner scanner;
    private static final Logger logger = LoggerFactory.getLogger(GetByKeyTask.class);

    public GetByKeyTask(Scanner scanner, String key) {
        this.scanner = scanner;
        this.keyToSearch = key;
        this.timer = MetricsManager.getMetricRegistry().timer("get_by_key.simple");
        scanner.setRange(Range.exact(this.keyToSearch));
    }

    @Override
    public Map.Entry<Key, Value> call() {
        logger.info(String.format("Looking for data with key '%s'", this.keyToSearch));
        Timer.Context c = this.timer.time();
        Map.Entry e = null;
        for (Map.Entry<Key, Value> ignored : this.scanner) {
            e = ignored;
        }
        c.stop();
        return e;
    }
}
