package project.industrial.benchmark.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Scenario {

    protected String name;
    protected static Logger logger = LoggerFactory.getLogger(Scenario.class);

    public Scenario(String name) {
        this.name = name;
    }

    public void checkMaxDuration(long expected, long begin, long end) throws ScenarioNotRespectedException {
        long duration = end - begin;
        if(duration > expected) {
            String message = String.format(
                    "Problem with the '%s' scenario, expected to be finished in %d ms but took %d ms",
                    this.name,
                    expected,
                    duration
            );
            throw new ScenarioNotRespectedException(message);
        }
        else {
            logger.info(String.format("Scenario '%s' finished in %d ms",this.name, duration));
        }
    }

    public abstract void action() throws Exception;

}
