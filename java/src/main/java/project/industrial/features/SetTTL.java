package project.industrial.features;

import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.iterators.user.AgeOffFilter;
import org.apache.log4j.Logger;

import com.beust.jcommander.Parameter;

/**
 * Classe de fonctionnalité (non validé)
 *
 * Cette classe si la configuration d'un TTL est possible
 * sur un objet.
 *
 * @author Pierre Maeckereel
 * @author Yann Prono
 */
public class SetTTL {

	/**
	 * Definition d'une nouvelle option pour le TTL
	 */
	public static class Opts extends ClientOnRequiredTable {
		@Parameter(names = "-ttl", required = true, description = "time to live in milliseconds")
	    long ttl;
	  }

	public static void main(String[] args) throws Exception {
		Opts opts = new Opts();
		ScannerOpts bsOpts = new ScannerOpts();
	    opts.parseArgs(SetTTL.class.getName(), args, bsOpts);
	    Connector connector = opts.getConnector();
		//to use a filter, which is an iterator, you must create an IteratorSetting
		//specifying which iterator class you are using
		IteratorSetting iter = new IteratorSetting(15, "myFilter", AgeOffFilter.class);
		boolean orFields = false;
		// On set le TTL pour ne récupérer que les données supérieur au TTL donnée
		AgeOffFilter.setTTL(iter, opts.ttl);
		AgeOffFilter.setNegate(iter, orFields);
		connector.tableOperations().attachIterator(opts.getTableName(), iter);
	}

}
