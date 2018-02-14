package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;

class KeyFileOpts extends ClientOnRequiredTable {
    @Parameter(names = "--keyFile", description = "file containing a set of keys")
    String keyFile = null;
}
