package project.industrial.benchmark.tasks.mapred;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.TaskCounter;
import org.apache.hadoop.util.ToolRunner;

import java.io.BufferedReader;
import java.io.IOException;

public class InfiniteJobFetch600Entries extends JobMapReduce {

    public static class Mapper600Entries extends BenchmarkMapper {

        private boolean found;

        @Override
        protected void setup(Context context) {
            this.found = false;
        }

        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            if(row.hashCode() % 3000000 == 0 && !this.found) {
                context.write(NullWritable.get(), row.getRow());
                this.found = true;
            }
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
