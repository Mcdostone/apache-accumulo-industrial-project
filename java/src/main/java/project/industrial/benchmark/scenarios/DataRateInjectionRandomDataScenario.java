package project.industrial.benchmark.scenarios;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.data.Mutation;
import project.industrial.benchmark.core.MetricsManager;
import project.industrial.benchmark.core.Scenario;

import java.util.Random;

public class DataRateInjectionRandomDataScenario extends Scenario {

    private final BatchWriter bw;
    private final Meter meter;

    public DataRateInjectionRandomDataScenario(BatchWriter bw) {
        super(DataRateInjectionRandomDataScenario.class.getSimpleName());
        this.bw = bw;
        this.meter = MetricsManager.getMetricRegistry().meter("rate_injection");
    }


    @Override
    protected void action() throws Exception {
        int i = 0;
        while(i <= 30000000) {
            this.bw.addMutation(this.createRandomMutation(i));
            this.meter.mark();
            i++;
        }
    }

    private String createRandomKey(int index) {
        Random rnd = new Random();
        StringBuilder key = new StringBuilder();
        for(int i = 0 ; i < 4; i++) {
            key.append((char) (rnd.nextInt(26) + 'a'));
        }
        key.append(index);
        return key.toString();
    }

    private Mutation createRandomMutation(int index) {
        Mutation m = new Mutation(this.createRandomKey(index));
        m.put("cf","cq", "my_value_" + index);
        return m;
    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw  = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scenario scenario = new DataRateInjectionRandomDataScenario(bw);
        scenario.run();
        scenario.finish();
    }
}
