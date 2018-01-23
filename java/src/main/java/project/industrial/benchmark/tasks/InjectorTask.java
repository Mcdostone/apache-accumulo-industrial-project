package project.industrial.benchmark.tasks;

import project.industrial.benchmark.injectors.Injector;

import java.util.concurrent.Callable;

public class InjectorTask implements Callable<Integer> {

    private Injector injector;

    public InjectorTask(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Integer call() throws Exception {
        return this.injector.inject();
    }
}
