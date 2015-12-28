package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.resource.Archive;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 28/12/2015.
 */
public interface ArchiveManager {

    boolean add(@NotNull Archive newArchive);

    /**
     * Deletes an archive from the database. The archive is not allowed to still have existing fonds.
     *
     * @param idArchive The archive to delete.
     * @return True if the process was successful. Note: Returns true, even if the deletion of any empty fond
     * eXist-collections didn't succeed.
     */
    boolean delete(@NotNull IdArchive idArchive);

    @NotNull
    Optional<Archive> get(@NotNull IdArchive idArchive);

    boolean isExisting(@NotNull IdArchive idArchive);

    @NotNull
    List<IdArchive> list();

    @NotNull
    List<IdArchive> list(@NotNull Region region);

    @NotNull
    List<IdArchive> list(@NotNull Country country);

}
