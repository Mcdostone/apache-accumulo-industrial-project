package project.industrial.features.injectors;

public class DefaultRowIdStrategy implements RowIdStrategy {

    @Override
    public String getRowId(String rowId) {
        return rowId;
    }
}
