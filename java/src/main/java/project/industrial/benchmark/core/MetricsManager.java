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

/**
 * Classe permettant de gérer l'envoi des métriques
 * via la librairies metrics de DropWizards
 *
 * @author Yann Prono
 */
public class MetricsManager {

    private static boolean reportersInit = false;
    private final static MetricRegistry METRIC_REGISTRY = new MetricRegistry();
    private static final List<ScheduledReporter> reporters = new ArrayList<>();

    public static MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }

    public static void initReporters() {
        if(!reportersInit) {
            reporters.add(initGraphiteReporter());
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

    public static void forceFlush() {
        reporters.forEach(r -> r.report());
    }

    private static ScheduledReporter initGraphiteReporter() {
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
                    .prefixedWith(prop.getProperty("graphite.prefix"))
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build(graphite);
            graphiteReporter.start(
                    Long.parseLong(prop.getProperty("graphite.polling.period")),
                    TimeUnit.valueOf(prop.getProperty("graphite.polling.timeUnit")));
        } catch (IOException e) { e.printStackTrace(); }

        return graphiteReporter;
    }

}