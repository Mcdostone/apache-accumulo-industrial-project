package project.industrial;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Mutation;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;

import project.industrial.GeneralDelete.Opts;

public class DeleteCol {
	
	/**
	 * Delete the row matching the family and qualifier
	 */
	public static class Opts extends ClientOnRequiredTable {
		@Parameter(names = "-row", required = true,  description = "rowId of row to delete")
		String rowId = "";
	    @Parameter(names = "-colfam", required = true, description = "column family")
	    String colfam = "";
	    @Parameter(names = "-colqual", required = true, description = "column qualifier")
	    String colqual = "";
	  }
	
	private static Logger logger = Logger.getLogger(DeleteCol.class);

	/**
	   * Delete rows in an Accumulo table.
	   */
	public static void main(String[] args) throws AccumuloException, AccumuloSecurityException, TableNotFoundException {
		Opts opts = new Opts();
		BatchWriterOpts bwOpts = new BatchWriterOpts();
	    opts.parseArgs(DeleteCol.class.getName(), args, bwOpts);

	    Connector connector = opts.getConnector();
	
	    BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
	    
	    Text rowId = new Text(opts.rowId);
	    Text colFam = new Text(opts.colfam);
	    Text colQual = new Text(opts.colqual);
	    Mutation mut = new Mutation(rowId);
	    mut.putDelete(colFam, colQual);
	    bw.addMutation(mut);
		bw.close();
		
		logger.info("Deleting rows in " + opts.getTableName() + " with rowID : " + rowId + " with colum family : " +
		colFam + " and column qualifier : " + colQual + "\n");
	}
}
