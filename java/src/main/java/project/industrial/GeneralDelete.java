package project.industrial;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchDeleter;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;

public class GeneralDelete {

	/**
	 * Example : -u root -p root -i accumulo -t my_table -ranges row_7910-row_7920,row_7940-,-row_10 --colfam colfam --colqual colqual_2
	 * 
	 * Optional arguments for the DeleteRow class : restrict the rows to delete.
	 * with no --colqual : delete all the rows matching ranges and colfam
	 * with no --colfam : delete all the rows matching ranges
	 */
	public static class Opts extends ClientOnRequiredTable {
	    @Parameter(names = "-ranges", required = true,   description = "',' delimited list of row ranges 'min-max' that will be deleted")
	    String ranges = "";
	    @Parameter(names = "--colfam", description = "column family of row that will be deleted")
	    String colfam = "";
	    @Parameter(names = "--colqual", description = "column qualifier row that will be deleted")
	    String colqual = "";
	  }
	
	private static Logger logger = Logger.getLogger(GeneralDelete.class);

	/**
	   * Delete rows in an Accumulo table.
	   * Possibility to specify several ranges of raw_ID.
	   */
	  public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
		Opts opts = new Opts();
		BatchWriterOpts bwOpts = new BatchWriterOpts();
	    opts.parseArgs(GeneralDelete.class.getName(), args, bwOpts);

	    Connector connector = opts.getConnector();

	    // 3rd argument : number of query threads
	    BatchDeleter bd = connector.createBatchDeleter(opts.getTableName(), opts.auths, 2, bwOpts.getBatchWriterConfig());
	    
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
	    
		bd.setRanges(ranges);
		
		// restrict to colqual and colfam if specified
		if (opts.colqual.equals("")) {
			if (opts.colfam.equals("")) {
			}
			else {
				bd.fetchColumnFamily(new Text(opts.colfam));
			}
		}
		else {
			bd.fetchColumn(new Text(opts.colfam), new Text(opts.colqual));
		}
		
		// delete matching rows
		bd .delete();
	    logger.info("Deleting rows in " + opts.getTableName() + "\n");
	    
	    bd.close();
	    
	  }
}
