package project.industrial.benchmark.injectors;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.data.Mutation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class for injecting objects from our CSV file about people.
 * Example of a row in this file:
 * 2017-10-22, Stallman, Richard, r.stallman@l.fr, https://www.gnu.org/, 208.118.235.148
 *
 * @author Yann Prono
 */
public class PeopleCSVInjector extends AbstractCSVInjector {

    public PeopleCSVInjector(BatchWriter bw, String csvFile) {
        super(bw, csvFile);
    }

    @Override
    protected List<Mutation> parseLine(String line) {
        List<Mutation> mutations = new ArrayList<>();
        // date, name, firstname, email, url, ip
        String[] parts = line.split(",");
        mutations.add(this.buildMutation(randomString(), "identity", "date", parts[0]));
        mutations.add(this.buildMutation(randomString() + "_" + parts[1], "identity", "name", parts[1]));
        mutations.add(this.buildMutation(randomString() + "_" + parts[2], "identity", "firstname", parts[2]));
        mutations.add(this.buildMutation(randomString(), "identity", "email", parts[3]));
        mutations.add(this.buildMutation(randomString(), "access", "url", parts[4]));
        mutations.add(this.buildMutation(randomString(), "access", "ip", parts[5]));
        return mutations;
    }

    private static String randomString() {
        return UUID.randomUUID().toString();
    }

}
