package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.id.IdFond;
import eu.icarus.momca.momcapi.model.resource.Fond;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * The fond manager. Performs fond-related tasks in the database.
 */
@SuppressWarnings("AccessCanBeTightened")
public interface FondManager {

    /**
     * Adds a fond to the database.
     *
     * @param fond The fond to add.
     * @return <code>True</code> if the action was successful.
     */
    boolean add(@NotNull Fond fond);

    /**
     * Deletes a fond from the database. The fond is not allowed to still have existing charters.
     *
     * @param id The fond to delete.
     * @return True if the deletion is successful. Note: still returns true if the process wasn't able to delete
     * any empty charter eXist-collections.
     */
    boolean delete(@NotNull IdFond id);

    /**
     * Gets a fond from the database.
     *
     * @param id The fond to get.
     * @return The fond wrapped in an <code>Optional</code>.
     */
    @NotNull
    Optional<Fond> get(@NotNull IdFond id);

    /**
     * Checks the existence of a specific fond in the database.
     *
     * @param id The fond to check.
     * @return <code>True</code> if the fond exists.
     */
    boolean isExisting(@NotNull IdFond id);

    /**
     * Lists all fonds associated with a specific archive in the database.
     *
     * @param id The archive to associate with.
     * @return A list of the ids of all fonds associated with the archive.
     */
    @NotNull
    List<IdFond> list(@NotNull IdArchive id);

}
