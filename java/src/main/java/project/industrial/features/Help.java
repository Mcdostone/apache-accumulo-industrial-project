package project.industrial.features;

import project.industrial.features.injectors.AddColumn;
import project.industrial.features.injectors.PeopleInjector;
import project.industrial.examples.*;
import project.industrial.features.mining.GetByList;
import project.industrial.features.mining.GetByRange;
import project.industrial.features.mining.GeneralScan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays the list of all availables java classes
 * that can be executed by accumulo.
 *
 * @author Yann Prono
 */
public class Help {

    public static void main(String[] args) {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(Flush.class);
        classes.add(RandomBatchScanner.class);
        classes.add(RandomBatchWriter.class);
        classes.add(ReadWriteExample.class);
        classes.add(InsertWithBatchWriter.class);
        classes.add(RowOperations.class);
        classes.add(SequentialBatchWriter.class);
        classes.add(TraceDumpExample.class);
        classes.add(TracingExample.class);
        classes.add(PartitioningBatchWriter.class);
        classes.add(AddColumn.class);
        classes.add(GetByRange.class);
        classes.add(GetByList.class);
        classes.add(GetByKey.class);
        classes.add(GeneralScan.class);
        classes.add(GeneralDelete.class);
        classes.add(DeleteCol.class);
        classes.add(PeopleInjector.class);
        classes.add(AddTwice.class);
        classes.add(SetTTL.class);
        classes.add(Update.class);
        System.out.printf("%d java classes available for testing features:\n", classes.size());
        for(Class c: classes)
            System.out.printf("\t%s\n", c.getName());
    }
}
