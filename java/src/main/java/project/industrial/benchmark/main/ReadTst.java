package project.industrial.benchmark.main;

import com.beust.jcommander.Parameter;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.Scanner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.accumulo.core.client.mapreduce.*;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import java.util.*;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.util.Pair;
import org.apache.log4j.Logger;
import project.industrial.features.GetByKey;
import org.apache.commons.cli.*;
import project.industrial.features.Printer;
import project.industrial.features.mining.GetByRange;
       /* import java.util.ArrayList;
        import java.util.Collection;
        import org.apache.accumulo.core.cli.ClientOnRequiredTable;
        import org.apache.accumulo.core.cli.ScannerOpts;
        import org.apache.accumulo.core.client.*;
        import org.apache.accumulo.core.data.Range;
        import org.apache.hadoop.io.Text;
        import org.apache.log4j.Logger;
        import com.beust.jcommander.Parameter;
        import project.industrial.features.Printer; */

@SuppressWarnings("unchecked")

public class ReadTst {



    public static void main(String[] args) throws Exception {


        Options options = new Options();
        Option input = new Option("colF", "Column Family", true, "Column Family");
        input.setRequired(true);
        options.addOption(input);
        Option output = new Option("colQ", "Column Qualifier", true, "Column QUalifier");
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
        String CF = cmd.getOptionValue("Column Family");
        String CQ = cmd.getOptionValue("Column Qualifier");
        System.out.println(CF);
        System.out.println(CQ);





    Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ReadTst");
        job.setInputFormatClass(AccumuloInputFormat.class);
        AccumuloInputFormat.setInputTableName(job, "trace");
        ClientConfiguration zkiConfig = new ClientConfiguration().withInstance("accumulo")
                .withZkHosts("localhost:2181");
        AccumuloInputFormat.setZooKeeperInstance(job, zkiConfig);
        AccumuloInputFormat.setConnectorInfo(job, "root", new PasswordToken("root"));
        List<Pair<Text, Text>> columns = new ArrayList<>();

        columns.add(new Pair(new Text(CF), new Text(CQ)));
        AccumuloInputFormat.fetchColumns(job, columns); // optional
        System.out.println(columns.toString());
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range("a", "p"));
        AccumuloInputFormat.setRanges(job, ranges); // optional
        AccumuloInputFormat.setScanIsolation(job, true); // optional
        // AccumuloInputFormat.setScanAuthorizations(job, auths); // optional
        System.out.println("done");
        System.out.println(ranges);
    }
}