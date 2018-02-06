package project.industrial.benchmark;

import project.industrial.benchmark.main.CreateSplits;
import project.industrial.benchmark.main.CreateTable;
import project.industrial.benchmark.main.DeleteTable;
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
        classes.add(SandboxScenario.class);
        classes.add(CreateSplits.class);
        classes.add(CreateTable.class);
        classes.add(DeleteTable.class);
        System.out.printf("%d java classes available for the benchmark:\n", classes.size());
        for(Class c: classes)
            System.out.printf("\t%s\n", c.getName());
    }

}
