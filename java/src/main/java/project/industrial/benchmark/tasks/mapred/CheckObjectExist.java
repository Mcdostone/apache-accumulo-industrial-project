package project.industrial.benchmark.tasks.mapred;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.util.PeekingIterator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskCounter;

import java.io.IOException;
import java.util.Map;

/**
 * MapReduce vérifiant si un objet est accessible.
 *
 * @author Yann Prono
 */
public class CheckObjectExist extends JobMapReduce {

    public static class MapperCheckObject extends BenchmarkMapper {

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
        public void map(Text row, PeekingIterator<Map.Entry<Key,Value>> data, Context context) {
            if(data.next().getKey().toString().equals(context.getConfiguration().get("rowkey")) && !this.found)  {
                this.found = true;
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException {
            String baseMetric = "vm.MR." + CheckObjectExist.class.getSimpleName() + "." + context.getTaskAttemptID();
            double rate = context.getCounter(TaskCounter.MAP_INPUT_RECORDS).getValue() / (context.getCounter(TaskCounter.CPU_MILLISECONDS).getValue() / 1000.0);
            this.sendMetrics(baseMetric + ".rate", String.valueOf(rate));
            this.sendMetrics(baseMetric + ".cpu_time_spent", String.valueOf(TaskCounter.CPU_MILLISECONDS));
            this.sendMetrics(baseMetric + ".map_input_records", String.valueOf(TaskCounter.MAP_INPUT_RECORDS));
            this.sendMetrics(baseMetric + ".map_output_records", String.valueOf(TaskCounter.MAP_OUTPUT_RECORDS));
            this.sendMetrics(baseMetric + ".availability.10m.yes", String.valueOf(this.found ? 1 : 0));
            this.sendMetrics(baseMetric + ".availability.10m.no", String.valueOf(this.found ? 0 : 1));
        }

    }

    @Override
    public Class getMapperClass() {
        return MapperCheckObject.class;
    }

}


