package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;

/**
 * Option pour le CLI, permettant de spécifier une fichier de ROW ID
 *
 * @author Yann Prono
 */
class KeyFileOpts extends ClientOnRequiredTable {
    @Parameter(names = "--keyFile", description = "file containing a set of keys")
    String keyFile = null;
}
