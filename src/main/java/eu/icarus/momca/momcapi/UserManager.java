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
import org.xmldb.api.base.XMLDBException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages users in MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 03.07.2015.
 * @see
 */
public class UserManager extends AbstractManager {

    @NotNull
    private static final String NEW_USER_CONTENT = "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\">" +
            " <xrx:username /> <xrx:password /> <xrx:firstname>%s</xrx:firstname> <xrx:name>%s</xrx:name> " +
            "<xrx:email>%s</xrx:email> <xrx:moderator>%s</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town />" +
            " <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list />" +
            " </xrx:storage> </xrx:user>";

    /**
     * Instantiates a new UserManager.
     *
     * @param momcaConnection The connection to the database.
     */
    UserManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    /**
     * Adds a new user to MOM-CA.
     *
     * @param userName      The name of the user.
     * @param password      The password of the user.
     * @param moderatorName The name of an existing moderator.
     * @param firstName     The first name of the user.
     * @param lastName      The last name of the user.
     * @return The user just added to the database.
     */
    public User addUser(@NotNull String userName, @NotNull String password, @NotNull String moderatorName,
                        @NotNull String firstName, @NotNull String lastName) {

        IdUser idUser = new IdUser(userName);

        if (!getUser(idUser).isPresent()) {

            if (!getUser(new IdUser(moderatorName)).isPresent()) {
                throw new IllegalArgumentException("Moderator '" + moderatorName + "' not existing in database.");
            }

            String xmlContent = createUserResourceContent(userName, moderatorName, firstName, lastName);
            ExistResource userResource = new ExistResource(userName + ".xml", ResourceRoot.USER_DATA.getUri(), xmlContent);
            momcaConnection.storeExistResource(userResource);
            initializeUser(idUser, password);

        }

        return getUser(idUser).orElseThrow(RuntimeException::new);

    }

    /**
     * Adds a new user to MOM-CA.
     *
     * @param userName      The name of the user.
     * @param password      The password of the user.
     * @param moderatorName The name of an existing moderator.
     * @return The user just added to the database.
     */
    public User addUser(@NotNull String userName, @NotNull String password, @NotNull String moderatorName) {
        return addUser(userName, password, moderatorName, "New", "User");
    }

    /**
     * Changes the moderator of an user.
     *
     * @param user         The user to change.
     * @param newModerator The new moderator.
     * @return The updated user.
     */
    @NotNull
    public User changeModerator(@NotNull User user, @NotNull User newModerator) {

        String newModeratorElement = String.format("<xrx:moderator>%s</xrx:moderator>", newModerator.getIdentifier());
        String userUri = user.getUri();
        ExistQuery query = ExistQueryFactory.replaceFirstInResource(userUri, "xrx:moderator", newModeratorElement);

        momcaConnection.queryDatabase(query);

        return getUser(user.getId()).orElseThrow(RuntimeException::new);

    }

    /**
     * Changes the password of an user.
     *
     * @param user        The user to change.
     * @param newPassword The new password.
     */
    public void changeUserPassword(@NotNull User user, @NotNull String newPassword) {

        String userName = user.getIdentifier();

        try {

            RemoteUserManagementService service = getUserService();
            Account account = service.getAccount(userName);

            if (account != null) {
                account.setPassword(newPassword);
                service.updateAccount(account);
            }

        } catch (XMLDBException e) {
            throw new MomcaException("Failed to change the password for '" + userName + "'", e);
        }

    }

    private String createUserResourceContent(@NotNull String userName, @NotNull String moderatorName,
                                             @NotNull String firstName, @NotNull String lastName) {
        return String.format(NEW_USER_CONTENT, firstName, lastName, userName, moderatorName);
    }

    /**
     * Deletes an eXist user account. This does not delete the MOM-CA-user file and collection in {@code xrx.user}.
     *
     * @param accountName The name of the account to delete.
     */
    void deleteExistUserAccount(@NotNull String accountName) {

        try {

            RemoteUserManagementService service = getUserService();
            Account account = service.getAccount(accountName);

            if (account != null) {
                service.removeAccount(account);
            }

        } catch (XMLDBException e) {
            throw new MomcaException("Failed to remove account '" + accountName + "'", e);
        }

    }

    /**
     * Deletes an existing user from MOM-CA.
     *
     * @param idUser The id of the user to delete.
     */
    public void deleteUser(@NotNull IdUser idUser) {

        getUser(idUser).ifPresent(u -> {
            deleteExistUserAccount(u.getIdentifier());
            momcaConnection.deleteExistResource(u);
            momcaConnection.deleteCollection(ResourceRoot.USER_DATA.getUri() + "/" + u.getIdentifier());
        });

    }

    /**
     * Gets a user from the database.
     *
     * @param idUser The id of the user.
     * @return The user.
     */
    @NotNull
    public Optional<User> getUser(@NotNull IdUser idUser) {
        return momcaConnection.getExistResource(idUser.getIdentifier() + ".xml", ResourceRoot.USER_DATA.getUri())
                .flatMap(existResource -> Optional.of(new User(existResource)));
    }

    @NotNull
    private RemoteUserManagementService getUserService() {

        try {
            return (RemoteUserManagementService) momcaConnection.getRootCollection()
                    .getService("UserManagementService", "1.0");
        } catch (XMLDBException e) {
            throw new MomcaException("Failed to get the UserManagementService for the remote database.", e);
        }

    }

    /**
     * Initializes a registered but uninitialized user. This happens if the User doesn't receive the registration-email
     * or doesn't click on the confirmation link. Before this, the user is added to {@code xrx.user} but not added as
     * an eXist-account.
     *
     * @param idUser   The id of an uninitialized user.
     * @param password The password.
     * @return The initialized user.
     */
    public User initializeUser(@NotNull IdUser idUser, @NotNull String password) {

        String userName = idUser.getIdentifier();

        if (!isUserInitialized(idUser)) {

            try {

                RemoteUserManagementService service = getUserService();

                Group group = new GroupAider("atom");
                service.addGroup(group);

                Account newAccount = new UserAider(userName, group);
                newAccount.setPassword(password);
                service.addAccount(newAccount);
                service.addAccountToGroup(userName, "guest");

            } catch (XMLDBException e) {
                if (!isExceptionBecauseAccountExists(userName, e)) {
                    throw new MomcaException("Failed to create user '" + userName + "'", e);
                }
            }

        }

        return getUser(idUser).orElseThrow(RuntimeException::new);

    }

    private boolean isExceptionBecauseAccountExists(@NotNull String userName, @NotNull XMLDBException e) {
        String existingAccountErrorMessage =
                String.format("Failed to invoke method addAccount in class " +
                        "org.exist.xmlrpc.RpcConnection: Account '%s' exist", userName);
        return e.getMessage().equals(existingAccountErrorMessage);
    }

    /**
     * Checks if a user is initialized (an eXist account with the same name is existing).
     *
     * @param idUser The id of the user to test.
     * @return {@code True} if the user is initialized.
     */
    boolean isUserInitialized(@NotNull IdUser idUser) {

        String userName = idUser.getIdentifier();

        try {
            RemoteUserManagementService service = getUserService();
            return Optional.ofNullable(service.getAccount(userName)).isPresent();
        } catch (XMLDBException e) {
            throw new MomcaException("Failed to get resource for user '" + userName + "'", e);
        }

    }

    @NotNull
    private List<String> listUserResourceNames() {

        List<String> users = new ArrayList<>();
        momcaConnection.getCollection(ResourceRoot.USER_DATA.getUri()).ifPresent(collection -> {

            String[] encodedUserNames;

            try {
                encodedUserNames = collection.listResources();
            } catch (XMLDBException e) {
                String message = String.format("Failed to list resources in collection '%s'.", ResourceRoot.USER_DATA.getUri());
                throw new MomcaException(message, e);
            }

            for (String encodedUserName : encodedUserNames) {
                users.add(Util.decode(encodedUserName));
            }

        });

        users.sort(Comparator.<String>naturalOrder());
        return users;

    }

    /**
     * @return A list of all registered users in the database.
     */
    @NotNull
    public List<IdUser> listUsers() {
        return listUserResourceNames().stream().map(s -> new IdUser(s.replace(".xml", ""))).collect(Collectors.toList());
    }

}
