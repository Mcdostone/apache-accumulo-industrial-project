package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.Scenario;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CheckByKeyListConcurrentScenario extends Scenario {

    private static int retrieved;
    private final BatchScanner sc;

    public CheckByKeyListConcurrentScenario(BatchScanner sc) {
        super(CheckByKeyListConcurrentScenario.class.getSimpleName());
        this.sc = sc;
    }

    private static Collection<Range> generateKeys() {
        List<Range> ranges = new ArrayList();
        for (int i = 0; i < 2000; i++) {
            Random randomGenerator = new Random();
            int key = randomGenerator.nextInt(60000000);
            ranges.add(Range.exact(Integer.toString(key)));
        }
        return ranges;
    }


    @Override
    protected void action() throws Exception {
        while(true) {
            sc.setRanges(generateKeys());
            for (Map.Entry<Key, Value> keyValueEntry : sc)
                retrieved++;
            if(retrieved % 1000 == 0) {
                System.out.printf("%d keys retrieved\n", retrieved);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        long begin = System.currentTimeMillis();

        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(DataRateInjectionScenario.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        BatchScanner sc = connector.createBatchScanner(opts.getTableName(), opts.auths, 10);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                BufferedWriter bw = new BufferedWriter(
                        new FileWriter(CheckByKeyListConcurrentScenario.class.getSimpleName() + ".results"));
                long time = System.currentTimeMillis() - begin;

                System.out.println("\n\n\n#####################################");
                System.out.println("##               END               ##");
                System.out.println("#####################################");
                System.out.printf("Time: %d ms\n", time);
                System.out.printf("Number of keys retrieved: %d\n", retrieved);

                bw.write(String.format("Time: %d ms", time));
                bw.write(String.format("Number of keys retrieved: %d", retrieved));
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        CheckByKeyListConcurrentScenario scenario = new CheckByKeyListConcurrentScenario(sc);
        scenario.action();
        scenario.finish();
    }
}
