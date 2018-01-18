package project.industrial.benchmark.core;

import com.codahale.metrics.*;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.PickledGraphite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MetricsManager {

    private static final Logger logger = LoggerFactory.getLogger(MetricsManager.class);
    private static boolean reportersInit = false;
    private final static MetricRegistry METRIC_REGISTRY = new MetricRegistry();
    private static final List<ScheduledReporter> reporters = new ArrayList<>();

    public static MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }

    public static void initReporters(Class cls) { initReporters(cls.getSimpleName()); }

    public static void initReporters(String scenarioName) {
        if(!reportersInit) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
            String name = sdf.format(new Date());
            if(scenarioName != null)
                name = scenarioName + "_" + name;
            reporters.add(initGraphiteReporter(name));
            reporters.add(initCsvReporter(name));
            reportersInit = true;
        }
        else
            System.err.println("You can call 'initReporters' only once!");
    }

    public static void close() {
        reporters.forEach(r -> {
            r.report();
            r.close();
        });
    }

    private static ScheduledReporter initCsvReporter(String scenarioName) {
        final CsvReporter consoleReporter = CsvReporter.forRegistry(getMetricRegistry())
                .build(new File("."));
        consoleReporter.start(1, TimeUnit.SECONDS);
        return consoleReporter;
    }

    private static ScheduledReporter initGraphiteReporter(String scenarioName) {
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
                    .prefixedWith(prop.getProperty("graphite.prefix") + "." + scenarioName)
                    .build(graphite);
                graphiteReporter.start(
                        Long.parseLong(prop.getProperty("graphite.polling.period")),
                        TimeUnit.valueOf(prop.getProperty("graphite.polling.timeUnit")));
            System.out.println(graphite.isConnected());
        } catch (IOException e) { e.printStackTrace(); }

        return graphiteReporter;
    }



    public static void main(String[] args) throws InterruptedException {
        initReporters("loul");
        Counter requests = getMetricRegistry().counter("requests");
        requests.inc();
        requests.inc();
        requests.inc();
        Thread.sleep(1000);
        requests.inc();
        Thread.sleep(1000);
        requests.inc();
        Thread.sleep(1000);
        requests.inc();
        Thread.sleep(1000);
        requests.inc();
        close();
    }

}
