package project.industrial.benchmark.tasks;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;
import project.industrial.benchmark.core.KeyGeneratorStrategy;
import project.industrial.benchmark.core.MetricsManager;
import project.industrial.benchmark.core.RandomKeyGeneratorStrategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Samia Benjida
 */
public class InfiniteGetByKeyListTask extends InfiniteGetTask {

    public InfiniteGetByKeyListTask(BatchScanner bscanner, Meter m, KeyGeneratorStrategy keyGen) {
        super(bscanner, m, keyGen);
    }

    @Override
    public Object call() {
        while(true) {
            List<String> values = this.keyGeneratorStrategy.generateKeys(2000);
            this.bscanner.setRanges(values.stream().map(id -> Range.exact(new Text(id))).collect(Collectors.toList()));
            Iterator<Map.Entry<Key, Value>> iterator = this.bscanner.iterator();
            while(iterator.hasNext()) {
                Map.Entry e = iterator.next();
                this.meter.mark();
                if(this.meter.getCount() % 10000 == 0)
                    System.out.println(e);
            }
        }
    }
}