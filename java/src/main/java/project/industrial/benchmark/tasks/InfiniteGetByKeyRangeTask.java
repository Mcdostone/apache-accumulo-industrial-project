package project.industrial.benchmark.tasks;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

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
public class InfiniteGetByKeyRangeTask implements Callable {

    private final BatchScanner bscanner;
    private final List<String> rowKeys;
    private final Meter meter;

    public InfiniteGetByKeyRangeTask(BatchScanner bscanner, List<String> rk, Meter m) {
        this.bscanner = bscanner;
        this.rowKeys=rk;
        this.meter = m;
    }

    @Override
    public Object call() {
        while(true) {
            java.util.Collection<Range> rang = java.util.Arrays.asList(this.generateRange());
            this.bscanner.setRanges(rang);
            Iterator<Map.Entry<Key, Value>> iterator = this.bscanner.iterator();
            while(iterator.hasNext()) {
                Map.Entry e = iterator.next();
                System.out.println("coucou " + e );
                this.meter.mark();
                if(this.meter.getCount() % 200 == 0)
                    System.out.println(e);
            }
        }
    }

    private Range generateRange() {
        int index = (int) (Math.random() * (this.rowKeys.size()));
        // test sur table petite 5 donnees
        while(index + 4 > this.rowKeys.size()) {
            index = (int) (Math.random() * (this.rowKeys.size()));
        }
        String startKey = this.rowKeys.get(index);
        System.out.println("SATRTKEY === " + startKey);
        return new Range(startKey, this.rowKeys.get(index + 4));

        /*while(index + 2000 > this.rowKeys.size())
            index = (int) (Math.random() * (this.rowKeys.size()));
        String startKey = this.rowKeys.get(index);
        return new Range(startKey, this.rowKeys.get(index + 2000));
        */
    }
}