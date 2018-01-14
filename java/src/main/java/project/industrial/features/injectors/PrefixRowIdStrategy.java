package project.industrial.features.injectors;

public class PrefixRowIdStrategy implements RowIdStrategy{
    private String prefix;

    public PrefixRowIdStrategy(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getRowId(String rowId) {
        return String.format("%s_%s", this.prefix, rowId);
    }
}
