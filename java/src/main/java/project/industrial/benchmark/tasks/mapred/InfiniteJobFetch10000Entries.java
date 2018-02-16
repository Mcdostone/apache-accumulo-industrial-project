package project.industrial.benchmark.tasks.mapred;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class InfiniteJobFetch10000Entries extends JobMapReduce {

    public static class Mapper10000Entries extends BenchmarkMapper {

        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            // we have 676 mappers, each mapper must write 15 objects
            if(row.hashCode() % 2000000 == 0 && !isFinished()) {
                context.write(NullWritable.get(), row.getRow());
            }
        }

        @Override
        protected boolean isFinished() {
            AtomicInteger check = new AtomicInteger(0);
            this.countAttributes.forEach((k, v) -> check.addAndGet(v));
            return this.countAttributes.keySet().size() == 15 && check.equals(6 * 15);
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
