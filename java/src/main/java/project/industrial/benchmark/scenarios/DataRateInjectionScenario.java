package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.PeopleMutationBuilder;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.injectors.MultiThreadedInjector;

import java.util.Iterator;
import java.util.List;

/**
 * This scenario checks the data rate injection in accumulo.
 * We should inject 80000 objects per second
 *
 * @author Yann Prono
 */
public class DataRateInjectionScenario extends Scenario {

    private static final Logger logger = LoggerFactory.getLogger(DataRateInjectionScenario.class);
    private Injector injector;

    public DataRateInjectionScenario(BatchWriter[] bw, String filename) {
        super(DataRateInjectionScenario.class.getSimpleName());
        this.injector = new MultiThreadedInjector(bw);
        List<Mutation> mutations = PeopleMutationBuilder.buildFromCSV(filename);
        Iterator<Mutation> iterator = mutations.iterator();
        while (iterator.hasNext())
            this.injector.addMutation(iterator.next());
    }

    @Override
    public void action() throws Exception {
        this.injector.inject();
        this.injector.close();
    }

    public static void main(String[] args) throws Exception {
        InjectorOpts opts = new InjectorOpts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        int nbBatchwriters = 10;
        BatchWriter[] batchWriters = new BatchWriter[nbBatchwriters];
        for(int i = 0; i < nbBatchwriters; i++)
            batchWriters[i] = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());

        Scenario scenario = new DataRateInjectionScenario(batchWriters, opts.csv);
        scenario.run();
        scenario.finish();
    }

}
