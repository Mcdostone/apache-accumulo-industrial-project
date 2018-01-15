package project.industrial.benchmark.core;

/**
 * Throw this class if the scenario doesn't respect a requirement.
 *
 * @author Yann Prono
 */
public class ScenarioNotRespectedException extends Exception {

    public ScenarioNotRespectedException(String message) {
        super(message);
    }

    public ScenarioNotRespectedException() {
        this("The Requirement declared for the scenario is not respected");
    }
}
