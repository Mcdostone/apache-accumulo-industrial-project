package project.industrial.benchmark.core;

import com.codahale.metrics.*;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MetricsManager {

    private final static MetricRegistry METRIC_REGISTRY = new MetricRegistry();
    private static GraphiteReporter graphiteReporter;
    private static MetricsManager metricsManager;

    private MetricsManager(String scenarioName) {
        MetricsManager.initGraphiteReporter(scenarioName);
    }

    private MetricsManager() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'.000Z'");
        MetricsManager.initGraphiteReporter(sdf.format(new Date()));
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

    public void close() {
        graphiteReporter.close();
    }

    private static void initGraphiteReporter(String scenarioName) {
        if (graphiteReporter == null) {
            Properties prop = new Properties();
            try {
                prop.load(MetricRegistry.class.getResourceAsStream("/metrics.properties"));
                InetSocketAddress address = new InetSocketAddress(prop.getProperty("graphite.server"),
                        Integer.parseInt(prop.getProperty("graphite.port"))
                );
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
        metricsManager = new MetricsManager(prefix);
    }

    public static MetricsManager getInstance() {
        if(metricsManager == null) {
            System.err.println("You forger to call MetricsManager.initInstance(\"my_prefix\")");
            System.exit(1);
        }
        return metricsManager;
    }

    public static void main(String[] args) throws InterruptedException {
        MetricsManager metricsManager = new MetricsManager();
        MetricRegistry metricRegistry = metricsManager.getMetricRegistry();
        final ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.start(10, TimeUnit.SECONDS);
        Meter requests = metricRegistry.meter("requests");
        for(int i = 0; i < 60; i++) {
            requests.mark();
            Thread.sleep(1000);
        }
        metricsManager.report();

    }

}
