package project.industrial;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Displays the list of all availables java classes
 * that can be executed by accumulo.
 *
 * @author Yann Prono
 */
public class Help {

    private static final String[] PACKAGES = new String[]{"examples"};

    public static void main(String[] args) throws IOException {
        // Disable logger
        Logger.getRootLogger().removeAllAppenders();
        Logger.getRootLogger().addAppender(new NullAppender());
        String packageRoot = Help.class.getPackage().getName();
        List<Class<?>> classes = new ArrayList<>();
        for(String packageName: PACKAGES)
            classes.addAll(findClassesOfPackage(packageRoot + '.' + packageName));
        System.out.printf("List of java classes available:\n");
        for(Class c: classes)
            System.out.printf("\t%s\n", c.getName());
    }

    /**
     * @param prefix the package prefix to explore
     * @return The list of all java classes with the given package prefix
     */
    public static Set<Class<?>> findClassesOfPackage(String prefix) throws IOException {
        final Set<Class<?>> classes = new HashSet<>();
        new FastClasspathScanner(prefix)
                .matchSubclassesOf(Object.class, aClass -> classes.add(aClass)).scan();
        return classes;
    }
}
