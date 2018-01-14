package project.industrial.features.injectors;

/**
 * Define how to generate a row ID
 * @author Yann Prono
 */
public interface RowIdStrategy {

    public String getRowId(String rowId);
}
