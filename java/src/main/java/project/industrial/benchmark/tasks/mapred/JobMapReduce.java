package project.industrial.benchmark.tasks.mapred;

import org.apache.accumulo.core.cli.MapReduceClientOnRequiredTable;
import org.apache.accumulo.core.client.mapreduce.AccumuloInputFormat;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;

public abstract class JobMapReduce extends Configured implements Tool {


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
        return job.isSuccessful() ? 0 : 1;
    }

    public abstract Class getMapperClass();

}
