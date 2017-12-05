package project.industrial;

import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Range;
import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;

public class Scan {


	/**
	 * Optional arguments for the Scan class
	 */
	public static class Opts extends ClientOnRequiredTable {
	    @Parameter(names = "--min", description = "minimum row that will be scanned")
	    String min = "";
	    @Parameter(names = "--max", description = "maximum row that will be scanned")
	    String max = "";
	  }
	
	private static Logger logger = Logger.getLogger(Scan.class);

	/**
	   * Scans an Accumulo table using a {@link Scanner}.
	   * Prints the raw_ID, column family, column qualifier, visibility and value.
	   * Possibility to specify a range of raw_ID with options max and min
	   */
	  public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
		Opts opts = new Opts();
	    ScannerOpts sOpts = new ScannerOpts();
	    opts.parseArgs(Scan.class.getName(), args, sOpts);

	    Connector connector = opts.getConnector();
	    Scanner scanner = connector.createScanner(opts.getTableName(), opts.auths);


	    /*
	     *  By default, the range is (-Inf, +Inf)
	     *  So there is nothing to change for a full scan
	     */
	    
	    System.out.println("Scanning " + opts.getTableName());
	    if (!opts.min.equals("")) {
	    	if (!opts.max.equals("")) {
	    		scanner.setRange(new Range(opts.min, opts.max));
	    		System.out.println("Scanning from row : " + opts.min + " to row : " + opts.max);
	    	}
	    	else {
	    		scanner.setRange(new Range(opts.min, null));
	    		System.out.println("Scanning from row : " + opts.min);
	    	}
	    }
	    else {
	    	if (!opts.max.equals("")) {
	    		scanner.setRange(new Range(null,opts.max));
	    		System.out.println("scanning to row " + opts.max);
	    	}
	    }
	    
	    // Printing all the scanned rows
	    System.out.println("Results ->");
	    
	    logger.info("Scanning " + opts.getTableName() + "\n");

	    Printer.printAll(scanner.iterator());
	  }
}
