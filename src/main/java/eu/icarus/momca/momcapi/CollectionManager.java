package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.model.resource.Collection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by djell on 28/12/2015.
 */
public interface CollectionManager {

    boolean add(@NotNull Collection collection);

    /**
     * Deletes a charters collection from the database. Only collections without existing charters are deleted.
     *
     * @param idCollection The collection to delete.
     * @return True if the deletion was successful. Note: still returns true even if the deletion of any empty
     * charters eXist-collections didn't succeed.
     */
    boolean delete(@NotNull IdCollection idCollection);

    @NotNull
    Optional<Collection> get(@NotNull IdCollection idCollection);

    boolean isExisting(@NotNull IdCollection idCollection);

    @NotNull
    List<IdCollection> list(@NotNull Country country);

    @NotNull
    List<IdCollection> list(@NotNull Region region);

    @NotNull
    List<IdCollection> list();

}
