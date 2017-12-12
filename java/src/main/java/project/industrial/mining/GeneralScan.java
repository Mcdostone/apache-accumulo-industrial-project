package project.industrial;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Range;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;

public class GeneralScan {


	/**
	 * Optional arguments for the Scan class
	 */
	public static class Opts extends ClientOnRequiredTable {
		@Parameter(names = "--ranges", description = "',' delimited list of row ranges 'min-max' that will be deleted")
	    String ranges = "";
	    @Parameter(names = "--colfam", description = "column family of row that will be deleted")
	    String colfam = "";
	    @Parameter(names = "--colqual", description = "column qualifier row that will be deleted")
	    String colqual = "";
	  }
	
	private static Logger logger = Logger.getLogger(GeneralScan.class);

	/**
	   * Scans an Accumulo table using a {@link Scanner}.
	   * Prints the raw_ID, column family, column qualifier, visibility and value.
	   * Possibility to specify a range of raw_ID with options max and min
	   */
	  public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
		Opts opts = new Opts();
	    ScannerOpts sOpts = new ScannerOpts();
	    opts.parseArgs(GeneralScan.class.getName(), args, sOpts);

	    Connector connector = opts.getConnector();
	    BatchScanner scanner = connector.createBatchScanner(opts.getTableName(), opts.auths, 2);


	    /*
	     *  By default, the range is (-Inf, +Inf)
	     *  So there is nothing to change for a full scan
	     */
	    
	    /*
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
	 */   
	    
	 // Get the ranges
	    
	    Collection<Range> ranges = new ArrayList<Range>();
	    
	    if(opts.ranges.equals("")) {
	    	ranges.add(new Range());
	    }
	    else {
	    	String[] splitRanges = opts.ranges.split(",");
		    int i;
		    for (i=0; i < splitRanges.length; i++ ) {
		    	String[] array = splitRanges[i].split("-");
		    	Text min = null;
		    	Text max = null;
		    	if (!splitRanges[i].startsWith("-")) {
		    		min = new Text(array[0]);
		    		if (array.length > 1) {
		    			max = new Text(array[1]);
		    		}
		    	}
		    	else {
		    		if (array.length < 3) {
		    			max = new Text(array[1]);
		    		}
		    	}
		    	ranges.add(new Range(min, max));
		    }
	    }
	    
		scanner.setRanges(ranges);
		
		// restrict to colqual and colfam if specified
		if (opts.colqual.equals("")) {
			if (opts.colfam.equals("")) {
			}
			else {
				scanner.fetchColumnFamily(new Text(opts.colfam));
			}
		}
		else {
			scanner.fetchColumn(new Text(opts.colfam), new Text(opts.colqual));
		}
	    
	    // Printing all the scanned rows
	    System.out.println("Results ->");
	    
	    logger.info("Scanning " + opts.getTableName() + "\n");

	    Printer.printAll(scanner.iterator());
	  }
}
