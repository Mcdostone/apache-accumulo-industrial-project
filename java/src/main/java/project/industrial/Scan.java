package project.industrial;

import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.accumulo.core.cli.BatchScannerOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;

import com.beust.jcommander.Parameter;

public class Scan {

	
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

	    System.out.println("Scanning " + opts.getTableName());
	    
	    System.out.println("Results ->");
	    for (Entry<Key, Value> entry : scanner) {
	    System.out.println(" " + entry.getKey() + " " + entry.getValue());
	    }
	    
	  }
}
