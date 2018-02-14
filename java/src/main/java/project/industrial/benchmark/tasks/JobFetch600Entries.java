package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JobFetch600Entries extends JobReadMapReduce {
private static int i=0;

    public static class Mapper600Entries extends Mapper<Key, Value, NullWritable, Text> {
        private static final String COUNTER = "COUNTER";

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            context.getConfiguration().setInt(COUNTER, 0);
        }

        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            if ( i % 600 == 0 ) {
                context.write(NullWritable.get(), row.getRow());
            }
            i++;
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
