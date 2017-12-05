package project.industrial;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Range;
import org.apache.log4j.Logger;

/**
 * Fetch logical row with a given rowId
 * @author Yann Prono
 */
public class GetKey {

	private static Logger logger = Logger.getLogger(GetKey.class);



	static class Opts extends ClientOnRequiredTable {
		@Parameter(names = "--rowId", required = true, description = "the rowId you want to to retrieve")
		String rowId = null;
	}

	public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
		Opts opts = new Opts();
		ScannerOpts sOpts = new ScannerOpts();
		opts.parseArgs(GetKey.class.getName(), args, sOpts);
		Connector connector = opts.getConnector();
		Scanner scanner = connector.createScanner(opts.getTableName(), opts.auths);

		// Define a range which starts to rowId and finished with rowId
		scanner.setRange(new Range(opts.rowId, opts.rowId));
		logger.info(String.format("Retrieve row with rowID=%s in '%s'\n", opts.rowId, opts.getTableName()));
		Printer.printAll(scanner.iterator());
	}
}
