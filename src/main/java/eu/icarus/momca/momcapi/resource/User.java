package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.AtomIdCharter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 24.06.2015.
 */
public class User extends ExistResource {

    private final boolean isInitialized;
    @NotNull
    private final String moderator;
    @NotNull
    private final String userName;

    /**
     * Instantiates a new User.
     *
     * @param existResource The eXist Resource of the user in the database.
     */
    public User(@NotNull ExistResource existResource) {
        this(existResource, false);
    }

    /**
     * Instantiates a new User.
     *
     * @param existResource The eXist Resource of the user in the database.
     * @param isInitialized Whether or not the user is already initialized in the database.
     */
    public User(@NotNull ExistResource existResource, boolean isInitialized) {
        super(existResource);
        userName = queryUniqueFieldValue(XpathQuery.QUERY_XRX_EMAIL);
        moderator = queryUniqueFieldValue(XpathQuery.QUERY_XRX_MODERATOR);
        this.isInitialized = isInitialized;
    }

    /**
     * @return The name of the user's moderator.
     */
    @NotNull
    public String getModeratorName() {
        return moderator;
    }

    /**
     * @return The name of the user.
     */
    @NotNull
    public String getUserName() {
        return userName;
    }

    /**
     * @return {@code True} if the user is already initialized.
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * @return A list of the charters the user has bookmarked.
     */
    @NotNull
    public List<AtomIdCharter> listBookmarkedCharterIds() {
        return parseToCharterIds(listQueryResultStrings(XpathQuery.QUERY_XRX_BOOKMARK));
    }

    /**
     * @return A list of the charters the user has saved.
     */
    @NotNull
    public List<AtomIdCharter> listSavedCharterIds() {
        return parseToCharterIds(listQueryResultStrings(XpathQuery.QUERY_XRX_SAVED_ID));
    }

    @NotNull
    private List<AtomIdCharter> parseToCharterIds(@NotNull List<String> idStrings) {
        List<AtomIdCharter> ids = new ArrayList<>();
        idStrings.forEach(s -> ids.add(new AtomIdCharter(s)));
        return ids;
    }

    @NotNull
    private String queryUniqueFieldValue(@NotNull XpathQuery query) {

        List<String> queryResults = listQueryResultStrings(query);
        if (queryResults.size() == 1) {
            return queryResults.get(0);
        } else {
            throw new IllegalArgumentException("The XML content of the resource doesn't have an xrx:name element. It's probably not a valid user resource.");
        }

    }

}
