package project.industrial.benchmark.scenarios;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;

public class InjectorOpts extends ClientOnRequiredTable {

    @Parameter(names = "--csv", required = true, description = "The CSV file containing data you want to store")
    public
    String csv = null;

}
