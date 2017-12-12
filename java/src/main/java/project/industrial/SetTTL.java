package project.industrial;

import java.util.ArrayList;
import java.util.List;

import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.admin.CompactionConfig;
import org.apache.accumulo.core.iterators.user.AgeOffFilter;
import org.apache.accumulo.core.iterators.user.RegExFilter;
import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;

public class SetTTL {

	/**
	 * Optional arguments for the Scan class
	 */
	public static class Opts extends ClientOnRequiredTable {
		@Parameter(names = "-ttl", required = true, description = "time to live in milliseconds")
	    long ttl;
	  }
	
	private static Logger logger = Logger.getLogger(SetTTL.class);
	
	public static void main(String[] args) throws TableNotFoundException, AccumuloException, AccumuloSecurityException {
		
		Opts opts = new Opts();
		ScannerOpts bsOpts = new ScannerOpts();
	    opts.parseArgs(SetTTL.class.getName(), args, bsOpts);

	    Connector connector = opts.getConnector();

		//to use a filter, which is an iterator, you must create an IteratorSetting
		//specifying which iterator class you are using
		IteratorSetting iter = new IteratorSetting(15, "myFilter", AgeOffFilter.class);
		
		boolean orFields = false;
		AgeOffFilter.setTTL(iter, opts.ttl);
		AgeOffFilter.setNegate(iter, orFields);
		
		connector.tableOperations().attachIterator(opts.getTableName(), iter);
		
	}
	
	
}
