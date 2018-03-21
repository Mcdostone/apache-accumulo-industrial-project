package project.industrial.features.injectors;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Classe de fonctionnalité.
 *
 * Cette classe vérifie s'il est possible d'ajouter massivement
 * une colonne.
 *
 * @author Yann Prono
 */
public class AddColumn implements Injector {

    private static final String FILENAME = "people.tsv";
    private static final Logger logger = LoggerFactory.getLogger(PeopleInjector.class);
    private BatchWriter bw;

    public AddColumn(BatchWriter bw) {
        this.bw = bw;
    }

    public int insert() {
        InputStream in = PeopleInjector.class.getResourceAsStream('/' + FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        int countLine = 1;
        try {
            reader.readLine();
            while((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                this.bw.addMutation(createMutation(countLine, data));
                countLine++;
            }
            this.bw.close();
        } catch (MutationsRejectedException | IOException e1) {
            e1.printStackTrace();
        }
        logger.info(countLine - 1 + " columns has been inserted");
        return countLine - 1;
    }

    private Mutation createMutation(int id, String[] data) {
        Mutation sexMutation = new Mutation(String.valueOf(id));
        sexMutation.put(
                "identity",
                "gender",
                new ColumnVisibility(""),
                data[12]
        );
        return sexMutation;
    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(AddColumn.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        if(!connector.tableOperations().exists(opts.getTableName())) {
            logger.info("Creating table " + opts.getTableName());
            connector.tableOperations().create(opts.getTableName());
        }
        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        AddColumn injector = new AddColumn(bw);
        injector.insert();
    }

}
