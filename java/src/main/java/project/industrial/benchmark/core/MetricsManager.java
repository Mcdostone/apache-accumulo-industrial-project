package project.industrial.benchmark.core;

import com.codahale.metrics.*;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.PickledGraphite;
import org.apache.accumulo.server.metrics.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MetricsManager {

    private final static MetricRegistry METRIC_REGISTRY = new MetricRegistry();
    private static final List<ScheduledReporter> reporters = new ArrayList<>();

    public static MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }

    public static void initReporters(String prefix) {
        reporters.add(initGraphiteReporter(prefix));
    }

    public static void initReporters() {
        initReporters(null);
    }

    public static void forceFlush() {
        reporters.forEach(r -> {
            r.report();
        });
    }

    public static void close() {
        reporters.forEach(r -> {
            r.report();
            r.close();
        });
    }

    private static ScheduledReporter initGraphiteReporter(String prefix) {
        Properties prop = new Properties();
        GraphiteReporter graphiteReporter = null;
        try {
            prop.load(MetricRegistry.class.getResourceAsStream("/metrics.properties"));
            InetSocketAddress address = new InetSocketAddress(
                    prop.getProperty("graphite.server"),
                    Integer.parseInt(prop.getProperty("graphite.port"))
            );

            final PickledGraphite graphite = new PickledGraphite(address);

            graphiteReporter = GraphiteReporter.forRegistry(getMetricRegistry())
                    .prefixedWith(prefix == null ? prop.getProperty("graphite.prefix"): prefix)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build(graphite);
                graphiteReporter.start(
                        Long.parseLong(prop.getProperty("graphite.polling.period")),
                        TimeUnit.valueOf(prop.getProperty("graphite.polling.timeUnit")));
        } catch (IOException e) { e.printStackTrace(); }

        return graphiteReporter;
    }

    public static void main(String[] args) {
        MetricsManager.initReporters("local");
        Counter c = MetricsManager.getMetricRegistry().counter("test");
        Scanner sc = new Scanner(System.in);
        int v = 0;
        while(true) {
            v = sc.nextInt();
            c.inc(v);
            forceFlush();
        }
    }

}
