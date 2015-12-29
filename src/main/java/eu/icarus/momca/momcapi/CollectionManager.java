package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.model.resource.Collection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * The collection manager. Performs archival collation-related tasks in the MOM-CA database.
 */
public interface CollectionManager {

    /**
     * Adds a new archival collection to the database.
     *
     * @param collection The collection to add.
     * @return <code>True</code> if the action was successful.
     */
    boolean add(@NotNull Collection collection);

    /**
     * Deletes a archival collection from the database. Works only for collections without existing charters are deleted.
     *
     * @param id The archival collection to delete.
     * @return True if the deletion was successful. Note: still returns true even if the deletion of any empty
     * charters eXist-collections didn't succeed.
     */
    boolean delete(@NotNull IdCollection id);

    /**
     * Gets an archival collection from the database.
     *
     * @param id The id of the archival collection to get.
     * @return The archival collection wrapped in an <code>Optional</code>.
     */
    @NotNull
    Optional<Collection> get(@NotNull IdCollection id);

    /**
     * Checks if a specific archival collection exists in the database.
     *
     * @param id The id of the archival collection to check.
     * @return <code>True</code> if the archival collection is existing.
     */
    boolean isExisting(@NotNull IdCollection id);

    /**
     * List all archival collections associated with a specific country in the database.
     *
     * @param country The country to associate with.
     * @return A list of all the ids of archival collections for the selected country.
     */
    @NotNull
    List<IdCollection> list(@NotNull Country country);

    /**
     * List all archival collections associated with a specific region in the database.
     *
     * @param region The region to associate with.
     * @return A list of all the ids of archival collections for the selected region.
     */
    @NotNull
    List<IdCollection> list(@NotNull Region region);

    /**
     * List all archival collections in the database.
     *
     * @return A list of the ids of all archival collections in the database.
     */
    @NotNull
    List<IdCollection> list();

}
