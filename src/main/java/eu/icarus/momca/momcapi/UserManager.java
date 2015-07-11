package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.ExistResource;
import eu.icarus.momca.momcapi.resource.User;
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
 * Created by daniel on 03.07.2015.
 */
class UserManager {

    @NotNull
    private static final String NEW_USER_CONTENT = "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"> <xrx:username /> <xrx:password /> <xrx:firstname>%s</xrx:firstname> <xrx:name>%s</xrx:name> <xrx:email>%s</xrx:email> <xrx:moderator>%s</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>";
    @NotNull
    private static final String PATH_USER = "/db/mom-data/xrx.user";
    @NotNull
    private final MomcaConnection momcaConnection;

    UserManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    public User addUser(@NotNull String userName, @NotNull String password, @NotNull String moderatorName, @NotNull String firstName, @NotNull String lastName) {

        if (!getUser(userName).isPresent()) {

            String xmlContent = String.format(NEW_USER_CONTENT, firstName, lastName, userName, moderatorName);
            ExistResource userResource = new ExistResource(userName + ".xml", PATH_USER, xmlContent);

            momcaConnection.storeExistResource(userResource);

            initializeUser(new User(userResource), password);

        }

        return getUser(userName).get();

    }

    public User addUser(@NotNull String userName, @NotNull String password, @NotNull String moderatorName) {
        return addUser(userName, password, moderatorName, "New", "User");
    }

    @NotNull
    public User changeModerator(@NotNull User user, @NotNull User newModerator) {
        momcaConnection.queryDatabase(ExistQueryFactory.replaceFirstOccurrenceInResource(user.getUri(), "xrx:moderator", String.format("<xrx:moderator>%s</xrx:moderator>", newModerator.getUserName())));
        return getUser(user.getUserName()).get();
    }

    public void changeUserPassword(@NotNull User user, @NotNull String newPassword) {

        String userName = user.getUserName();

        try {

            RemoteUserManagementService service = (RemoteUserManagementService) momcaConnection.getRootCollection().getService("UserManagementService", "1.0");
            Account account = service.getAccount(userName);
            if (account != null) {
                account.setPassword(newPassword);
                service.updateAccount(account);
            }

        } catch (XMLDBException e) {
            throw new MomcaException("Failed to change the password for '" + userName + "'", e);
        }

    }

    public void deleteUser(@NotNull User user) {

        momcaConnection.deleteExistResource(user);

        String userName = user.getUserName();
        deleteExistUserAccount(userName);
        momcaConnection.deleteCollection(PATH_USER + "/" + userName);

    }

    @NotNull
    public Optional<User> getUser(@NotNull String userName) {
        boolean isInitialized = isUserInitialized(userName);
        return momcaConnection.getExistResource(userName + ".xml", PATH_USER).flatMap(existResource -> Optional.of(new User(existResource, isInitialized)));
    }

    public User initializeUser(@NotNull User uninitializedUser, @NotNull String password) {

        String userName = uninitializedUser.getUserName();

        if (!uninitializedUser.isInitialized()) {

            try {

                RemoteUserManagementService service = (RemoteUserManagementService) momcaConnection.getRootCollection().getService("UserManagementService", "1.0");

                Group atomGroup = new GroupAider("atom");
                service.addGroup(atomGroup);

                Account newAccount = new UserAider(userName, atomGroup);
                newAccount.setPassword(password);
                service.addAccount(newAccount);
                service.addAccountToGroup(userName, "guest");

            } catch (XMLDBException e) {
                if (!e.getMessage().equals(String.format("Failed to invoke method addAccount in class org.exist.xmlrpc.RpcConnection: Account '%s' exist", userName))) {
                    throw new MomcaException("Failed to create user '" + userName + "'", e);
                }
            }

        }

        return getUser(userName).get();

    }

    @NotNull
    public List<String> listUserNames() {
        return listUserResourceNames().stream().map(s -> s.replace(".xml", "")).collect(Collectors.toList());
    }

    void deleteExistUserAccount(@NotNull String accountName) {

        try {

            RemoteUserManagementService service = (RemoteUserManagementService) momcaConnection.getRootCollection().getService("UserManagementService", "1.0");
            Account account = service.getAccount(accountName);

            if (account != null) {
                service.removeAccount(account);
            }

        } catch (XMLDBException e) {
            throw new MomcaException("Failed to remove account '" + accountName + "'", e);
        }

    }

    boolean isUserInitialized(@NotNull String userName) {

        try {
            RemoteUserManagementService service = (RemoteUserManagementService) momcaConnection.getRootCollection().getService("UserManagementService", "1.0");
            return Optional.ofNullable(service.getAccount(userName)).isPresent();
        } catch (XMLDBException e) {
            throw new MomcaException("Failed to get resource for user '" + userName + "'", e);
        }

    }

    @NotNull
    private List<String> listUserResourceNames() {

        List<String> users = new ArrayList<>();
        momcaConnection.getCollection(PATH_USER).ifPresent(collection -> {

            String[] escapedUserNames;
            try {
                escapedUserNames = collection.listResources();
            } catch (XMLDBException e) {
                throw new MomcaException(String.format("Failed to list resources in collection '%s'.", PATH_USER), e);
            }

            for (String escapedUserName : escapedUserNames) {
                users.add(Util.decode(escapedUserName));
            }

        });

        users.sort(Comparator.<String>naturalOrder());
        return users;

    }

}
