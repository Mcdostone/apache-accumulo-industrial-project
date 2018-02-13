package project.industrial.benchmark.tasks;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Callable task which returns the scanner when this process is finished
 *
 * @author Yann Prono
 */
public class InfiniteGetByKeyTask implements Callable {


    private final Scanner scanner;
    private final ArrayList rowKeys;
    private final Meter meter;

    public InfiniteGetByKeyTask(Scanner scanner, ArrayList rk, Meter m) {
        this.scanner = scanner;
        this.rowKeys=rk;
        this.meter = m;
    }

    @Override
    public Object call() {
        while (true) {
            String val = this.generateKey();
            this.scanner.setRange(Range.exact(val));
            Iterator<Map.Entry<Key, Value>> iterator = this.scanner.iterator();
            while (iterator.hasNext()) {
                Map.Entry e = iterator.next();
                this.meter.mark();
                if(this.meter.getCount() % 10000 == 0)
                    System.out.println(e);
            }
        }
    }

    private String generateKey() {
            int i = (int) (Math.random() * (this.rowKeys.size()));
            return this.rowKeys.get(i).toString();
    }
}