package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * The user manager. Performs user-related tasks in the database.
 */
@SuppressWarnings("AccessCanBeTightened")
public interface UserManager {

    /**
     * Adds a new user to the database.
     *
     * @param user     The user to add.
     * @param password The user password.
     * @return <code>True</code> if the action was successful.
     */
    boolean add(@NotNull User user, @NotNull String password);

    /**
     * Changes the moderator of an user to another (existing) user.
     *
     * @param idUser      The user to change.
     * @param idModerator The new moderator.
     * @return <code>True</code> if the action was successful.
     */
    boolean changeModerator(@NotNull IdUser idUser, @NotNull IdUser idModerator);

    /**
     * Changes the password of a user.
     *
     * @param user        The user to change.
     * @param newPassword The new password
     * @return <code>True</code> if the action was successful.
     */
    boolean changeUserPassword(@NotNull User user, @NotNull String newPassword);

    /**
     * Deletes a user from the database.
     *
     * @param id The user to delete.
     * @return <code>True</code> if the action was successful.
     */
    boolean delete(@NotNull IdUser id);

    /**
     * Gets a user from the database.
     *
     * @param id The user to get.
     * @return The user wrapped in an <code>Optional</code>.
     */
    @NotNull
    Optional<User> get(@NotNull IdUser id);

    /**
     * Initializes a user in the database. This is neccessary (for example), if a user didn't get/click the
     * activation link sent to them by mail.
     *
     * @param id       The id of the user to initialize.
     * @param password The password.
     * @return <code>True</code> if the action was successful.
     */
    boolean initialize(@NotNull IdUser id, @NotNull String password);

    /**
     * Checks if a user is existing in the database.
     *
     * @param id The id of the user to check.
     * @return <code>True</code> if the user exists.
     */
    boolean isExisting(@NotNull IdUser id);

    /**
     * Lists all users in the database.
     *
     * @return A list of the ids of all users in the database.
     */
    @NotNull
    List<IdUser> list();

}
