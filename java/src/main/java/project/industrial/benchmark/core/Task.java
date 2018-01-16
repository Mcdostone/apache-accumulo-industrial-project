package project.industrial.benchmark.core;

import java.util.concurrent.Callable;

/**
 * A task is a callable for execute an operation
 * in accumulo
 * @param <T> The object you want to retrieve when there are concurrency operations
 * @author Yann Prono
 */
public interface Task<T> extends Callable<T> {

}
