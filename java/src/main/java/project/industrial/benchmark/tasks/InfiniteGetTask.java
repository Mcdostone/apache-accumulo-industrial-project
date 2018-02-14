package project.industrial.benchmark.tasks;

import com.codahale.metrics.Meter;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;
import project.industrial.benchmark.core.KeyGeneratorStrategy;
import project.industrial.benchmark.core.RandomKeyGeneratorStrategy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public abstract class InfiniteGetTask implements Callable {

    protected final BatchScanner bscanner;
    protected final Meter meter;
    protected KeyGeneratorStrategy keyGeneratorStrategy;

    public InfiniteGetTask(BatchScanner bscanner, Meter m, KeyGeneratorStrategy keyGen) {
        this.bscanner = bscanner;
        this.meter = m;
        this.keyGeneratorStrategy = keyGen;
    }

/*    @Override
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
*/
    /*private List<String> generateKey() {
        List<String> list = new ArrayList();
        int max = this.rowKeys.size() < 2000 ? 5 : 20000;
        for (int j = 0; j < max; j++) {
            int i = (int) (Math.random() * (this.rowKeys.size()));
            list.add(this.rowKeys.get(i).toString());
        }
        return list;
    }*/
}
