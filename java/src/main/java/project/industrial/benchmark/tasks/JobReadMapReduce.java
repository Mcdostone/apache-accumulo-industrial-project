package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.cli.MapReduceClientOnRequiredTable;
import org.apache.accumulo.core.client.mapreduce.AccumuloInputFormat;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.metrics2.sink.GraphiteSink;
import org.apache.hadoop.util.Tool;

public abstract class JobReadMapReduce extends Configured implements Tool {

/*    public static class MapperRead extends Mapper<Key, Value, NullWritable, Text> {
        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            context.write(NullWritable.get(), row.getRow());
        }
    }
*/
    public abstract Class getMapperClass();

    public int run(String[] args) throws Exception {
        Job job = Job.getInstance(getConf());
        job.setJobName(this.getClass().getSimpleName() + "_" + System.currentTimeMillis());
        job.setJarByClass(this.getClass());
        MapReduceClientOnRequiredTable opts = new MapReduceClientOnRequiredTable();
        opts.parseArgs(getClass().getName(), args);

        job.setInputFormatClass(AccumuloInputFormat.class);
        opts.setAccumuloConfigs(job);
        job.setMapperClass(this.getMapperClass());
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);


        job.setNumReduceTasks(0);
        job.setOutputFormatClass(NullOutputFormat.class);

        job.waitForCompletion(true);
        Counters c = job.getCounters();
        for(String group: c.getGroupNames()) {
            System.out.println(group);
        }
        return job.isSuccessful() ? 0 : 1;
    }

    /*public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new project.industrial.benchmark.tasks.JobFetch1000Entries(), args);
    }*/
}
