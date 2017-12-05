package project.industrial;

import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.*;
import org.apache.log4j.Logger;

public class Scan {

	private static Logger logger = Logger.getLogger(Scan.class);


	/**
	   * Scans over a specified number of entries to Accumulo using a {@link BatchScanner}. Completes scans twice to compare times for a fresh query with those for
	   * a repeated query which has cached metadata and connections already established.
	   */
	  public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
		ClientOnRequiredTable opts = new ClientOnRequiredTable();
	    ScannerOpts sOpts = new ScannerOpts();
	    opts.parseArgs(Scan.class.getName(), args, sOpts);

	    Connector connector = opts.getConnector();
	    Scanner scanner = connector.createScanner(opts.getTableName(), opts.auths);

	    logger.info("Scanning " + opts.getTableName() + "\n");

	    Printer.printAll(scanner.iterator());
	  }
}
