package project.industrial.benchmark.tasks;

import project.industrial.benchmark.core.Task;

import java.util.Timer;

public class GetKeyWithDelay implements Task {

    private GetKeyTask task;
    private final int DELAY = 10 * 1000;

    public GetKeyWithDelay() {
        this.task = new GetKeyTask(null, "key");
    }

    public void run() {
        Timer timer = new Timer();
        timer.schedule(this.task, DELAY);
    }
}
