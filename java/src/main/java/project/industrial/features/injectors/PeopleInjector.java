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
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire.
 *
 * Cette classe permet d'injecter de la donnée dans accumulo.
 * Par défault, cette classe lit uniquement le fichier 'people.tsv'
 *
 * @author Yann Prono
 */
public class PeopleInjector implements Injector {

    public static final String FILENAME = "people.tsv";
    private static final Logger logger = LoggerFactory.getLogger(PeopleInjector.class);
    private RowIdStrategy rowIdStrategy;
    private BatchWriter bw;

    public PeopleInjector(BatchWriter bw) {
        this(bw, new DefaultRowIdStrategy());
    }

    public PeopleInjector(BatchWriter bw, RowIdStrategy strategy) {
        this.bw = bw;
        this.rowIdStrategy = strategy;
    }

    /**
     * Read all file and insert data
     */
    public int insert() {
        InputStream in = PeopleInjector.class.getResourceAsStream('/' + FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        int countLine = 1;
        try {
            reader.readLine();
            while((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                this.bw.addMutations(createMutations(countLine, data));
                countLine++;
            }
            this.bw.close();
        } catch (MutationsRejectedException | IOException e1) {
            e1.printStackTrace();
        }
        logger.info(countLine - 1 + " rows has been inserted");
        return countLine - 1;
    }

    /**
     * @return Mutations from the person data
     */
    private List<Mutation> createMutations(int id, String[] data) {
        List<Mutation> mutations = new ArrayList<>();
        Mutation name = new Mutation(this.rowIdStrategy.getRowId(String.valueOf(id)));
        name.put(
                "identity",
                "name",
                new ColumnVisibility(""),
                data[1]
        );
        Mutation city = new Mutation(this.rowIdStrategy.getRowId(String.valueOf(id)));
        city.put(
                "identity",
                "city",
                new ColumnVisibility(""),
                data[3]
        );
        mutations.add(name);
        mutations.add(city);
        return mutations;
    }

    public void setRowIdStrategy(PrefixRowIdStrategy rowIdStrategy) {
        this.rowIdStrategy = rowIdStrategy;
    }

    public static void main(String[] args) throws Exception {
        ClientOnRequiredTable opts = new ClientOnRequiredTable();
        BatchWriterOpts bwOpts = new BatchWriterOpts();
        opts.parseArgs(PeopleInjector.class.getName(), args, bwOpts);
        Connector connector = opts.getConnector();
        if(!connector.tableOperations().exists(opts.getTableName())) {
            logger.info("Creating table " + opts.getTableName());
            connector.tableOperations().create(opts.getTableName());
        }

        BatchWriter bw = connector.createBatchWriter(opts.getTableName(), bwOpts.getBatchWriterConfig());
        PeopleInjector injector = new PeopleInjector(bw);
        injector.insert();
    }
}
