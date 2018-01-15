package project.industrial.benchmark.injectors;

public class IncrementorRowIdIBuilderStrategy implements RowIdBuilderStrategy {

    private int index;

    public IncrementorRowIdIBuilderStrategy() {
        this.index = 0;
    }

    @Override
    public String buildRowId() {
        return String.valueOf(this.index++);
    }

}
