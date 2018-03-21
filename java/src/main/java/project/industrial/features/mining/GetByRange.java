package project.industrial.features.mining;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Range;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import project.industrial.features.GetByKey;
import project.industrial.features.Printer;

/**
 * Classe de fonctionnalité
 *
 * Cette classe récupère toute les données dont leur ROW ID fait partie
 * de la range donnée par l'utilisateur.
 *
 * @author Yann Prono
 */
public class GetByRange {

    private static Logger logger = Logger.getLogger(GetByKey.class);

    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--startAt", required = true, description = "the beginning of range")
        String start = null;
        @Parameter(names = "--endAt", required = true, description = "the end of range")
        String end = null;
        @Parameter(names = "--filter", description = "Filter by column name cf:cq")
        String filter = null;
    }

    public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
        GetByRange.Opts opts = new GetByRange.Opts();
        ScannerOpts sOpts = new ScannerOpts();
        opts.parseArgs(GetByKey.class.getName(), args, sOpts);
        Connector connector = opts.getConnector();
        if(!connector.tableOperations().exists(opts.getTableName()))
            logger.warn("Table " + opts.getTableName() + " doesn't exist");
        else {
            Scanner scanner = connector.createScanner(opts.getTableName(), opts.auths);
            // ON infique la range à fetch
            scanner.setRange(new Range(opts.start, opts.end));
            if(opts.filter != null)
                scanner.fetchColumn(
                        new Text(opts.filter.split(":")[0]),
                        new Text(opts.filter.split(":")[1])
                );
            Printer.printAll(scanner.iterator());
        }
    }
}
