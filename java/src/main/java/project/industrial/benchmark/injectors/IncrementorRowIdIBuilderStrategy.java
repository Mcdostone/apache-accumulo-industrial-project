package project.industrial.benchmark.injectors;

public class IncrementorRowIdIBuilderStrategy implements RowIdBuilderStrategy {

    /** We keep the index in memory (like autoincrement in SQL) */
    private int index;

    public IncrementorRowIdIBuilderStrategy() {
        this.index = 0;
    }

    @Override
    public String buildRowId() {
        return String.valueOf(this.index++);
    }

}
