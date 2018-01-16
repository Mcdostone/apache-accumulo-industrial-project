package project.industrial.benchmark.scenarios;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * This scenario tests concurrent actions during the injection of 60 000 000 000 objects.
 * During the injection, 3 types of actions will be executed:
 * 10 objects accesses by key per second
 * 2 object accesses by key lists every 5 seconds
 * 5 full scans where 3 of them retrieve 600 objects and the two others 10 0000 objects
 * The injector must be executed in another process !
 *
 * @author Yann Prono
 */
public class ConcurrentActionsScenario extends Scenario {
    // TODO FULL SCAN WITH CONDITIONS
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentActionsScenario.class);
    private List<Callable<Object>> tasks;
    private final long PERIOD_LIST_KEYS = 5;
    private final long PERIOD_KEY = 1;

    public ConcurrentActionsScenario() {
        super("Concurrent actions", 20);
        this.tasks = new ArrayList();
    }

    @Override
    public void action() throws Exception {
        logger.info(String.format("Available processors ", Runtime.getRuntime().availableProcessors()));
        // Objects by Keys list
        for (int i = 0; i < 2; i++)
            this.executorService.scheduleAtFixedRate(null, 0, PERIOD_LIST_KEYS, TimeUnit.SECONDS);

        // Objects by Keys list
        for (int i = 0; i < 10; i++) {
            this.executorService.scheduleAtFixedRate(null, 0, PERIOD_KEY, TimeUnit.SECONDS);
        }

        // Full scan
        for (int i = 0; i < 5; i++) {
            this.executorService.submit((Runnable) null);
        }

    }

    public static void main(String[] args) throws Exception {
        Scenario scenario = new ConcurrentActionsScenario();
        scenario.action();
    }
}
