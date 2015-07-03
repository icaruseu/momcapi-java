package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomCAException;
import eu.icarus.momca.momcapi.resource.ExistResource;
import eu.icarus.momca.momcapi.resource.User;
import nu.xom.ParsingException;
import org.exist.security.Account;
import org.exist.security.Group;
import org.exist.security.internal.aider.GroupAider;
import org.exist.security.internal.aider.UserAider;
import org.exist.xmldb.RemoteUserManagementService;
import org.jetbrains.annotations.NotNull;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    private MomcaConnection momcaConnection;

    public UserManager(@NotNull MomcaConnection momcaConnection) {
        this.momcaConnection = momcaConnection;
    }

    public void addUser(@NotNull String userName, @NotNull String password, @NotNull String moderatorName) throws MomCAException {

        if (!getUser(userName).isPresent()) {

            String xmlContent = String.format(NEW_USER_CONTENT, "New", "User", userName, moderatorName);
            ExistResource userResource;
            try {
                userResource = new ExistResource(userName + ".xml", PATH_USER, xmlContent);
            } catch (ParsingException | IOException e) {
                throw new MomCAException("Failed to create new user resource.", e);
            }
            momcaConnection.storeExistResource(userResource);

            initializeUser(userName, password);

        }

    }

    public void changeUserPassword(@NotNull String userName, @NotNull String newPassword) throws MomCAException {

        try {

            RemoteUserManagementService service = (RemoteUserManagementService) momcaConnection.getRootCollection().getService("UserManagementService", "1.0");
            Account account = service.getAccount(userName);
            if (account != null) {
                account.setPassword(newPassword);
                service.updateAccount(account);
            }

        } catch (XMLDBException e) {
            throw new MomCAException("Failed to change the password for '" + userName + "'", e);
        }

    }

    public void deleteExistUserAccount(@NotNull String userName) throws MomCAException {

        try {

            RemoteUserManagementService service = (RemoteUserManagementService) momcaConnection.getRootCollection().getService("UserManagementService", "1.0");
            Account account = service.getAccount(userName);

            if (account != null) {
                service.removeAccount(account);
            }

        } catch (XMLDBException e) {
            throw new MomCAException("Failed to remove account '" + userName + "'", e);
        }

    }

    public void deleteUser(@NotNull String userName) throws MomCAException {

        Optional<User> userOptional = getUser(userName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            momcaConnection.deleteExistResource(user);
        }

        deleteExistUserAccount(userName);

        momcaConnection.deleteCollection(PATH_USER + "/" + userName);

    }

    @NotNull
    public Optional<User> getUser(@NotNull String userName) throws MomCAException {
        return momcaConnection.getExistResource(userName + ".xml", PATH_USER).flatMap(existResource -> Optional.of(new User(existResource)));
    }

    public void initializeUser(String userName, String password) throws MomCAException {

        String atom = "atom";

        try {

            RemoteUserManagementService service = (RemoteUserManagementService) momcaConnection.getRootCollection().getService("UserManagementService", "1.0");

            Group atomGroup = new GroupAider(atom);
            service.addGroup(atomGroup);

            Account newAccount = new UserAider(userName, atomGroup);
            newAccount.setPassword(password);
            service.addAccount(newAccount);
            service.addAccountToGroup(userName, "guest");

        } catch (XMLDBException e) {
            if (!e.getMessage().equals(String.format("Failed to invoke method addAccount in class org.exist.xmlrpc.RpcConnection: Account '%s' exist", userName))) {
                throw new MomCAException("Failed to create user '" + userName + "'", e);
            }
        }

    }

    public boolean isUserInitialized(@NotNull String userName) throws MomCAException {

        try {
            RemoteUserManagementService service = (RemoteUserManagementService) momcaConnection.getRootCollection().getService("UserManagementService", "1.0");
            return Optional.ofNullable(service.getAccount(userName)).isPresent();
        } catch (XMLDBException e) {
            throw new MomCAException("Failed to get resource for user '" + userName + "'", e);
        }

    }

    @NotNull
    public List<String> listUninitializedUserNames() throws MomCAException {

        List<String> uninitializedUsers = new ArrayList<>(0);

        for (String user : listUserNames()) {
            if (!isUserInitialized(user)) {
                uninitializedUsers.add(user);
            }
        }

        return uninitializedUsers;

    }

    @NotNull
    public List<String> listUserNames() throws MomCAException {
        return listUserResourceNames().stream().map(s -> s.replace(".xml", "")).collect(Collectors.toList());
    }

    @NotNull
    private List<String> listUserResourceNames() throws MomCAException {

        Optional<Collection> userCollection = momcaConnection.getCollection(PATH_USER);

        List<String> users = new ArrayList<>();
        if (userCollection.isPresent()) {

            String[] escapedUserNames;
            try {
                escapedUserNames = userCollection.get().listResources();
            } catch (XMLDBException e) {
                throw new MomCAException(String.format("Failed to list resources in collection '%s'.", PATH_USER), e);
            }

            for (String escapedUserName : escapedUserNames) {
                try {
                    users.add(URLDecoder.decode(escapedUserName, MomcaConnection.URL_ENCODING));
                } catch (UnsupportedEncodingException e) {
                    throw new MomCAException(String.format("URL-Encoding '%s' not supported.", MomcaConnection.URL_ENCODING), e);
                }
            }

        }

        users.sort(Comparator.<String>naturalOrder());
        return users;

    }

}
