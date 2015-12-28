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
 * Created by djell on 28/12/2015.
 */
public interface MyCollectionManager {
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
     * @param id     The myCollection to delete.
     * @param idUser The creator of the myColleciton. If @code{NULL}, the myCollection is considered
     *               as a public myCollection.
     * @return True if the deletion was successful. Note: still returns true, if the method couldn't delete
     * any empty charters' collections.
     */
    boolean delete(@NotNull IdMyCollection id, @Nullable IdUser idUser);

    @NotNull
    Optional<MyCollection> get(@NotNull IdMyCollection id, @NotNull MyCollectionStatus status);

    boolean isExisting(@NotNull IdMyCollection idMyCollection, @NotNull MyCollectionStatus myCollectionStatus);

    @NotNull
    List<IdMyCollection> listPrivateMyCollections(@NotNull IdUser idUser);

    @NotNull
    List<IdMyCollection> listPublicMyCollections();
}
