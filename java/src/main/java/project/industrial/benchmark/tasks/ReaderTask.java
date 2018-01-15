package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import project.industrial.benchmark.core.Task;

import java.util.Iterator;
import java.util.Map;

public interface ReaderTask extends Task<Iterator<Map.Entry<Key, Value>>> {
}
