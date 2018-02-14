package project.industrial.benchmark.tasks;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Scanner;
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

    public InfiniteGetByKeyRangeTask(BatchScanner scanner, Meter m, KeyGeneratorStrategy keyGen) {
        super(scanner, m, keyGen);
    }

    @Override
    public Object call() {
        System.out.println("je suis vénér");
        while(true) {
            this.bscanner.setRanges(Arrays.asList(this.generateRange()));
            Iterator<Map.Entry<Key, Value>> iterator = this.bscanner.iterator();
            while(iterator.hasNext()) {
                Map.Entry e = iterator.next();
                this.meter.mark();
                if(this.meter.getCount() % 100000 == 0)
                    System.out.println(e);
            }
        }
    }

    private Range generateRange() {
        String[] r = this.keyGeneratorStrategy.getRange();
        return new Range(r[0], r[1]);
    }
}