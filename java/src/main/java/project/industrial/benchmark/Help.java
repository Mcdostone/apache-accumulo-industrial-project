package project.industrial.benchmark;

import project.industrial.benchmark.core.MetricsManager;
import project.industrial.benchmark.main.CreateTable;
import project.industrial.benchmark.scenarios.DataAvailabilityTimeScenario;
import project.industrial.benchmark.scenarios.DataRateInjectionScenario;
import project.industrial.benchmark.scenarios.SandboxScenario;

import java.util.ArrayList;
import java.util.List;

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
        // classes.add(DataRateDuringFullScanScenario.class);
        // classes.add(TimeGetByKeyScenario.class);
        // classes.add(TimeGetByKeysListScenario.class);
        // classes.add(ConcurrentActionsScenario.class);
        classes.add(SandboxScenario.class);
        classes.add(MetricsManager.class);
        classes.add(CreateTable.class);
        // classes.add(InjectorLoopTask.class);
        System.out.printf("%d java classes available for the benchmark:\n", classes.size());
        for(Class c: classes)
            System.out.printf("\t%s\n", c.getName());
    }

}
