package project.industrial.features;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Classe permettant d'afficher plus élégamment la lecture des données
 *
 * @author Yann Prono
 */
public class Printer {

    public static final String FORMAT_ALL = "%-20s %-10s %-10s [%-13s] ----> %s\n";

    public static void printAll(Iterator<Map.Entry<Key, Value>> iterable) {
        String header = String.format(FORMAT_ALL, "Row ID", "CF", "CF", "TS", "Value");
        System.out.println(header + String.join("", Collections.nCopies(header.length(), "=")));
        print(iterable);
    }

    public static void print(Iterator<Map.Entry<Key, Value>> iterator) {
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
