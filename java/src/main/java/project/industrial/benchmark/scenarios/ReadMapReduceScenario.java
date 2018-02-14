package project.industrial.benchmark.scenarios;

import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.mapred.ReadMapReduceTask;

public class ReadMapReduceScenario extends Scenario {

    private final String[] args;

    public ReadMapReduceScenario(String[] args) {
        super(ReadMapReduceScenario.class);
        this.args = args;
    }

    @Override
    protected void action() throws Exception {
        ReadMapReduceTask.main(this.args);
    }

    public static void main(String[] args) throws Exception {
        Scenario scenario = new ReadMapReduceScenario(args);
        scenario.run();
        scenario.finish();
    }

}
