package project.industrial.benchmark.tasks;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;
import project.industrial.benchmark.core.MetricsManager;

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
public class InfiniteGetByKeyListTask implements Callable {

    private final BatchScanner bscanner;
    private final List<String> rowKeys;
    private final Meter meter;

    public InfiniteGetByKeyListTask(BatchScanner bscanner, List<String> rk, Meter m) {
        this.bscanner = bscanner;
        this.rowKeys=rk;
        this.meter = m;
    }

    @Override
    public Object call() {
        while(true) {
            List<String> values = this.generateKey();
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

    private List<String> generateKey() {
        List<String> list = new ArrayList();
        int max = this.rowKeys.size() < 2000 ? 5 : 20000;
        for (int j = 0; j < max; j++) {
            int i = (int) (Math.random() * (this.rowKeys.size()));
            list.add(this.rowKeys.get(i).toString());
        }
        return list;
    }
}