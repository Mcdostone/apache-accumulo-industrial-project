package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Range;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.core.Task;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.tasks.GetByKeyTask;

import java.util.Iterator;

/**
 * Used to do some tests
 * @author Yann Prono
 */
public class SandboxScenario extends Scenario {

    public SandboxScenario(String name) {
        super(name);
    }

    @Override
    protected void action() throws Exception {

    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        Connector connector = opts.getConnector();
        Scanner sc = connector.createScanner(opts.getTableName(), opts.auths);

        // Initialisation
        sc.setRange(Range.exact("aaaaaaaaaa"));
        Iterator it = sc.iterator();
        System.out.println("Before while");
        Thread.sleep(3000);
        while(it.hasNext()) {
            System.out.println("in while");
            Thread.sleep(2000);
            it.next();
        }

    }
}