package project.industrial.benchmark.scenarios;

import project.industrial.benchmark.core.Scenario;
import project.industrial.benchmark.tasks.mapred.InfiniteJobFetch10000Entries;
import project.industrial.benchmark.tasks.mapred.InfiniteJobFetch600Entries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FullScanConcurrentScenario extends Scenario {

    private ExecutorService executorService;
    private String[] args;

    public FullScanConcurrentScenario(String[] args) {
        super(FullScanConcurrentScenario.class);
        this.args = args;
        this.executorService = Executors.newFixedThreadPool(25);
    }

    protected void action() throws Exception {
        Collection<Callable<Integer>> tasks = new ArrayList<>();
        //tasks.add(() -> { InfiniteJobFetch600Entries.main(args); return 0; });
        //tasks.add(() -> { InfiniteJobFetch600Entries.main(args); return 0; });
        //tasks.add(() -> { InfiniteJobFetch600Entries.main(args); return 0; });
        tasks.add(() -> { InfiniteJobFetch10000Entries.main(args); return 0; });
        //tasks.add(() -> { InfiniteJobFetch10000Entries.main(args); return 0; });
        this.executorService.invokeAll(tasks);
    }

    public void finish() {
        super.finish();
        this.executorService.shutdown();
    }

    public static void main(String[] args) throws Exception {
        FullScanConcurrentScenario scenario = new FullScanConcurrentScenario(args);
        scenario.action();
        scenario.finish();
    }
}
