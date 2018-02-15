package project.industrial.benchmark.tasks;

import com.codahale.metrics.Timer;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.KeyGeneratorStrategy;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Samia Benjida
 */
public class InfiniteGetByKeyRangeTask extends InfiniteGetTask {

    private static final Logger logger = LoggerFactory.getLogger(InfiniteGetByKeyRangeTask.class);

    public InfiniteGetByKeyRangeTask(BatchScanner scanner, Timer timer, KeyGeneratorStrategy keyGen) {
        super(scanner, timer, keyGen);
    }

    @Override
    public Object call() {
        while(true) {
            long begin = System.currentTimeMillis();
            for(int current = 0; current < 10; current++) {
                this.bscanner.setRanges(Arrays.asList(this.generateRange()));
                final Timer.Context context = timer.time();
                Iterator<Map.Entry<Key, Value>> iterator = this.bscanner.iterator();
                while(iterator.hasNext())
                    iterator.next();
                context.stop();
            }
            long duration = System.currentTimeMillis() - begin;
            System.out.println("### " + duration + " ms");
            System.out.println("### " + duration/10 + " ms/get_by_range_of_2000");
        }
    }

    private Range generateRange() {
        String[] r = this.keyGeneratorStrategy.getRange();
        return new Range(r[0], r[1]);
    }
}