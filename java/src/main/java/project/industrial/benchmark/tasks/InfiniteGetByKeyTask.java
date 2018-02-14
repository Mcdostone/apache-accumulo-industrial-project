package project.industrial.benchmark.tasks;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.core.KeyGeneratorStrategy;
import project.industrial.benchmark.core.MetricsManager;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Yann Prono
 */
public class InfiniteGetByKeyTask extends InfiniteGetTask {

    private static final Logger logger = LoggerFactory.getLogger(InfiniteGetByKeyTask.class);

    public InfiniteGetByKeyTask(BatchScanner scanner, Meter m, KeyGeneratorStrategy keyGen) {
        super(scanner, m, keyGen);
    }

    @Override
    public Object call() {
        while(true) {
            String val = this.keyGeneratorStrategy.generateOne();
            logger.info("Looking for " + val);
            this.bscanner.setRanges(Arrays.asList(Range.exact(val)));
            Iterator<Map.Entry<Key, Value>> iterator = this.bscanner.iterator();
            logger.info("Iterate on results");
            while (iterator.hasNext()) {
                Map.Entry e = iterator.next();
                this.meter.mark();
                if(this.meter.getCount() % 10000 == 0) {
                    System.out.println(e);
                }

            }
        }
    }
}