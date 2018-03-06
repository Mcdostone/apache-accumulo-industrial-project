package project.industrial.benchmark.scenarios;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Range;
import project.industrial.benchmark.core.MetricsManager;
import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.core.Task;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.tasks.GetByKeyTask;
import project.industrial.benchmark.tasks.InfiniteGetByKeyTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Used to do some tests
 * @author Yann Prono
 */
public class SandboxScenario {


    public static void main(String[] args) throws Exception {
        String[] a = new String[]{"1", "2", "3", "4"};
        String[] b = Arrays.stream(a).limit(3).toArray(String[]::new);
        for(String s: b)
            System.out.println(s);
    }
}