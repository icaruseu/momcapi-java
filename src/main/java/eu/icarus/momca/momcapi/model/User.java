package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a user in MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 24.06.2015.
 */
public class User extends MomcaResource {

    private final boolean isInitialized;
    @NotNull
    private final String moderator;
    @NotNull
    private final String userName;

    /**
     * Instantiates a new User.
     *
     * @param momcaResource The eXist Resource of the user in the database.
     */
    public User(@NotNull MomcaResource momcaResource) {
        this(momcaResource, false);
    }

    /**
     * Instantiates a new User.
     *
     * @param momcaResource The eXist Resource of the user in the database.
     * @param isInitialized Whether or not the user is already initialized in the database.
     */
    public User(@NotNull MomcaResource momcaResource, boolean isInitialized) {

        super(momcaResource);

        this.userName = queryUniqueFieldValue(XpathQuery.QUERY_XRX_EMAIL);
        this.moderator = queryUniqueFieldValue(XpathQuery.QUERY_XRX_MODERATOR);
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
    public List<IdCharter> listBookmarkedCharterIds() {
        return queryContentAsList(XpathQuery.QUERY_XRX_BOOKMARK).stream()
                .map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    /**
     * @return A list of the charters the user has saved.
     */
    @NotNull
    public List<IdCharter> listSavedCharterIds() {
        return queryContentAsList(XpathQuery.QUERY_XRX_SAVED_ID).stream()
                .map(AtomId::new).map(IdCharter::new).collect(Collectors.toList());
    }

    @NotNull
    private String queryUniqueFieldValue(@NotNull XpathQuery query) {

        List<String> queryResults = queryContentAsList(query);

        if (queryResults.size() == 1) {
            return queryResults.get(0);
        } else {
            throw new IllegalArgumentException("The XML content of the resource doesn't have an 'xrx:name' element. " +
                    "It's probably not a valid user resource.");
        }

    }

}
