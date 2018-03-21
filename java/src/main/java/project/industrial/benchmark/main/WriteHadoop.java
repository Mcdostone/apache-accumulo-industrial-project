package project.industrial.benchmark.main;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.MapReduceClientOnRequiredTable;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.mapreduce.AccumuloOutputFormat;
import org.apache.accumulo.core.client.mapreduce.lib.partition.RangePartitioner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.util.TextUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.accumulo.core.data.Mutation;
//import project.industrial.benchmark.mapReduce.InjectMapRed;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Base64;
import java.util.Collection;

/**
 * Injection de données via MapReduce. Le fichier
 * doit être stocké sur HDFS.
 *
 */
public class WriteHadoop extends Configured implements Tool {

    static class Opts extends MapReduceClientOnRequiredTable {
        @Parameter(names = "--inputDir", required = true)
        String inputDir;
    }

    private Text outputKey = new Text();
    private Text outputValue = new Text();


    /**
     * The Mapper class. For each input line, write one line in accumulo for each column.
     */
    public static class MapClass extends Mapper<LongWritable,Text,Text,Mutation> {
        private Text outputKey = new Text();
        private Text outputValue = new Text();

        public void map(LongWritable key, Text value, Context output) throws IOException, InterruptedException {

            String[] stringRow = value.toString().split("\n");

            for (String values: stringRow) {
                String[] stringCol = values.toString().split(",");

                String rowID = randomAlphabetic(10).toLowerCase();

                Mutation mutation = new Mutation(new Text(rowID));
                mutation.put(new Text("meta"), new Text("date"), new Value(stringCol[0]));
                mutation.put(new Text("meta"), new Text("nom"), new Value(stringCol[1]));
                mutation.put(new Text("meta"), new Text("prenom"), new Value(stringCol[2]));
                mutation.put(new Text("meta"), new Text("email"), new Value(stringCol[3]));
                mutation.put(new Text("meta"), new Text("url"), new Value(stringCol[4]));
                mutation.put(new Text("meta"), new Text("ip"), new Value(stringCol[5]));
                output.write(null, mutation);
            }

        }
    }

    public int run(String[] args) throws Exception{
        Opts opts = new Opts();
        opts.parseArgs(WriteHadoop.class.getName(), args);

        Configuration conf = getConf();

        Job job = Job.getInstance(conf);
        job.setJobName("MapRed ingest accumulo");
        job.setJarByClass(this.getClass());
        job.setInputFormatClass(TextInputFormat.class);

        job.setMapperClass(MapClass.class);

        job.setNumReduceTasks(0);

        job.setOutputKeyClass(Text.class);
        job.setOutputFormatClass(AccumuloOutputFormat.class);
        job.setOutputValueClass(Mutation.class);
        opts.setAccumuloConfigs(job);

        Connector connector = opts.getConnector();

        TextInputFormat.setInputPaths(job, new Path(opts.inputDir));

        long startTime = System.currentTimeMillis();
            job.waitForCompletion(true);
         System.out.println("Job Finished in " + (System.currentTimeMillis() - startTime) / 1000.0
                + " seconds");    
        
        return 0;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new WriteHadoop(), args);
        
    }
}