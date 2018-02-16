package project.industrial.benchmark.core;

import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Mutation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.tasks.CheckAvailability;
import project.industrial.benchmark.tasks.GetByKeyTask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeopleMutationBuilderWithCheck implements MutationBuilder {

    private final ScheduledExecutorService executorService;
    private PeopleMutationBuilder peopleMutationBuilder;
    private static final Logger logger = LoggerFactory.getLogger(PeopleMutationBuilderWithCheck.class);
    private Scanner scanner;
    private int counter;

    public PeopleMutationBuilderWithCheck(Scanner sc, PeopleMutationBuilder b) {
        this.peopleMutationBuilder = b;
        this.scanner = sc;
        this.executorService = Executors.newScheduledThreadPool(100);
    }

    @Override
    public List<Mutation> build(String data) {
        List<Mutation> mutations = this.peopleMutationBuilder.build(data);
        String key = new String(mutations.get(0).getRow());
        if(key.hashCode() % 550555 == 0) {
            logger.info("Check data availability of " + counter + ", fetch by key in 10s");
            String parts[] = new String[]{
                    this.counter + "/08/2016",
                    this.counter + "Ardinger",
                    this.counter + "Heather",
                    this.counter + "Ardinger.Heather@rdi.eat",
                    this.counter + "QH40.org/SN",
                    this.counter + ".24.183.151"
            };
            mutations.addAll(this.peopleMutationBuilder.buildFromArray(String.valueOf(this.counter), parts));
            this.executorService.schedule(new CheckAvailability(this.scanner, Integer.toString(counter)), 10, TimeUnit.SECONDS);
            this.counter++;
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
