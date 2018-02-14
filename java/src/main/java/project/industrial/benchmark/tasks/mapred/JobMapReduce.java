package project.industrial.benchmark.tasks.mapred;

import com.codahale.metrics.Counter;
import org.apache.accumulo.core.cli.MapReduceClientOnRequiredTable;
import org.apache.accumulo.core.client.mapreduce.AccumuloInputFormat;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.TaskCounter;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import project.industrial.benchmark.core.MetricsManager;

import javax.naming.Context;
import java.io.IOException;

public abstract class JobMapReduce extends Configured implements Tool {

    private static Counter createCounter(Job job, String name) {
        String[] parts = job.getJobName().split("_");
        return MetricsManager.getMetricRegistry().counter(String.format("MR.%s.%s.%s", parts[0], parts[1], name));
    }

    public static void sendMetrics(Job job) throws IOException {
        Counter countInput = createCounter(job, "map_input_records");
        Counter countCpuTime = createCounter(job, "cpu_time_spent");
        Counter countRate = createCounter(job, "rate_input_records");
        countInput.inc(job.getCounters().findCounter(TaskCounter.MAP_INPUT_RECORDS).getValue());
        countCpuTime.inc(job.getCounters().findCounter(TaskCounter.CPU_MILLISECONDS).getValue());
        countRate.inc(countInput.getCount() / (countCpuTime.getCount()/1000));
        MetricsManager.forceFlush();
    }

    public MapReduceClientOnRequiredTable getOpts(){
        return new MapReduceClientOnRequiredTable();
    }

    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(getConf());
        job.setJobName(this.getClass().getSimpleName() + "_" + System.currentTimeMillis());
        job.setJarByClass(this.getClass());
        MapReduceClientOnRequiredTable opts = this.getOpts();
        opts.parseArgs(getClass().getName(), args);

        job.setInputFormatClass(AccumuloInputFormat.class);
        opts.setAccumuloConfigs(job);
        job.setMapperClass(this.getMapperClass());
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);


        job.setNumReduceTasks(0);
        job.setOutputFormatClass(NullOutputFormat.class);

        job.waitForCompletion(true);
        sendMetrics(job);
        return job.isSuccessful() ? 0 : 1;
    }

    public abstract Class getMapperClass();

}
