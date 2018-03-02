package project.industrial.benchmark.tasks.mapred;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.util.PeekingIterator;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.TaskCounter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class BenchmarkMapper extends Mapper<Text, PeekingIterator<Map.Entry<Key,Value>>, NullWritable, Text> {

    protected HashMap<Text, Integer> countAttributes;
    protected boolean found;

    protected abstract boolean isFinished();

    @Override
    protected void setup(Context context) {
        this.countAttributes = new HashMap<>();
    }

    private void sendMetrics(String metricName, String value) throws IOException {
        long now = System.currentTimeMillis() / 1000;
        String[] cmd = {
                "/bin/bash",
                "-c",
                String.format("echo \"%s %s %d\" | nc -q0 37.59.123.111 2003", metricName, value, now)
        };
        Runtime.getRuntime().exec(cmd);
    }

    @Override
    protected void cleanup(Context context) throws IOException {
        String baseMetric = "vm.MR." + JobFetch600Entries.class.getSimpleName() + "." + context.getTaskAttemptID();
        double rate = context.getCounter(TaskCounter.MAP_INPUT_RECORDS).getValue() / (context.getCounter(TaskCounter.CPU_MILLISECONDS).getValue() / 1000.0);
        this.sendMetrics(baseMetric + ".rate", String.valueOf(rate));
        this.sendMetrics(baseMetric + ".cpu_time_spent", String.valueOf(TaskCounter.CPU_MILLISECONDS));
        this.sendMetrics(baseMetric + ".map_input_records", String.valueOf(TaskCounter.MAP_INPUT_RECORDS));
        this.sendMetrics(baseMetric + ".map_output_records", String.valueOf(TaskCounter.MAP_OUTPUT_RECORDS));
    }

}