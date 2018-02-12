package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.PeopleMutationBuilder;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.InjectorWithMetrics;
import project.industrial.benchmark.injectors.SimpleInjector;

import java.util.ArrayList;
import java.util.List;

/**
 * This scenario checks the data rate injection in accumulo.
 * We should inject 80000 objects per second
 *
 * @author Yann Prono
 */
public class OneMutationOf100BScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(OneMutationOf100BScenario.class);
    private Injector injector;

    public OneMutationOf100BScenario(BatchWriter bw) {
        super(OneMutationOf100BScenario.class.getSimpleName());
        this.injector = new SimpleInjector(bw);
    }

    private Mutation createMutation(int i) {
        Mutation m = new Mutation("myrowkeys" + i);
        m.put("", "mycolumnca","myrowvalue");
        logger.info("### Size of mutation: " + m.size());
        //System.out.println("#######\n#######\n#######\nSize of mutation: " + m.size() +"\n#######");
        return m;
    }

    @Override
    public void action() throws Exception {
        this.injector.inject(this.createMutation(1));
        List<Mutation> m = new ArrayList<>();
        for(int i = 0; i <= 1000; i++) {
            m.add(this.createMutation(i));
        }
        //this.injector.inject(this.createMutation(2));
        this.injector.close();
    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(OneMutationOf100BScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw  = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scenario scenario = new OneMutationOf100BScenario(bw);
        scenario.run();
        scenario.finish();
    }

}
