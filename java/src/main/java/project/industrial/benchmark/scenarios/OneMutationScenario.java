package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.MapReduceClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.SimpleInjector;

/**
 * This scenario checks the data rate injection in accumulo.
 * We should inject 80000 objects per second
 *
 * @author Yann Prono
 */
public class OneMutationScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(OneMutationScenario.class);
    private Injector injector;
    private Opts opts;

    public OneMutationScenario(BatchWriter bw) {
        this(bw, null);
    }

    public OneMutationScenario(BatchWriter bw, Opts opts) {
        super(OneMutationScenario.class.getSimpleName());
        this.injector = new SimpleInjector(bw);
        this.opts = opts;
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
        Mutation m;
        if(this.opts != null) {
            m = new Mutation(this.opts.key);
            m.put(this.opts.cf, this.opts.cq, this.opts.value);
        }
        else {
            m = new Mutation("aaaaaaaaab");
            String v = "";
            for(int i = 0; i < 100; i++) {
                v += "t";
            }
            m.put("mycolumnfa", "mycolumnca", v);
        }

        this.injector.inject(m);
        this.injector.close();
    }

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--key")
        String key;
        @Parameter(names = "--cf")
        String cf;
        @Parameter(names = "--cq")
        String cq;
        @Parameter(names = "--value")
        String value;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(OneMutationScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw  = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scenario scenario = new OneMutationScenario(bw);
        scenario.run();
        scenario.finish();
    }

}
