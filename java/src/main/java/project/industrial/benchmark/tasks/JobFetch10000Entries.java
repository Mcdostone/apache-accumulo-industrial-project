package project.industrial.benchmark.tasks;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;
import org.w3c.dom.ranges.Range;

import java.io.IOException;
import java.util.List;

public class JobFetch10000Entries extends JobReadMapReduce {

    public static class Mapper10000Entries extends Mapper<Key, Value, NullWritable, Text> {
        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            if(row.getRow().toString().length() == 3)
                context.write(NullWritable.get(), row.getRow());
        }
    }

    @Override
    public Class getMapperClass() {
        return Mapper10000Entries.class;
    }

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new JobFetch600Entries(), args);
    }

}
