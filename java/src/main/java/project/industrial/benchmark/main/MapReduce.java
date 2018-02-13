package project.industrial.benchmark.main;

import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashSet;
import java.util.Map;

import org.apache.accumulo.core.cli.MapReduceClientOnRequiredTable;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.mapreduce.AccumuloInputFormat;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.util.Pair;
import org.apache.accumulo.core.util.format.DefaultFormatter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.beust.jcommander.Parameter;

public class MapReduce extends Configured implements Tool {

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
            System.out.println(row);
            Map.Entry<Key, Value> entry = new SimpleImmutableEntry<>(row, data);
            context.write(NullWritable.get(), new Text(DefaultFormatter.formatEntry(entry, false)));
            context.setStatus("Outputed Value");
        }
    }

    public int run(String[] args) throws IOException, InterruptedException, ClassNotFoundException, AccumuloSecurityException {
        Job job = Job.getInstance(getConf());
        job.setJobName(this.getClass().getSimpleName() + "_" + System.currentTimeMillis());
        job.setJarByClass(this.getClass());
        Opts opts = new Opts();
        opts.parseArgs(getClass().getName(), args);

        job.setInputFormatClass(AccumuloInputFormat.class);
        opts.setAccumuloConfigs(job);

        /**  cQ cF  **/
        HashSet<Pair<Text,Text>> columnsToFetch = new HashSet<>();
        for (String col : opts.columns.split(",")) {
            System.out.println("col: " + col);
            int idx = col.indexOf(":");
            Text cf = new Text(idx < 0 ? col : col.substring(0, idx));
            Text cq = idx < 0 ? null : new Text(col.substring(idx + 1));
            if (cf.getLength() > 0)
                columnsToFetch.add(new Pair<>(cf, cq));
        }
        if (!columnsToFetch.isEmpty()){
          //  AccumuloInputFormat.setBatchScan(job,true);
            AccumuloInputFormat.fetchColumns(job, columnsToFetch);}


        job.setMapperClass(TTFMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Text.class);

        job.setNumReduceTasks(0); // Pas de Reduce

        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(opts.output));

        long startTime = System.currentTimeMillis();
        job.waitForCompletion(true);
        System.out.println("Job Finished in " + (System.currentTimeMillis() - startTime) / 1000.0
                + " seconds");
        return job.isSuccessful() ? 0 : 1;
    }

    /**
     *
     * @param args
     *          instanceName zookeepers username password table columns outputpath
     */
    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new MapReduce(), args);
    }
}