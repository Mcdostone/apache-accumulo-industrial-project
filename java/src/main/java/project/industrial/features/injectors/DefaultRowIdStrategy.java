package project.industrial.features.injectors;

/**
 * Stratégie par défault pour la génération d'une ROW ID.
 *
 * @author Yann Prono
 */
public class DefaultRowIdStrategy implements RowIdStrategy {

    @Override
    public String getRowId(String rowId) {
        return rowId;
    }
}
