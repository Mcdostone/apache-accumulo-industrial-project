package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.Task;

import java.util.Map;
import java.util.TimerTask;

public class GetKeyTask extends TimerTask implements Task {

    private final String keyToSearch;
    private Scanner scanner;

    public GetKeyTask(Scanner scanner, String key) {
            this.scanner = scanner;
            this.keyToSearch = key;
    }

    @Override
    public void run() {
        scanner.setRange(Range.exact(this.keyToSearch));
        for (Map.Entry<Key,Value> entry : scanner)
            System.out.println("Got the key " + entry.getKey());
        scanner.close();
    }
}
