package project.industrial.benchmark.core;

import java.util.concurrent.Callable;

/**
 * Une Taks est une callable qui exécute une opération dans accumulo.
 *
 * @param <T> L'objet que vous souhaitez récupérer
 * @author Yann Prono
 */
public interface Task<T> extends Callable<T> {

}
