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


    public static class Mapper600Entries extends Mapper<Key, Value, NullWritable, Text> {
        private static final String COUNTER = "COUNTER";

        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            if(row.getRow().toString().length() == 2) {
                context.getConfiguration().setInt(COUNTER, context.getConfiguration().getInt(COUNTER, 0) + 1);
            }
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
