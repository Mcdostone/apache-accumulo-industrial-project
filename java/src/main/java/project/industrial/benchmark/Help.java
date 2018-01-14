package project.industrial.benchmark;

import project.industrial.benchmark.tasks.InjectorTask;

import java.io.IOException;
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
        classes.add(InjectorTask.class);
        System.out.printf("%d java classes available for the benchmark:\n", classes.size());
        for(Class c: classes)
            System.out.printf("\t%s\n", c.getName());
    }
}
