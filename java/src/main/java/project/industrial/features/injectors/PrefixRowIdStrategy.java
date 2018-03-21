package project.industrial.features.injectors;

/**
 * Stratégie pour la génération de la ROW ID, permettant
 * d'ajouter un prefix.
 *
 * @author Yann Prono
 */
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
