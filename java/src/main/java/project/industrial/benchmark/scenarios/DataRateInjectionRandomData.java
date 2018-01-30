package project.industrial.benchmark.scenarios;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.data.Mutation;
import project.industrial.benchmark.core.MetricsManager;
import project.industrial.benchmark.core.Scenario;

import java.util.UUID;

public class DataRateInjectionRandomData extends Scenario {

    private final BatchWriter bw;
    private final Meter meter;
    private int index;

    public DataRateInjectionRandomData(BatchWriter bw) {
        super(DataRateInjectionRandomData.class.getSimpleName());
        this.bw = bw;
        this.index = 0;
        this.meter = MetricsManager.getMetricRegistry().meter("rate_injection");
    }

    @Override
    protected void action() throws Exception {
        while(true) {
            this.bw.addMutation(this.createRandomMutation());
            this.meter.mark();
            this.index++;
        }
    }

    private Mutation createRandomMutation() {
        Mutation m = new Mutation(UUID.randomUUID().toString().substring(0, 6));
        m.put("cf","cq", "my_value_" + index);
        return m;
    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw  = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scenario scenario = new DataRateInjectionRandomData(bw);
        scenario.run();
        scenario.finish();
    }
}
