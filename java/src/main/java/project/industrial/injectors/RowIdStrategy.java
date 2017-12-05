package project.industrial.injectors;

/**
 * Define how to generate a row ID
 * @author Yann Prono
 */
public interface RowIdStrategy {

    public String getRowId(String rowId);
}
