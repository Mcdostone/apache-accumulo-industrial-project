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
 * Callable effectuant de manière infinie des GET BY LIST.
 *
 * @author Samia Benjida
 */
public class InfiniteGetByKeyListTask extends InfiniteGetTask {

    private int nbIterations = 10;
    private BatchScanner scanner;

    public InfiniteGetByKeyListTask(BatchScanner scanner, Timer timer, KeyGeneratorStrategy keyGen) {
        super(timer, keyGen);
        this.scanner = scanner;
    }

    @Override
    public Object call() {
        while(true) {
            long begin = System.currentTimeMillis();
            for(int current = 0; current < nbIterations; current++) {
                List<String> values = this.keyGeneratorStrategy.generateKeys(2000);
                this.scanner.setRanges(values.stream().map(id -> Range.exact(new Text(id))).collect(Collectors.toList()));
                final Timer.Context context = timer.time();
                for (Map.Entry<Key, Value> ignored : this.scanner) { }
                context.stop();
            }
            long duration = System.currentTimeMillis() - begin;
            System.out.printf("[%d] %d ms for %d iterations\n", Thread.currentThread().getId(), duration, nbIterations);
            System.out.printf("[%d] %d ms/get_by_list_of_2000_keys\n", Thread.currentThread().getId(), duration/nbIterations);
        }
    }
}