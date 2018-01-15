package project.industrial.features.mining;

import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.iterators.user.RegExFilter;
import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;

import project.industrial.features.Printer;


public class GetByRegex {

	/**
	 * Optional arguments for the Scan class
	 */
	public static class Opts extends ClientOnRequiredTable {
		@Parameter(names = "--row", description = "regex to match rowId")
	    String row = null;
	    @Parameter(names = "--colfam", description = "regex to match column family")
	    String colfam = null;
	    @Parameter(names = "--colqual", description = "regex to match column qualifier")
	    String colqual = null;
	    @Parameter(names = "--value", description = "regex to match value")
	    String value = null;
	  }
	
	private static Logger logger = Logger.getLogger(GetByRegex.class);
	
	public static void main(String[] args) throws TableNotFoundException, AccumuloException, AccumuloSecurityException {
		
		Opts opts = new Opts();
		ScannerOpts bsOpts = new ScannerOpts();
	    opts.parseArgs(GetByRegex.class.getName(), args, bsOpts);

	    Connector connector = opts.getConnector();

		//initialize a scanner
		Scanner scan = connector.createScanner(opts.getTableName(), opts.auths);

		//to use a filter, which is an iterator, you must create an IteratorSetting
		//specifying which iterator class you are using
		IteratorSetting iter = new IteratorSetting(15, "myFilter", RegExFilter.class);
		//next set the regular expressions to match
		String rowRegex = opts.row;
		String colfRegex = opts.colfam;
		String colqRegex = opts.colqual;
		String valueRegex = opts.value;
		boolean orFields = false;
		RegExFilter.setRegexs(iter, rowRegex, colfRegex, colqRegex, valueRegex, orFields);
		//now add the iterator to the scanner, and you're all set
		scan.addScanIterator(iter);
		
	    logger.info("Scanning " + opts.getTableName() + "\n");
		
		Printer.printAll(scan.iterator());
	}
	
	
}
