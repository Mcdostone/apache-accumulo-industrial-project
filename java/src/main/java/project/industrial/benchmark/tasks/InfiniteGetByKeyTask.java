package project.industrial.benchmark.tasks;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.RandomKeyGeneratorStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Yann Prono
 */
public class InfiniteGetByKeyTask extends InfiniteGetTask {

    public InfiniteGetByKeyTask(BatchScanner scanner, Meter m) {
        super(scanner, m, new RandomKeyGeneratorStrategy());
    }

    @Override
    public Object call() {
        while (true) {
            String val = this.keyGeneratorStrategy.generateOne();
            this.bscanner.setRanges(Arrays.asList(Range.exact(val)));
            Iterator<Map.Entry<Key, Value>> iterator = this.bscanner.iterator();
            while (iterator.hasNext()) {
                Map.Entry e = iterator.next();
                this.meter.mark();
                if(this.meter.getCount() % 10000 == 0)
                    System.out.println(e);
            }
        }
    }

    /*private String generateKey() {
            int i = (int) (Math.random() * (this.rowKeys.size()));
            return this.rowKeys.get(i).toString();
    }*/
}