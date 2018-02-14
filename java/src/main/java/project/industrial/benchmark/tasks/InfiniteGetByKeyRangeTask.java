package project.industrial.benchmark.tasks;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;
import project.industrial.benchmark.core.KeyGeneratorStrategy;

import javax.sound.midi.Soundbank;
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
public class InfiniteGetByKeyRangeTask extends InfiniteGetTask {

    public InfiniteGetByKeyRangeTask(BatchScanner bscanner, Meter m, KeyGeneratorStrategy keyGen) {
        super(bscanner, m, keyGen);
    }

    @Override
    public Object call() {
        while(true) {
            List<Range> rang = java.util.Arrays.asList(this.generateRange());
            this.bscanner.setRanges(rang);
            Iterator<Map.Entry<Key, Value>> iterator = this.bscanner.iterator();
            while(iterator.hasNext()) {
                Map.Entry e = iterator.next();
                this.meter.mark();
                //System.out.println(Thread.currentThread().getId() + " - " + meter.getCount());
                if(this.meter.getCount() % 10000 == 0)
                    System.out.println(e);
            }
        }
    }

    private Range generateRange() {
        String[] r = this.keyGeneratorStrategy.getRange();
        return new Range(r[0], r[1]);
    }
}