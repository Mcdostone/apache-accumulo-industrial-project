package project.industrial.benchmark;

import project.industrial.benchmark.scenarios.DataAvailabilityTimeScenario;
import project.industrial.benchmark.scenarios.DataRateInjectionScenario;

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

    private Properties properties;

    public static void main(String[] args) {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(DataRateInjectionScenario.class);
        classes.add(DataAvailabilityTimeScenario.class);
        System.out.printf("%d java classes available for the benchmark:\n", classes.size());
        for(Class c: classes)
            System.out.printf("\t%s\n", c.getName());
    }

    public void test() {
        this.properties = new Properties();
        try {
            InputStream in = Help.class.getResourceAsStream("/accumulo.properties");
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
