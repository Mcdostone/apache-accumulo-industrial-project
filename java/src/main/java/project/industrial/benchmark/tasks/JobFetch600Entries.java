package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class JobFetch600Entries extends JobReadMapReduce {

    public static class Mapper600Entries extends Mapper<Key, Value, NullWritable, Text> {
        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            context.write(NullWritable.get(), row.getRow());
        }
    }

    @Override
    public Class getMapperClass() {
        return Mapper600Entries.class;
    }

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new JobFetch600Entries(), args);
    }

}
