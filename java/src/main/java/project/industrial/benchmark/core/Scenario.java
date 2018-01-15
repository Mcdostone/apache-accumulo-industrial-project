package project.industrial.benchmark.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Scenario {

    protected String name;
    protected static Logger logger = LoggerFactory.getLogger(Scenario.class);

    public Scenario(String name) {
        this.name = name;
    }

    public void assertTrue(String message, boolean condition) throws Exception {
        if(!condition)
            throw new ScenarioNotRespectedException(message);
    }

    public void assertFalse(String message, boolean condition) throws Exception {
        this.assertTrue(message, !condition);
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
