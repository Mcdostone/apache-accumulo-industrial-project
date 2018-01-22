package project.industrial.benchmark.main;

        import java.io.IOException;
        import java.util.StringTokenizer;

        import org.apache.accumulo.core.client.ClientConfiguration;
        import org.apache.accumulo.core.client.mapreduce.AccumuloInputFormat;
        import org.apache.commons.cli.*;
        import org.apache.hadoop.conf.Configuration;
        import org.apache.hadoop.fs.Path;
        import org.apache.hadoop.io.IntWritable;
        import org.apache.hadoop.io.Text;
        import org.apache.hadoop.mapreduce.Job;
        import org.apache.hadoop.mapreduce.Mapper;
        import org.apache.hadoop.mapreduce.Reducer;
        import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
        import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class TestMapRedWC {
    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable>{

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString().replace(",", " ")); // for CSV file
            // StringTokenizer itr = new StringTokenizer(value.toString(); Otherwise
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, one);
            }
        }
    }

    public static class IntSumReducer
            extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();
        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
           context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);
        Option output = new Option("o", "output", true, "output file");
        output.setRequired(true);
        options.addOption(output);
        CommandLineParser parser = new GnuParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
            return;
        }
        String inputFilePath = cmd.getOptionValue("input");
        String outputFilePath = cmd.getOptionValue("output");
        System.out.println(inputFilePath);
        System.out.println(outputFilePath);


        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "TestMapRedWC");
        job.setJarByClass(TestMapRedWC.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
       // FileInputFormat.addInputPath(job, new Path("hdfs:/path/to/file"));
       // FileInputFormat.addInputPath(job, new Path(args[0]));
        //FileOutputFormat.setOutputPath(job, new Path(args[1]));
        FileInputFormat.addInputPath(job, new Path(inputFilePath));
        FileOutputFormat.setOutputPath(job, new Path(outputFilePath));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

     /*   job.setInputFormatClass(AccumuloInputFormat.class);
        ClientConfiguration zkiConfig = new ClientConfiguration() .withInstance(args[0])
                .withZkHosts(args[1]);
        AccumuloInputFormat.setInputTableName(job, WikipediaConstants.ARTICLES_TABLE); List<Pair<Text,Text>> columns = new ArrayList<>();
        columns.add(new Pair(WikipediaConstants.CONTENTS_FAMILY_TEXT, new Text("")));
        AccumuloInputFormat.fetchColumns(job, columns); AccumuloInputFormat.setZooKeeperInstance(job, zkiConfig); AccumuloInputFormat.setConnectorInfo(job, args[2], new PasswordToken(args[3]));
*/
    }
}
