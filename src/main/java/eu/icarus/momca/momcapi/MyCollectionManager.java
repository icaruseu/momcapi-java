package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.id.IdMyCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.MyCollection;
import eu.icarus.momca.momcapi.model.resource.MyCollectionStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * The myCollection manager. Performs myCollection-related tasks in the database.
 */
@SuppressWarnings("AccessCanBeTightened")
public interface MyCollectionManager {

    /**
     * Adds a myCollection to the database.
     *
     * @param myCollection The myCollection to add.
     * @return <code>True</code> if the action was successful.
     */
    boolean add(@NotNull MyCollection myCollection);

    /**
     * Deletes a public myCollection from the database. Doesn't delete myCollections that still have charters.
     *
     * @param id The myCollection to delete.
     * @return True if the deletion was successful. Note: still returns true, if the method couldn't delete
     * any empty charters' collections.
     */
    boolean delete(@NotNull IdMyCollection id);

    /**
     * Deletes a myCollection from the database. Doesn't delete private myCollections that still have a public
     * myCollection or myCollections that still have charters.
     *
     * @param id   The myCollection to delete.
     * @param user The creator of the myColleciton. If @code{NULL}, the myCollection is considered
     *             as a public myCollection.
     * @return True if the deletion was successful. Note: still returns true, if the method couldn't delete
     * any empty charters' collections.
     */
    boolean delete(@NotNull IdMyCollection id, @Nullable IdUser user);

    /**
     * Gets a myCollection from the database.
     *
     * @param id     The mycollection to get.
     * @param status The status of the mycollection to get.
     * @return The myCollection wrapped in an <code>Optional</code>
     */
    @NotNull
    Optional<MyCollection> get(@NotNull IdMyCollection id, @NotNull MyCollectionStatus status);

    /**
     * Checks if a myCollection is existing in the database.
     *
     * @param id     The id of the myCollection to check.
     * @param status The status to check for.
     * @return <code>True</code> if a myCollection with the specified id and status exists in the database.
     */
    boolean isExisting(@NotNull IdMyCollection id, @NotNull MyCollectionStatus status);

    /**
     * Lists the private mycollections associated with a specific user.
     *
     * @param id The user to associate with.
     * @return A list of the ids of all myCollections associated with the user.
     */
    @NotNull
    List<IdMyCollection> listPrivateMyCollections(@NotNull IdUser id);

    /**
     * Lists all public myCollections in the database.
     *
     * @return A list of the ids of all myCollections in the database.
     */
    @NotNull
    List<IdMyCollection> listPublicMyCollections();

}
