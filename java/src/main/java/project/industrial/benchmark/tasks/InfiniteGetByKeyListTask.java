package project.industrial.benchmark.tasks;

import com.codahale.metrics.Timer;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;
import project.industrial.benchmark.core.KeyGeneratorStrategy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Samia Benjida
 */
public class InfiniteGetByKeyListTask extends InfiniteGetTask {

    public InfiniteGetByKeyListTask(BatchScanner bscanner, Timer timer, KeyGeneratorStrategy keyGen) {
        super(bscanner, timer, keyGen);
    }

    @Override
    public Object call() {
        while(true) {
            long begin = System.currentTimeMillis();
            for(int current = 0; current < 10; current++) {
                List<String> values = this.keyGeneratorStrategy.generateKeys(2000);
                this.bscanner.setRanges(values.stream().map(id -> Range.exact(new Text(id))).collect(Collectors.toList()));
                final Timer.Context context = timer.time();
                Iterator<Map.Entry<Key, Value>> iterator = this.bscanner.iterator();
                while (iterator.hasNext())
                    iterator.next();
                context.stop();
            }
            long duration = System.currentTimeMillis() - begin;
            System.out.println("### " + duration + " ms");
            System.out.println("### " + duration/10 + " ms/get_by_list_of_2000");
        }
    }
}