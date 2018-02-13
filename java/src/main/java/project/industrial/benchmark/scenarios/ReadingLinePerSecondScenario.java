package project.industrial.benchmark.scenarios;

import project.industrial.benchmark.core.PeopleMutationBuilder;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.FakeInjector;

public class ReadingLinePerSecondScenario extends Scenario {

    private final String filename;

    public ReadingLinePerSecondScenario(String filename) {
        super(ReadingLinePerSecondScenario.class.getSimpleName());
        this.filename = filename;
    }

    @Override
    protected void action() throws Exception {
        long begin = System.currentTimeMillis();
        PeopleMutationBuilder.injectFromCSV(filename, new FakeInjector());
        long end = System.currentTimeMillis();
        System.out.println(end - begin);
    }

    public static void main(String[] args) throws Exception {
        Scenario s = new ReadingLinePerSecondScenario(args[0]);
        s.run();
        s.finish();
    }

}
