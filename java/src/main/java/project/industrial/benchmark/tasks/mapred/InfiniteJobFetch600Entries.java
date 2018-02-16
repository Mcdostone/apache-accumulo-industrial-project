package project.industrial.benchmark.tasks.mapred;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class InfiniteJobFetch600Entries extends JobMapReduce {

    public static class Mapper600Entries extends BenchmarkMapper {

        private boolean found;

        @Override
        protected boolean isFinished() {
            return this.found;
        }

        @Override
        protected void setup(Context context) {
            super.setup(context);
            this.found = false;
        }

        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            // we have 676 mappers, each mapper must write 1 object
            if(row.hashCode() % 3000000 == 0 && !isFinished()) {
                context.write(NullWritable.get(), row.getRow());
                int count = this.countAttributes.getOrDefault(row, 0);
                countAttributes.put(row, count + 1);
                this.found = this.countAttributes.get(row).equals(6);
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
