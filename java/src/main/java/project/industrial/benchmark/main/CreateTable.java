package project.industrial.benchmark.main;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOpts;
import org.apache.accumulo.core.client.Connector;
import project.industrial.benchmark.core.Scenario;

/**
 * Crée un table dans accumulo.
 *
 * @author Yann Prono
 */
public class CreateTable {

    public static class CreateTableOpts extends ClientOpts {
        @Parameter(names = {"-t", "--table"}, description = "table to use")
        private String tableName;
    }

    public static void main(String[] args) throws Exception {
        CreateTableOpts opts = new CreateTableOpts();
        opts.parseArgs(CreateTable.class.getName(), args);
        Connector connector = opts.getConnector();
        String tableName = opts.tableName;

        if(tableName == null )
            tableName = Scenario.askInput("Name of the table to create:");

        if(!connector.tableOperations().exists(tableName))
            connector.tableOperations().create(tableName);
        else
            System.out.println(String.format("The table %s already exists", tableName));
    }
}
