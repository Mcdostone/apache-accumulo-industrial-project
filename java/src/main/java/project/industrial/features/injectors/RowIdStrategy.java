package project.industrial.features.injectors;

/**
 * Cette classe définit comment générer une ROW ID.
 *
 * @author Yann Prono
 */
public interface RowIdStrategy {

    public String getRowId(String rowId);
}
