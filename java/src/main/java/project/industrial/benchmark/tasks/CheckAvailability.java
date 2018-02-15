package project.industrial.benchmark.tasks;

import com.codahale.metrics.Counter;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.MetricsManager;

import java.util.Map;

public class CheckAvailability implements Runnable {

    private Counter counterAvailable;
    private Counter counterNotAvailable;
    private Scanner sc;
    private String key;
    private static final Logger logger = LoggerFactory.getLogger(CheckAvailability.class);

    public CheckAvailability(Scanner scanner) {
        this.sc = scanner;
        this.counterAvailable = MetricsManager.getMetricRegistry().counter("availability.10s.yes");
        this.counterNotAvailable = MetricsManager.getMetricRegistry().counter("availability.10s.no");
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void run() {
        logger.info("Check availability of " + key);
        GetByKeyTask task = new GetByKeyTask(this.sc, key);
        Map.Entry<Key, Value> entry =  task.call();
        if(entry.getKey().getRow().toString().equals(key))
            this.counterAvailable.inc();
        else
            this.counterNotAvailable.inc();
    }
}
