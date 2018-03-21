package project.industrial.benchmark.main;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOpts;
import org.apache.accumulo.core.client.Connector;
import project.industrial.benchmark.core.Scenario;

/**
 * Supprime une table d'accumulo.
 *
 * @author Yann Prono
 */
public class DeleteTable {

    public static class TableOpts extends ClientOpts {
        @Parameter(names = {"-t", "--table"}, description = "table to use")
        private String tableName;

        public String getTableName() {
            return this.tableName;
        }
    }

    public static void main(String[] args) throws Exception {
        TableOpts opts = new TableOpts();
        opts.parseArgs(DeleteTable.class.getName(), args);
        Connector connector = opts.getConnector();
        String tableName = opts.tableName;

        if(tableName == null )
            tableName = Scenario.askInput("Name of the table to delete:");
        if(connector.tableOperations().exists(tableName))
            connector.tableOperations().delete(tableName);
        else
            System.out.println(String.format("The table %s doesn't exist", tableName));
    }
}
