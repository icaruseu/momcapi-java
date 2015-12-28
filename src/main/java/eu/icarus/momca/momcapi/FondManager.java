package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.id.IdFond;
import eu.icarus.momca.momcapi.model.resource.Fond;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 28/12/2015.
 */
public interface FondManager {

    boolean add(@NotNull Fond fond);

    /**
     * Deletes a fond from the database. The fond is not allowed to still have existing charters.
     *
     * @param idFond The fond to delete.
     * @return True if the deletion is successful. Note: still returns true if the process wasn't able to delete
     * any empty charter eXist-collections.
     */
    boolean delete(@NotNull IdFond idFond);

    @NotNull
    Optional<Fond> get(@NotNull IdFond idFond);

    boolean isExisting(@NotNull IdFond idFond);

    @NotNull
    List<IdFond> list(@NotNull IdArchive idArchive);

}
