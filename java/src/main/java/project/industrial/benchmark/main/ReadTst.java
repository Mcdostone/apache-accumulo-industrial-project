package project.industrial.benchmark.main;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.accumulo.core.client.mapreduce.*;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import java.util.*;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.util.Pair;
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

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "ReadTst");
        job.setInputFormatClass(AccumuloInputFormat.class);
        AccumuloInputFormat.setInputTableName(job, "trace");
        ClientConfiguration zkiConfig = new ClientConfiguration().withInstance("accumulo")
                .withZkHosts("localhost:2181");
        AccumuloInputFormat.setZooKeeperInstance(job, zkiConfig);
        AccumuloInputFormat.setConnectorInfo(job, "root", new PasswordToken("root"));
        List<Pair<Text, Text>> columns = new ArrayList<>();

        columns.add(new Pair(new Text("colFam"), new Text("colQual")));
    //    AccumuloInputFormat.fetchColumns(job, columns); // optional

        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range("a", "k"));
      //  AccumuloInputFormat.setRanges(job, ranges); // optional
       // AccumuloInputFormat.setScanIsolation(job, true); // optional
        // AccumuloInputFormat.setScanAuthorizations(job, auths); // optional
    }
}