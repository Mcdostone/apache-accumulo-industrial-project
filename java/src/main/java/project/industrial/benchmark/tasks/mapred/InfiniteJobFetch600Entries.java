package project.industrial.benchmark.tasks.mapred;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.util.MapCounter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.TaskCounter;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InfiniteJobFetch600Entries extends JobMapReduce {

    public static class Mapper600Entries extends Mapper<Key, Value, NullWritable, Text> {
        private int i;
        private int count;

        @Override
        protected void setup(Context context) {
            this.i = 0;
            this.count = (int) 13000000000.0 / 600;
        }

        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            if(i % count == 0)
                context.write(NullWritable.get(), row.getRow());
            i++;
        }

        protected void cleanup(Context context) throws IOException, InterruptedException {
            String message = "map input records: " + context.getCounter(TaskCounter.MAP_INPUT_RECORDS);
            message += "CPU time spent: " + context.getCounter(TaskCounter.CPU_MILLISECONDS);
            throw new IOException(message);
        }
    }

    @Override
    public Class getMapperClass() {
        return Mapper600Entries.class;
    }

    public static void main(String[] args) throws Exception {
        while(true)
            ToolRunner.run(new Configuration(), new InfiniteJobFetch600Entries(), args);
    }

}
