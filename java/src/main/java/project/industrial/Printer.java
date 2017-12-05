package project.industrial;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.Iterator;
import java.util.Map;

public class Printer {

    public static final String FORMAT_ALL = "%-20s %-10s %-10s [%-13s] ----> %s\n";

    public static void printAll(Iterator<Map.Entry<Key, Value>> iterable) {
        System.out.printf(FORMAT_ALL, "Row ID", "CF", "CF", "TS", "Value");
        print(iterable, FORMAT_ALL);
    }

    public static void print(Iterator<Map.Entry<Key, Value>> iterator, String format) {
        while (iterator.hasNext()) {
            Map.Entry<Key, Value> kv = iterator.next();
            System.out.printf(FORMAT_ALL,
                kv.getKey().getRow(),
                kv.getKey().getColumnFamily(),
                kv.getKey().getColumnQualifier(),
                kv.getKey().getTimestamp(),
                new String(kv.getValue().get())
            );
        }
    }
}
