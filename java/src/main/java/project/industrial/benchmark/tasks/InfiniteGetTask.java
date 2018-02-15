package project.industrial.benchmark.tasks;

import com.codahale.metrics.Timer;
import org.apache.accumulo.core.client.BatchScanner;
import project.industrial.benchmark.core.KeyGeneratorStrategy;

import java.util.concurrent.Callable;

public abstract class InfiniteGetTask implements Callable {

    protected final BatchScanner bscanner;
    protected Timer timer;
    protected KeyGeneratorStrategy keyGeneratorStrategy;

    public InfiniteGetTask(BatchScanner bscanner, Timer t, KeyGeneratorStrategy keyGen) {
        this.bscanner = bscanner;
        this.timer = t;
        this.keyGeneratorStrategy = keyGen;
    }
}
