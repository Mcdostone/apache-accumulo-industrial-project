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
    protected volatile Meter meter;
    protected KeyGeneratorStrategy keyGeneratorStrategy;

    public InfiniteGetTask(BatchScanner bscanner, Meter m, KeyGeneratorStrategy keyGen) {
        this.bscanner = bscanner;
        this.meter = m;
        this.keyGeneratorStrategy = keyGen;
    }
}
