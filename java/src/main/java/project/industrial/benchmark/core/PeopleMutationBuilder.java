package project.industrial.benchmark.core;

import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.tasks.CheckAvailability;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PeopleMutationBuilder implements MutationBuilder {

    private static final Logger logger = LoggerFactory.getLogger(PeopleMutationBuilder.class);
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static String LAST_KEY_ADDED;

    @Override
    public List<Mutation> build(String data) {
        String[] parts = data.split(",");
        String key = PeopleMutationBuilder.generateRandomKey();
        LAST_KEY_ADDED = key;
        return this.buildFromArray(key, parts);
    }

    public List<Mutation> buildFromArray(String key, String[] data) {
        List<Mutation> mutations = new ArrayList<>();
        mutations.add(this.buildMutation(key, "meta", "date", data[0]));
        mutations.add(this.buildMutation(key, "identity", "name", data[1]));
        mutations.add(this.buildMutation(key, "identity", "firstname", data[2]));
        mutations.add(this.buildMutation(key, "meta", "email", data[3]));
        mutations.add(this.buildMutation(key, "access", "url", data[4]));
        mutations.add(this.buildMutation(key, "access", "ip", data[5]));
        return mutations;
    }


    public static String getLastKeyAdded() {
        return LAST_KEY_ADDED;
    }

    public static String generateRandomKey() {
        StringBuilder key = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            Random randomGenerator = new Random();
            char ch = ALPHABET.charAt(randomGenerator.nextInt(ALPHABET.length()));
            key.append(ch);
        }
        return key.toString();
    }

    public Mutation buildMutation(String key, String cf, String cq, String value) {
        Mutation m = new Mutation(key.trim());
        m.put(cf.trim(), cq.trim(), value.trim());
        return m;
    }


    public static long loopInjectFromCSV(String filename, Injector injector) {
        int countLine = 0;
        logger.info("Start Injection");
        while(countLine < 20000000000.0) {
            logger.info("New Loop for injection, inserted lines: " + countLine);
            countLine += injectFromCSV(filename, injector);
        }
        return countLine;
    }

    public static long injectFromCSV(String filename, Injector injector) {
        PeopleMutationBuilder builder = new PeopleMutationBuilder();
        BufferedReader reader;
        long countLine = 0;
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
