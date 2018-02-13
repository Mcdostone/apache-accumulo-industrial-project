package project.industrial.benchmark.core;

import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.injectors.Injector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PeopleMutationBuilder implements MutationBuilder {

    private static final Logger logger = LoggerFactory.getLogger(PeopleMutationBuilder.class);
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    @Override
    public List<Mutation> build(String data) {
        List<Mutation> mutations = new ArrayList<>();
        // date, name, firstname, email, url, ip
        String[] parts = data.split(",");
        String key = PeopleMutationBuilder.generateRandomKey();
        mutations.add(this.buildMutation(key, "meta", "date", parts[0]));
        mutations.add(this.buildMutation(key, "identity", "name", parts[1]));
        mutations.add(this.buildMutation(key, "identity", "firstname", parts[2]));
        mutations.add(this.buildMutation(key, "meta", "email", parts[3]));
        mutations.add(this.buildMutation(key, "access", "url", parts[4]));
        mutations.add(this.buildMutation(key, "access", "ip", parts[5]));
        return mutations;
    }

    public static String generateRandomKey() {
        StringBuilder key = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            Random randomGenerator = new Random();
            char ch = ALPHABET.charAt(randomGenerator.nextInt(ALPHABET.length()));
            key.append(ch);
        }
        return key.toString();
    }

    private Mutation buildMutation(String key, String cf, String cq, String value) {
        Mutation m = new Mutation(key.trim());
        m.put(cf.trim(), cq.trim(), value.trim());
        return m;
    }

    public static int injectFromCSV(String filename, Injector injector) {
        PeopleMutationBuilder builder = new PeopleMutationBuilder();
        logger.info(String.format("Reading '%s'", filename));
        BufferedReader reader;
        int countLine = 0;
        String line;
        try {
            reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                injector.inject(builder.build(line.trim()));
                countLine++;
            }
        } catch (IOException | MutationsRejectedException e) {
            e.printStackTrace();
        }
        return countLine;
    }

}
