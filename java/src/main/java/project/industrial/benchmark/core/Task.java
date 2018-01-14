package project.industrial.benchmark.core;

import org.apache.accumulo.core.client.MutationsRejectedException;

public interface Task {

    public void execute() throws MutationsRejectedException, Exception;
}
