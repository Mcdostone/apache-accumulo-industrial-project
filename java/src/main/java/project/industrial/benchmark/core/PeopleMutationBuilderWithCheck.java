package project.industrial.benchmark.core;

import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Mutation;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.industrial.benchmark.injectors.Injector;
import project.industrial.benchmark.tasks.CheckAvailability;
import project.industrial.benchmark.tasks.mapred.CheckObjectExist;
import org.apache.hadoop.conf.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    private String[] args;
    private int counter;

    public PeopleMutationBuilderWithCheck(Scanner sc, PeopleMutationBuilder b, String[] args) {
        this.peopleMutationBuilder = b;
        this.scanner = sc;
        this.executorService = Executors.newScheduledThreadPool(100);
        this.args = args;
    }

    @Override
    public List<Mutation> build(String data) {
        List<Mutation> mutations = this.peopleMutationBuilder.build(data);
        System.out.println("\n\n");
        for(String s: args)
            System.out.println(s);
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
            if(key.hashCode() % 20 == 0) {
                this.executorService.schedule(() -> {
                    Configuration conf = new Configuration();
                    conf.set("rowkey", Integer.toString(counter));
                    try {
                        ToolRunner.run(conf, new CheckObjectExist(), Arrays.stream(args).limit(10).toArray(String[]::new));
                    } catch (Exception e) { e.printStackTrace(); }
                },  10, TimeUnit.MINUTES);
            }
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
