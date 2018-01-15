package project.industrial.benchmark;

import project.industrial.benchmark.injectors.CSVInjector;
import project.industrial.benchmark.scenarios.DataAvailabilityTimeScenario;
import project.industrial.benchmark.scenarios.DataRateDuringFullScanScenario;
import project.industrial.benchmark.scenarios.DataRateInjectionScenario;
import project.industrial.benchmark.scenarios.SandboxScenario;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Displays the list of all availables java classes
 * that can be executed by accumulo.
 *
 * @author Yann Prono
 */
public class Help {

    public static void main(String[] args) {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(DataRateInjectionScenario.class);
        classes.add(DataAvailabilityTimeScenario.class);
        classes.add(DataRateDuringFullScanScenario.class);
        classes.add(SandboxScenario.class);
        classes.add(CSVInjector.class);
        System.out.printf("%d java classes available for the benchmark:\n", classes.size());
        for(Class c: classes)
            System.out.printf("\t%s\n", c.getName());
    }

}
