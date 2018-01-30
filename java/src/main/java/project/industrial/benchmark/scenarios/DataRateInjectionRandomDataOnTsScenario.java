package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import com.codahale.metrics.Meter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.data.Mutation;
import project.industrial.benchmark.core.MetricsManager;
import project.industrial.benchmark.core.Scenario;

import java.util.Random;

public class DataRateInjectionRandomDataOnTsScenario extends DataRateInjectionRandomDataScenario {
    private String rowKey;
    public DataRateInjectionRandomDataOnTsScenario(BatchWriter bw, String rowKey) {
        super(DataRateInjectionRandomDataOnTsScenario.class.getSimpleName(), bw);
        this.rowKey = rowKey;
    }

    protected String createRandomKey(int index) {
        Random rnd = new Random();
        StringBuilder key = new StringBuilder();
        key.append(this.rowKey);
        for(int i = 0 ; i < 3; i++) {
            key.append((char) (rnd.nextInt(26) + 'a'));
        }
        key.append(index);
        return key.toString();
    }

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--rowKey", required = true)
        String rowKey = null;
    }

    public static void main(String[] args) throws Exception {
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();

        BatchWriter bw  = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        Scenario scenario = new DataRateInjectionRandomDataOnTsScenario(bw, opts.rowKey);
        scenario.run();
        scenario.finish();
    }
}
