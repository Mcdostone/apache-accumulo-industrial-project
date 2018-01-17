package project.industrial.benchmark.core;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MetricsManager {

    private static final Logger logger = LoggerFactory.getLogger(MetricsManager.class);
    private final static MetricRegistry METRIC_REGISTRY = new MetricRegistry();
    private static GraphiteReporter graphiteReporter;
    private static MetricsManager metricsManager;

    private MetricsManager(String scenarioName) {
        if(scenarioName == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
            scenarioName = sdf.format(new Date());
        }
            MetricsManager.initReporters(scenarioName);
    }

    public void report() {
        if(graphiteReporter == null) {
            System.err.println("You forget to call 'MetricsManager.initGraphiteReport()' at the beginning of the class");
            System.exit(1);
        }
        graphiteReporter.report();
    }

    public static MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }

    public static void initReporters(String scenarioName) {
        initGraphiteReporter(scenarioName);
        initConsoleReporter(scenarioName);
    }

    public void close() {
        graphiteReporter.close();
    }

    private static void initConsoleReporter(String scenarioName) {
        final ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(getMetricRegistry()).build();
        consoleReporter.start(10, TimeUnit.SECONDS);
    }

    private static void initGraphiteReporter(String scenarioName) {
        if (graphiteReporter == null) {
            Properties prop = new Properties();
            try {
                prop.load(MetricRegistry.class.getResourceAsStream("/metrics.properties"));
                InetSocketAddress address = new InetSocketAddress(prop.getProperty("graphite.server"),
                        Integer.parseInt(prop.getProperty("graphite.port"))
                );
                logger.info(prop.toString());
                final Graphite graphite = new Graphite(address);
                graphiteReporter = GraphiteReporter.forRegistry(METRIC_REGISTRY)
                        .prefixedWith(prop.getProperty("graphite.prefix") + "." + scenarioName)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .filter(MetricFilter.ALL)
                        .build(graphite);
                graphiteReporter.start(
                        Integer.parseInt(prop.getProperty("graphite.polling.period")),
                        TimeUnit.valueOf(prop.getProperty("graphite.polling.timeUnit")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initInstance(String prefix) {
        logger.info("Init metricManager");
        metricsManager = new MetricsManager(prefix);
    }

    public static MetricsManager getInstance() {
        if(metricsManager == null) {
            System.err.println("You forget to call MetricsManager.initInstance(\"my_prefix\")");
            System.exit(1);
        }
        return metricsManager;
    }

    public static void main(String[] args) throws InterruptedException {
        MetricsManager.initInstance(null);
        MetricRegistry metricRegistry = MetricsManager.getMetricRegistry();
        final ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.start(10, TimeUnit.SECONDS);
        Counter requests = metricRegistry.counter("requests");
        for(int i = 0; i < 20; i++) {
            requests.inc();
            Thread.sleep(1000);
        }

        MetricsManager.getInstance().report();
    }

}
