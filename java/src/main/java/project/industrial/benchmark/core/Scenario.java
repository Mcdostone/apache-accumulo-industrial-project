package project.industrial.benchmark.core;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


/**
 * A scenario defines a set of instructions (read, write...) that
 * are watched with metrics and measurements (for example time spent to write huge amount of data).
 *
 * @author Yann Prono
 */
public abstract class Scenario {

    /** A user-friendly name for the scenario */
    private String name;
    /** Useful for scheduling some callables */
    protected ScheduledExecutorService executorService;
    protected static Logger logger = LoggerFactory.getLogger(Scenario.class);


    public Scenario(String name) {
        this(name, 1);
    }

    /**
     * @param name Name of the new scenario
     * @param corePoolSize Number of threads for the scheduler (based on threads)
     */
    public Scenario(String name, int corePoolSize) {
        this.name = name;
        this.executorService = new ScheduledThreadPoolExecutor(corePoolSize);
    }

    /**
     * @param message Logs this message if false
     * @param condition Condition to check
     * @throws Exception
     */
    public void assertTrue(String message, boolean condition) throws Exception {
        if(!condition)
            throw new ScenarioNotRespectedException(message);
    }

    public void assertFalse(String message, boolean condition) throws Exception {
        this.assertTrue(message, !condition);
    }

    /**
     * @param iterator Iterator from a ScannerBase class
     * @return Number of elements in the iterator.
     */
    public int countResults(Iterator<Map.Entry<Key, Value>> iterator) {
        int count = 0;
        while(iterator.hasNext()) {
            count++;
            iterator.next();
        }
        return count;
    }

    /**
     * Print in the console all data retrieved by a scanner
     * @param iterator Iterator of the scanner.
     */
    public void showResults(Iterator<Map.Entry<Key, Value>> iterator) {
        while(iterator.hasNext())
            System.out.println("\u001B[32m" + iterator.next() + "\u001B[0m");
    }

    public void assertEquals(String message, Object expected, Object given) throws Exception {
        String err = String.format("Expected %s, given %s, ", expected, given);
        this.assertTrue(message + " " + err, expected.equals(given));
    }

    /**
     * Save results of scanner into a CSV file
     * @throws IOException
     */
    public void saveResultsInCSV(Iterator<Map.Entry<Key, Value>> it) throws IOException {
        List<String> data = new ArrayList<>();
        while(it.hasNext()) {
            data.add(this.entryToCSV(it.next()));
        }
        this.saveResultsInCSV(data);
    }

    public void saveResultsInCSV(List<String> data) throws IOException {
        String filename = Paths.get(
                System.getProperty("user.dir"),
                this.getClass().getSimpleName() + ".csv"
        ).toString();
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        data.forEach(d -> {
            try { bw.write(d + "\n"); }
            catch (IOException e) { e.printStackTrace(); }
        });
        bw.close();
    }

    protected String entryToCSV(Map.Entry<Key, Value> entry) {
        return String.format("%s, %s, %s, %s, %d, %s\n",
                entry.getKey().getRow(),
                entry.getKey().getColumnFamily(),
                entry.getKey().getColumnQualifier(),
                entry.getKey().getColumnVisibility(),
                entry.getKey().getTimestamp(),
                entry.getValue()
        );
    }

    public static String askInput(String message) {
        Scanner sc  = new Scanner(System.in);
        System.out.print("\u001B[34m> " + message + "\u001B[0m ");
        return sc.nextLine().trim();
    }

    /**
     * Close the scenario (like at the end of a shooting)
     */
    public void cut() {
        this.executorService.shutdown();
        logger.info(String.format("Scenario '%s' finished",this.name));
    }

    /**
     * like at the beginning of a shooting,
     * we shout "action" and the story takes place in front of the camera.
     * Action contains your operations (actors) and your measurements (filmmaker)
     * @throws Exception
     */
    public abstract void action() throws Exception;

}
