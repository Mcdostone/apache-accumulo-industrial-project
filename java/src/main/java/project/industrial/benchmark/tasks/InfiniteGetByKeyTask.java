package project.industrial.benchmark.tasks;

import com.codahale.metrics.Timer;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.KeyGeneratorStrategy;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Yann Prono
 */
public class InfiniteGetByKeyTask extends InfiniteGetTask {

    private final Scanner scanner;
    private int nbIterations = 200;

    public InfiniteGetByKeyTask(Scanner scanner, Timer timer, KeyGeneratorStrategy keyGen) {
        super(timer, keyGen);
        this.scanner = scanner;
    }

    @Override
    public Object call() {
        while(true) {
            long begin = System.currentTimeMillis();
            for(int current = 0; current < nbIterations; current++) {
                String val = this.keyGeneratorStrategy.generateOne();
                this.scanner.setRange(Range.exact(val));
                final Timer.Context context = timer.time();
                for (Map.Entry<Key, Value> ignored : this.scanner) { }
                context.stop();
            }
            long duration = System.currentTimeMillis() - begin;
            System.out.printf("[%d] %d ms for %d iterations\n", Thread.currentThread().getId(), duration, nbIterations);
            System.out.printf("[%d] %d ms/get_by_key\n", Thread.currentThread().getId(), duration/nbIterations);
        }
    }

}