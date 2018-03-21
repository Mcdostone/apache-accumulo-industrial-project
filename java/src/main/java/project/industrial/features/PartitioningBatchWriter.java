package project.industrial.features;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import project.industrial.features.injectors.PeopleInjector;
import project.industrial.features.injectors.PrefixRowIdStrategy;

/**
 * Classe de fonctionnalité
 *
 * Cette classe permet à l'utiliser d'écrire des données dans accumulo
 * en précisant un préfix à ajouter à la row ID. Dans cette classe,
 * le format de la row ID sera le suivant: "PREFIX_ROW_ROWID"
 *
 * @author Yann Prono
 */
public class PartitioningBatchWriter {

    /**
     * Options supported for this class
     */
    static class Opts extends ClientOnRequiredTable {
        @Parameter(names = "--prefix", required = true, description = "Prefix to use for the rowID")
        String prefix = null;
        @Parameter(names = "--size", description = "Number for rows you want to insert (one column for each row")
        int size = 10000;
    }

    /**
     * Steps of this method are:
     * - Read the file people.tsv
     * - for the N first lines, insert the name of each person into accumulo with a prefix for the Row ID
     * - After insertion, read all data freshly inserted
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // Init the connection with accumulo
        Opts opts = new Opts();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(PartitioningBatchWriter.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        // Create the table if not exist
        if(!connector.tableOperations().exists(opts.getTableName()))
            connector.tableOperations().create(opts.getTableName());

        // Instanciation d'un batchWriter pour écrire des données
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        // Configuration de notre injecteur
        PeopleInjector injector = new PeopleInjector(bw);
        injector.setRowIdStrategy(new PrefixRowIdStrategy(opts.prefix));
        injector.insert();
    }

}
