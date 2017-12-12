package project.industrial;

import project.industrial.injectors.PeopleInjector;
import project.industrial.mining.GetAll;
import project.industrial.examples.*;
import project.industrial.mining.GetByList;
import project.industrial.mining.GetByRange;

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

    public static void main(String[] args) throws IOException {
        // Disable logger
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
        classes.add(GetAll.class);
        classes.add(GetByRange.class);
        classes.add(GetByList.class);
        classes.add(GetByKey.class);
        classes.add(PeopleInjector.class);
        classes.add(AddTwice.class);
        System.out.printf("%d java classes available:\n", classes.size());
        for(Class c: classes)
            System.out.printf("\t%s\n", c.getName());
    }
}
