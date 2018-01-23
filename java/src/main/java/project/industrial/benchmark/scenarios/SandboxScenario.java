package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.data.Mutation;
import project.industrial.benchmark.core.PeopleMutationBuilder;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.MultiThreadedInjector;

import java.util.Iterator;
import java.util.List;

/**
 * Used to do some tests
 * @author Yann Prono
 */
public class SandboxScenario extends Scenario {

    private Injector injector;

    public SandboxScenario(BatchWriter[] bw, String filename) {
        super(SandboxScenario.class.getSimpleName());
        this.injector = new MultiThreadedInjector(bw);
    }

    @Override
    protected void action() throws Exception {

    }


    public static void main(String[] args) throws Exception {
        int nbBatchwriters = 5;
        BatchWriter[] batchWriters = new BatchWriter[nbBatchwriters];
        for(int i = 0; i < nbBatchwriters; i++)
            batchWriters[i] = null;

        Scenario scenario = new SandboxScenario(batchWriters, args[0]);
        scenario.run();
        scenario.finish();
    }
}
