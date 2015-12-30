package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.id.IdAbstract;
import eu.icarus.momca.momcapi.model.id.IdCharter;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.Charter;
import eu.icarus.momca.momcapi.model.resource.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * The charter manager. Performs charter-related tasks in the MOM-CA database.
 */
@SuppressWarnings("AccessCanBeTightened")
public interface CharterManager {

    /**
     * Adds a charter to the database.
     *
     * @param charter The charter to add.
     * @return <code>True</code> if the action was successful.
     */
    boolean add(@NotNull Charter charter);

    /**
     * Deletes the public instance of a charter from the database.
     *
     * @param id The id of the charter to delete.
     * @return <code>True</code> if the process was successful.
     */
    boolean delete(@NotNull IdCharter id);

    /**
     * Deletes a charter instance from the database. Note: To delete a <code>PRIVATE</code> charter, its author needs
     * to be provided.
     *
     * @param id     The id of the charter to delete.
     * @param status The status of the charter to delete.
     * @param author The author of the charter to delete. Can be <code>NULL</code> if the charter
     *               is not <code>PRIVATE</code>.
     * @return <code>True</code> if the action was successful.
     */
    boolean delete(@NotNull IdCharter id, @NotNull CharterStatus status, @Nullable IdUser author);

    /**
     * Exports instances of charters to the local hard disk.
     *
     * @param charters The list of charters to export.
     * @param status   The status of the charters to be exported.
     * @param target   The location to save the charters to.
     * @param ceiOnly  <code>True</code> if the file should contain only the cei content of the charter,
     *                 <code>false</code> if the whole charter xml content should be exported, including the <code>ATOM</code> elements.
     */
    @SuppressWarnings("unused")
    void export(@NotNull List<IdCharter> charters, @NotNull CharterStatus status, @NotNull File target, boolean ceiOnly);

    /**
     * Gets a charter instance from the database.
     *
     * @param id     The charter to get.
     * @param status The status of the charter to get.
     * @return The charter wrapped in an optional.
     */
    @NotNull
    Optional<Charter> get(@NotNull IdCharter id, @NotNull CharterStatus status);

    /**
     * Gets all instances, for example the public and saved ones, of a charter from the database.
     *
     * @param id The id of the charter.
     * @return A list with all charter instances.
     */
    @NotNull
    List<Charter> getInstances(@NotNull IdCharter id);

    /**
     * Checks if a specific charter instance exists in the database.
     *
     * @param id     The id of the charter.
     * @param status The status of the charter.
     * @return <code>True</code> if the charter instance exists.
     */
    boolean isExisting(@NotNull IdCharter id, @Nullable CharterStatus status);

    /**
     * Lists all public charters associated with a specific parent, e.g. all public charters in a specific
     * fond or by a specific user.
     *
     * @param parent The id of the parent to limit to, e.g. an archive, fond or user.
     * @return A list of the ids of all charters that match the specific parent.
     */
    @NotNull
    List<IdCharter> list(@NotNull IdAbstract parent);

    /**
     * Lists all charters associated with a parent and status, e.g. all saved charters belonging to a specific
     * archival fond or all private charters of a user.
     *
     * @param parent The id of the parent to limit to, e.g. an archive, fond or user.
     * @param status The status of the charters to limit to, e.g. only imported charters.
     * @return A list of the ids of all charters that match the specified parent / status combination.
     */
    @NotNull
    List<IdCharter> list(@NotNull IdAbstract parent, @NotNull CharterStatus status);

    /**
     * Lists charters that are listed in a users file without them being properly saved to metadata.charter.saved.
     * This is due to an old bug in the database.
     *
     * @param id the User
     * @return A list of improperly saved charters.
     */
    @NotNull
    List<IdCharter> listNotExistingSavedCharters(@NotNull IdUser id);

    /**
     * Publishes a saved charter by overwriting the existing public instance of a charter with the saved instance.
     * Deletes the saved instance.
     *
     * @param user The user that created the saved instance.
     * @param id   The id of the charter to publish.
     * @return True if the process was successful.
     */
    boolean publishSavedCharter(@NotNull User user, @NotNull IdCharter id);

    /**
     * Updates the CEI content of a charter. The ATOM content, apart from @code{atom:updated} will not be updated
     * (for example @code{atom:author}).
     *
     * @param updatedCharter The charter with updated content.
     * @return true if the update was successful.
     */
    boolean update(@NotNull Charter updatedCharter);

    /**
     * Changes the id of a charter in the database.
     *
     * @param newId      The new id.
     * @param originalId The original id.
     * @param status     The status of the charter to change.
     * @param author     The author of the charter to change. Can be <code>NULL</code> for charters that are not private.
     * @return <code>True</code> if the change was successful.
     */
    boolean updateId(@NotNull IdCharter newId, @NotNull IdCharter originalId,
                     @NotNull CharterStatus status, @Nullable IdUser author);

    /**
     * Changes the status of a charter in the database.
     *
     * @param newStatus      The new status of the charter.
     * @param originalStatus The original status of the charter.
     * @param id             The id of the charter to change.
     * @param author         The author of the charter to change. Can be <code>NULL</code> for charters that are not private.
     * @return <code>True</code> if the change was successful.
     */
    boolean updateStatus(@NotNull CharterStatus newStatus, @NotNull CharterStatus originalStatus,
                         @NotNull IdCharter id, @Nullable IdUser author);

}
