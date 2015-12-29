package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.ExistResource;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import eu.icarus.momca.momcapi.model.resource.User;
import eu.icarus.momca.momcapi.query.ExistQuery;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import org.exist.security.Account;
import org.exist.security.Group;
import org.exist.security.internal.aider.GroupAider;
import org.exist.security.internal.aider.UserAider;
import org.exist.xmldb.RemoteUserManagementService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An implementation of <code>UserManager</code> based on an eXist MOM-CA connection.
 */
public class ExistUserManager extends AbstractExistManager implements UserManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExistUserManager.class);
    private RemoteUserManagementService remoteUserManagementService;

    /**
     * Creates a charter manager instance.
     *
     * @param momcaConnection The MOM-CA connection.
     * @param rootCollection  The database root collection, <code>/db</code>.
     */
    ExistUserManager(@NotNull ExistMomcaConnection momcaConnection, @NotNull Collection rootCollection) {

        super(momcaConnection);

        try {
            remoteUserManagementService = (RemoteUserManagementService) rootCollection.getService("UserManagementService", "1.0");
        } catch (XMLDBException e) {
            throw new MomcaException("Failed to get the UserManagementService for the remote database.", e);
        }

        LOGGER.debug("User manager instantiated.");

    }

    @Override
    public boolean add(@NotNull User user, @NotNull String password) {

        boolean success = false;
        String identifier = user.getIdentifier();

        LOGGER.info("Trying to add user '{}' to the database.", identifier);

        if (isExisting(user.getId())) {
            LOGGER.info("User '{}' already existing in the database, adding aborted.", identifier);
        } else {

            if (isExisting(user.getIdModerator())) {

                success = initialize(user.getId(), password);

                if (success) {
                    success = momcaConnection.writeExistResource(user);
                } else {
                    LOGGER.info("Failed to add eXist account for user '{}', abort writing to database.", identifier);
                }

                if (success) {

                    LOGGER.info("User '{}' added.", identifier);

                } else {

                    deleteExistUserAccount(identifier);
                    LOGGER.info("Failed to add user file for user '{}' to the database." +
                            " Aborting write and deleting created eXist user account.", identifier);

                }

            } else {
                LOGGER.info("User '{}' doesn't have a valid moderator. Adding aborted.", identifier);
            }

        }

        return success;

    }

    @Override
    public boolean changeModerator(@NotNull IdUser idUser, @NotNull IdUser idModerator) {

        boolean success = false;
        String userIdentifier = idUser.getIdentifier();
        String moderatorIdentifier = idModerator.getIdentifier();

        LOGGER.info("Trying to update moderator of userIdentifier '{}' to '{}'.", userIdentifier, moderatorIdentifier);

        Optional<User> userOptional = get(idUser);

        if (userOptional.isPresent()) {

            if (get(idModerator).isPresent()) {

                User user = userOptional.get();
                user.setModerator(moderatorIdentifier);

                success = updateUserData(user);

                if (success) {
                    LOGGER.info("Updated moderator of userIdentifier '{}' to '{}'",
                            userIdentifier, moderatorIdentifier);
                } else {
                    LOGGER.info("Failed to get updated userIdentifier '{}' from the database after changing moderator.",
                            userIdentifier);
                }

            } else {
                LOGGER.info("Moderator '{}'is not existing in database, aborting moderator change.", moderatorIdentifier);
            }

        } else {
            LOGGER.info("User '{}' is not existing, aborting moderator change.", userIdentifier);
        }

        return success;

    }

    @Override
    public boolean changeUserPassword(@NotNull User user, @NotNull String newPassword) {

        boolean success = false;

        String userName = user.getIdentifier();

        LOGGER.info("Trying to change password for user '{}'.", userName);

        if (momcaConnection.isResourceExisting(user.getUri())) {

            try {

                Account account = remoteUserManagementService.getAccount(userName);

                if (account == null) {

                    LOGGER.info("User '{}' is not initialized in eXist. Aborting password change.", userName);

                } else {

                    account.setPassword(newPassword);
                    remoteUserManagementService.updateAccount(account);

                    success = true;

                    LOGGER.info("Password for user '{}' changed.", userName);

                }

            } catch (XMLDBException e) {
                LOGGER.error("Failed to change the password for user '{}' due to an XMLDBException.", userName, e);
            }

        } else {
            LOGGER.info("User '{}' is not existing in the database, aborting password change.", userName);
        }

        return success;

    }

    @Override
    public boolean delete(@NotNull IdUser id) {

        boolean success = false;
        String identifier = id.getIdentifier();

        LOGGER.info("Trying to delete user '{}'.", identifier);

        Optional<User> userOptional = get(id);
        if (userOptional.isPresent()) {

            User user = userOptional.get();

            success = !isInitialized(id) || deleteExistUserAccount(user.getIdentifier());
            if (success) {

                success = momcaConnection.deleteResource(user);
                if (success) {

                    String uri = String.format("%s/%s", ResourceRoot.USER_DATA.getUri(), identifier);
                    success = !momcaConnection.isCollectionExisting(uri) || momcaConnection.deleteCollection(uri);

                    if (success) {
                        LOGGER.info("User '{}' deleted.", identifier);
                    } else {
                        LOGGER.info("User '{}' deleted but failed to delete user collection '{}'.", identifier, uri);
                    }

                } else {
                    LOGGER.info("Deleted eXist account for user '{}' but failed to delete user resource. ", identifier);
                }

            } else {
                LOGGER.info("Failed to delete eXist account for user '{}', aborting deletion.", identifier);
            }

        }

        return success;

    }

    boolean deleteExistUserAccount(@NotNull String accountName) {

        boolean success = false;

        LOGGER.debug("Trying to delete the eXist account '{}'.", accountName);

        try {

            Account account = remoteUserManagementService.getAccount(accountName);

            if (account == null) {

                LOGGER.debug("Account '{}' not existing, abort deletion.", accountName);

            } else {

                remoteUserManagementService.removeAccount(account);
                success = remoteUserManagementService.getAccount(accountName) == null;

                if (success) {
                    LOGGER.debug("Exist acount '{}' deleted.", accountName);
                } else {
                    LOGGER.debug("Failed to delete eXist account '{}'.", accountName);
                }

            }

        } catch (XMLDBException e) {
            LOGGER.error("Failed to delete eXist account '{}' due to an XMLDBException.", accountName, e);
        }

        return success;

    }

    @Override
    @NotNull
    public Optional<User> get(@NotNull IdUser id) {

        String identifier = id.getIdentifier();
        User user = null;

        LOGGER.info("Trying to get user '{}' from the database.", identifier);

        String resourceUri = String.format("%s/%s.xml", ResourceRoot.USER_DATA.getUri(), identifier);
        Optional<ExistResource> userResource = momcaConnection.readExistResource(resourceUri);

        if (userResource.isPresent()) {

            user = new User(userResource.get());

            LOGGER.info("User '{}' read from the database.", user);

        } else {
            LOGGER.info("User '{}' not found in database, returning nothing.", identifier);
        }

        return Optional.ofNullable(user);

    }

    @Override
    public boolean initialize(@NotNull IdUser id, @NotNull String password) {

        boolean success = false;
        String identifier = id.getIdentifier();

        LOGGER.info("Trying to initialize account for user '{}' in eXist.", identifier);

        if (!isInitialized(id)) {

            try {

                Group group = new GroupAider("atom");
                remoteUserManagementService.addGroup(group);

                Account newAccount = new UserAider(identifier, group);
                newAccount.setPassword(password);
                remoteUserManagementService.addAccount(newAccount);
                remoteUserManagementService.addAccountToGroup(identifier, "guest");

                success = true;

                LOGGER.info("Account for user '{}' initialized in eXist.", identifier);

            } catch (XMLDBException e) {
                if (!isExceptionBecauseAccountExists(identifier, e)) {
                    LOGGER.error("Failed to initialize account for user '{}' in eXist due to an XMLDBException.", identifier);
                }
            }

        } else {
            LOGGER.info("User '{}' is already initialized in eXist. Aborting initialization.", identifier);
        }

        return success;

    }

    private boolean isExceptionBecauseAccountExists(@NotNull String userName, @NotNull XMLDBException e) {

        String existingAccountErrorMessage =
                String.format("Failed to invoke method addAccount in class " +
                        "org.exist.xmlrpc.RpcConnection: Account '%s' exist", userName);

        return e.getMessage().equals(existingAccountErrorMessage);

    }

    @Override
    public boolean isExisting(@NotNull IdUser id) {

        String identifier = id.getIdentifier();

        LOGGER.info("Testing the existence of user '{}'.", identifier);

        ExistQuery query = ExistQueryFactory.checkAccountExistence(id);
        List<String> result = momcaConnection.queryDatabase(query);

        if (result.size() != 1) {
            throw new MomcaException("Failed to test for the existence of user '" + identifier + "'");
        }

        boolean isExisting = result.get(0).equals("true");

        LOGGER.info("Returning '{}' for the existence of user '{}'.", isExisting, identifier);

        return isExisting;

    }

    boolean isInitialized(@NotNull IdUser idUser) {

        boolean success = false;
        String userName = idUser.getIdentifier();

        LOGGER.debug("Try to test if user '{}' is initialized.", userName);

        try {

            Account account = remoteUserManagementService.getAccount(userName);
            success = account != null;

            LOGGER.debug("User initialization status for '{}' determined, returning '{}'.", userName, success);

        } catch (XMLDBException e) {
            LOGGER.error("Failed to test if user '{}' is initialized due to an XMLDBException. Returning 'false'.", userName, e);
        }

        return success;

    }

    @Override
    @NotNull
    public List<IdUser> list() {

        LOGGER.info("Trying to get a list of all users.");

        ExistQuery query = ExistQueryFactory.listUserIds();
        List<String> result = momcaConnection.queryDatabase(query);

        List<IdUser> userIds = result
                .stream()
                .map(IdUser::new)
                .collect(Collectors.toList());

        LOGGER.info("Returning {} users ids: {}", userIds.size(), userIds);

        return userIds;

    }

    /**
     * Update the metadata of a specific user. Doesn't update the id.
     *
     * @param updatedUser the user with updated data. Has to exist in the database.
     */
    boolean updateUserData(@NotNull User updatedUser) {

        boolean success = false;
        String identifier = updatedUser.getIdentifier();

        LOGGER.debug("Trying to update user '{}'.", identifier);

        if (momcaConnection.isResourceExisting(updatedUser.getUri())) {

            success = momcaConnection.writeExistResource(updatedUser);

            if (success) {
                LOGGER.debug("Updated user '{}'.", identifier);
            } else {
                LOGGER.debug("Failed to update user data.");
            }

        } else {
            LOGGER.debug("User '{}' doesn't exist in the database, aborting update.", identifier);
        }

        return success;

    }

}
