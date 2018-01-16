package project.industrial.benchmark;

import project.industrial.benchmark.injectors.CSVInjector;
import project.industrial.benchmark.scenarios.*;
import project.industrial.benchmark.tasks.InjectorLoopTask;

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
        classes.add(DataRateDuringFullScanScenario.class);
        classes.add(TimeGetByKeyScenario.class);
        classes.add(TimeGetByKeysListScenario.class);
        classes.add(ConcurrentActionsScenario.class);
        classes.add(SandboxScenario.class);
        classes.add(CSVInjector.class);
        classes.add(InjectorLoopTask.class);
        System.out.printf("%d java classes available for the benchmark:\n", classes.size());
        for(Class c: classes)
            System.out.printf("\t%s\n", c.getName());
    }

}
