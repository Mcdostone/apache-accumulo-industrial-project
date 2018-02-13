package project.industrial.benchmark.core;

import com.codahale.metrics.Counter;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskCounter;
import org.apache.hadoop.util.Tool;

import java.io.IOException;

public abstract class MapReduceScenario extends Configured implements Tool {

    public MapReduceScenario() {
        MetricsManager.initReporters("MR");
    }

    private Counter createCounter(Job job, String name) {
        String[] parts = job.getJobName().split("_");
        return MetricsManager.getMetricRegistry().counter(String.format("%s.%s.%s", parts[0], parts[1], name));
    }


    public void sendMetrics(Job job) throws IOException {
        Counter countInput = this.createCounter(job, "map_input_records");
        Counter countCpuTime = this.createCounter(job, "cpu_time_spent");
        Counter countRate = this.createCounter(job, "rate_input_records");
        countInput.inc(job.getCounters().findCounter(TaskCounter.MAP_INPUT_RECORDS).getValue());
        countCpuTime.inc(job.getCounters().findCounter(TaskCounter.MAP_INPUT_RECORDS).getValue());
        countRate.inc(countInput.getCount() / countCpuTime.getCount());
        MetricsManager.forceFlush();
    }

}
