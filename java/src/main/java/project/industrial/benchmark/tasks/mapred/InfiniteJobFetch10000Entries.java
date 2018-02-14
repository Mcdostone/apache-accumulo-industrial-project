package project.industrial.benchmark.tasks.mapred;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class InfiniteJobFetch10000Entries extends JobMapReduce {

    public static class Mapper10000Entries extends Mapper<Key, Value, NullWritable, Text> {

        private int i;

        @Override
        protected void setup(Context context) {
            this.i = 0;
        }

        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            if(this.i % 10000 == 0) {
                context.write(NullWritable.get(), row.getRow());
            }
            i++;
        }
    }

    @Override
    public Class getMapperClass() {
        return Mapper10000Entries.class;
    }

    public static void main(String[] args) throws Exception {
        while(true)
            ToolRunner.run(new Configuration(), new InfiniteJobFetch10000Entries(), args);
    }

}
