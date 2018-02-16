package project.industrial.benchmark.tasks.mapred;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.TaskCounter;

import java.io.IOException;

public class BenchmarkMapper extends Mapper<Key, Value, NullWritable, Text> {

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
    protected void cleanup(Context context) throws IOException, InterruptedException {
        String metricName = "vm.MR." + InfiniteJobFetch600Entries.class.getSimpleName() + "." + context.getTaskAttemptID();
        String msg = "" + context.getCounter(TaskCounter.CPU_MILLISECONDS).getValue();
        msg += " - " + context.getCounter(TaskCounter.MAP_INPUT_RECORDS).getValue();
        msg += " - " + context.getCounter(TaskCounter.MAP_OUTPUT_RECORDS).getValue();
        double rate = context.getCounter(TaskCounter.MAP_INPUT_RECORDS).getValue() / (context.getCounter(TaskCounter.CPU_MILLISECONDS).getValue() / 100.0);
        this.sendMetrics(metricName, String.valueOf(rate));
        throw new IOException(msg);
    }

}