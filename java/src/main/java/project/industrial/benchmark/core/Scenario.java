package project.industrial.benchmark.core;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public abstract class Scenario {

    protected String name;
    protected ScheduledExecutorService executorService;
    protected static Logger logger = LoggerFactory.getLogger(Scenario.class);

    public Scenario(String name) {
        this(name, 1);
    }

    public Scenario(String name, int corePoolSize) {
        this.name = name;
        this.executorService = new ScheduledThreadPoolExecutor(corePoolSize);
    }

    public void assertTrue(String message, boolean condition) throws Exception {
        if(!condition)
            throw new ScenarioNotRespectedException(message);
    }

    public void assertFalse(String message, boolean condition) throws Exception {
        this.assertTrue(message, !condition);
    }

    public int countResults(Iterator<Map.Entry<Key, Value>> iterator) {
        int count = 0;
        while(iterator.hasNext()) {
            count++;
            iterator.next();
        }
        return count;
    }

    public void assertEquals(String message, Object expected, Object given) throws Exception {
        if(!given.equals(expected)) {
            String err = String.format("Expected %s, given %s", expected, given);
            throw new ScenarioNotRespectedException(message + " " + err);
        }
    }

    public void cut() {
        logger.info(String.format("Scenario '%s' finished",this.name));
    }

    public abstract void action() throws Exception;

}
