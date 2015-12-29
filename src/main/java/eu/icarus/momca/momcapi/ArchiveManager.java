package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.resource.Archive;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * The archive manager. Performs archive-related tasks in the database.
 */
interface ArchiveManager {

    /**
     * Adds a new archive to the database.
     *
     * @param archive The archive to add.
     * @return <code>True</code> if the process was successful.
     */
    boolean add(@NotNull Archive archive);

    /**
     * Deletes an archive from the database. The archive is not allowed to still have existing fonds.
     *
     * @param id The archive to delete.
     * @return <code>True</code> if the process was successful. Note: Returns <code>true</code>, even if the
     * method couldn't delete of any empty fond eXist-collections.
     */
    boolean delete(@NotNull IdArchive id);

    /**
     * Gets an archive from the database.
     *
     * @param id The archive to get.
     * @return The Archive wrapped in an <code>Optional</code>.
     */
    @NotNull
    Optional<Archive> get(@NotNull IdArchive id);

    /**
     * Checks if a specific archive exists in the database.
     *
     * @param id The archive to check for.
     * @return <code>True</code> if the archive is existing.
     */
    boolean isExisting(@NotNull IdArchive id);

    /**
     * @return A list of all archives in the database.
     */
    @NotNull
    List<IdArchive> list();

    /**
     * @param region The region to find archives in.
     * @return A list of all archives in a certain region.
     */
    @NotNull
    List<IdArchive> list(@NotNull Region region);

    /**
     * @param country The country to find archives in.
     * @return A list of all archives in a certain country.
     */
    @NotNull
    List<IdArchive> list(@NotNull Country country);

}
