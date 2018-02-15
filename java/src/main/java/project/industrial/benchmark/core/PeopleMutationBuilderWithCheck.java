package project.industrial.benchmark.core;

import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Mutation;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.tasks.CheckAvailability;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeopleMutationBuilderWithCheck implements MutationBuilder {

    private final CheckAvailability check;
    private final ScheduledExecutorService executorService;
    private MutationBuilder peopleMutationBuilder;


    public PeopleMutationBuilderWithCheck(Scanner sc, PeopleMutationBuilder b) {
        this.peopleMutationBuilder = b;
        this.check = new CheckAvailability(sc);
        this.executorService = Executors.newScheduledThreadPool(100);
    }

    @Override
    public List<Mutation> build(String data) {
        List<Mutation> mutations = this.peopleMutationBuilder.build(data);
        String key = mutations.get(0).getRow().toString();
        if(key.hashCode() % 5000 == 0) {
            this.check.setKey(key);
            this.executorService.schedule(this.check, 10, TimeUnit.SECONDS);
        }
        return mutations;
    }

    public long loopInjectFromCSV(String filename, Injector injector) {
        int countLine = 0;
        while(countLine < 20000000000.0) {
            countLine += this.injectFromCSV(filename, injector);
        }
        return countLine;
    }

    public long injectFromCSV(String filename, Injector injector) {
        BufferedReader reader;
        long countLine = 0;
        String line;
        try {
            reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                injector.inject(this.build(line.trim()));
                countLine++;
            }
        } catch (IOException | MutationsRejectedException e) {
            e.printStackTrace();
        }
        return countLine;
    }

}
