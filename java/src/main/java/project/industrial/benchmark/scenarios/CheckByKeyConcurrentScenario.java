package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.Scenario;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CheckByKeyConcurrentScenario extends Scenario {

    private static int retrieved;
    private final Scanner sc;
    private int current;

    public CheckByKeyConcurrentScenario(Scanner sc) {
        super(CheckByKeyConcurrentScenario.class.getSimpleName());
        this.sc = sc;
        this.current = 0;
    }

    private Range generateKey() {
        return Range.exact(Integer.toString(current++));
    }


    @Override
    protected void action() throws Exception {
        while(true) {
            sc.setRange(generateKey());
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
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                BufferedWriter bw = new BufferedWriter(
                        new FileWriter(CheckByKeyConcurrentScenario.class.getSimpleName() + ".results"));
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


        CheckByKeyConcurrentScenario scenario = new CheckByKeyConcurrentScenario(sc);
        scenario.action();
        scenario.finish();
    }

}
