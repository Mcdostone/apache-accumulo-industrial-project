package project.industrial.benchmark.tasks;

import com.codahale.metrics.Timer;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.KeyGeneratorStrategy;

import java.util.Arrays;
import java.util.Map;

/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Samia Benjida
 */
public class InfiniteGetByKeyRangeTask extends InfiniteGetTask {

    private int nbIterations = 10;
    private Scanner scanner;

    public InfiniteGetByKeyRangeTask(Scanner scanner, Timer timer, KeyGeneratorStrategy keyGen) {
        super(timer, keyGen);
        this.scanner = scanner;
    }


    @Override
    public Object call() {
        while(true) {
            long begin = System.currentTimeMillis();
            for(int current = 0; current < nbIterations; current++) {
                this.scanner.setRange(this.generateRange());
                final Timer.Context context = timer.time();
                for (Map.Entry<Key, Value> ignored : this.scanner) { }
                context.stop();
            }
            long duration = System.currentTimeMillis() - begin;
            System.out.printf("[%d] %d ms for %d iterations\n", Thread.currentThread().getId(), duration, nbIterations);
            System.out.printf("[%d] %d ms/get_by_range_of_2000_keys\n", Thread.currentThread().getId(), duration/nbIterations);
        }
    }

    private Range generateRange() {
        String[] r = this.keyGeneratorStrategy.getRange();
        return new Range(r[0], r[1]);
    }
}