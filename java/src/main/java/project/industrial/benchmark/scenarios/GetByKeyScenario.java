package project.industrial.benchmark.scenarios;

import com.codahale.metrics.Timer;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.*;
import project.industrial.benchmark.tasks.InfiniteGetByKeyTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scénario le temps pour accéder à de la donnée via un GET BY KEY.
 *
 * @author Yann Prono
 */
public class GetByKeyScenario extends Scenario {

    private Scanner scanner;

    public GetByKeyScenario(Scanner scanner) {
        super(GetByKeyScenario.class);
        this.scanner = scanner;
    }

    @Override
    public void action() {
        System.out.println("Enter the key you want to retrieve: ");
        java.util.Scanner s = new java.util.Scanner(System.in);
        String key = s.nextLine().trim();
        this.scanner.setRange(Range.exact(key));

        Timer timer = MetricsManager.getMetricRegistry().timer("get_by_key.simple");
        Timer.Context c = timer.time();
        long begin = System.currentTimeMillis();
        Iterator it = this.scanner.iterator();
        while(it.hasNext()) { it.next(); }

        c.stop();
        System.out.println(System.currentTimeMillis() - begin + " ms");
    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        opts.parseArgs(GetByKeyScenario.class.getName(), args);
        Connector connector = opts.getConnector();
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        // Initialisation
        sc.setRange(Range.exact("aa"));
        for (Map.Entry<Key, Value> entry : sc) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        Scenario scenario = new GetByKeyScenario(sc);
        scenario.run();
        scenario.finish();
    }

}