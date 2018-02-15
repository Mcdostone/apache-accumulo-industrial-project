package project.industrial.benchmark.tasks;

import com.codahale.metrics.Timer;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.KeyGeneratorStrategy;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;


/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Yann Prono
 */
public class InfiniteGetByKeyTask extends InfiniteGetTask {

    private static final double COUNT_GET = 100.0;

    public InfiniteGetByKeyTask(BatchScanner scanner, Timer timer, KeyGeneratorStrategy keyGen) {
        super(scanner, timer, keyGen);
    }

    @Override
    public Object call() {
        while(true) {
            for(int current = 0; current < COUNT_GET; current++) {
                String val = this.keyGeneratorStrategy.generateOne();
                this.bscanner.setRanges(Arrays.asList(Range.exact(val)));
                final Timer.Context context = timer.time();
                Iterator<Map.Entry<Key, Value>> iterator = this.bscanner.iterator();
                while (iterator.hasNext())
                    iterator.next();
                context.stop();
            }
            /*long duration = System.currentTimeMillis() - begin;
            System.out.println("### " + duration + " ms");
            System.out.println("### " + duration/COUNT_GET + " ms/key");
            */
        }
    }
}