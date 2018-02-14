package project.industrial.benchmark.tasks.mapred;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.MapReduceClientOnRequiredTable;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.util.format.DefaultFormatter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class ReadMapReduceTask extends JobMapReduce {

    static class Opts extends MapReduceClientOnRequiredTable {
        @Parameter(names = "--output", description = "output directory", required = true)
        String output;
        @Parameter(names = "--columns", description = "columns to extract, in cf:cq{,cf:cq,...} form")
        String columns = "";
    }

    /**
     * The Mapper class that given a row number, will generate the appropriate output line.
     */
    public static class TTFMapper extends Mapper<Key, Value, NullWritable, Text> {
        @Override
        public void map(Key row, Value data, Context context) throws IOException, InterruptedException {
            Map.Entry<Key, Value> entry = new AbstractMap.SimpleImmutableEntry<>(row, data);
            context.write(NullWritable.get(), new Text(DefaultFormatter.formatEntry(entry, false)));
            context.setStatus("Outputed Value");
        }
    }

    @Override
    public Class getMapperClass() {
        return TTFMapper.class;
    }

    @Override
    public MapReduceClientOnRequiredTable getOpts() {
        return new Opts();
    }

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new ReadMapReduceTask(), args);
    }
}
