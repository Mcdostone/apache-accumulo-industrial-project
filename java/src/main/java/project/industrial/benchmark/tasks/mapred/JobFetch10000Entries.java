package project.industrial.benchmark.tasks.mapred;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.util.PeekingIterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * MapReduce récupérant 10000 objets durant un full scan.
 *
 * @author Yann Prono
 */
public class JobFetch10000Entries extends JobMapReduce {

    public static class Mapper10000Entries extends BenchmarkMapper {

        @Override
        public void map(Text row, PeekingIterator<Map.Entry<Key,Value>> data, Context context) throws IOException, InterruptedException {
            if(row.hashCode() % 2000000 == 0 && !isFinished()) {
                context.write(NullWritable.get(), row);
                int count = this.countAttributes.getOrDefault(row, 0);
                countAttributes.put(row, count + 1);
                this.found = this.countAttributes.get(row).equals(6);
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
        ToolRunner.run(new Configuration(), new JobFetch10000Entries(), args);
    }

}
