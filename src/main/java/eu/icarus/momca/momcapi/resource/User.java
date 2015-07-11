package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.AtomIdCharter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 24.06.2015.
 */
public class User extends ExistResource {

    private final boolean isInitialized;
    @NotNull
    private final String moderator;
    @NotNull
    private final String userName;

    public User(@NotNull ExistResource existResource) {
        this(existResource, false);
    }

    public User(@NotNull ExistResource existResource, boolean isInitialized) {
        super(existResource);
        userName = queryUniqueFieldValue(XpathQuery.QUERY_XRX_EMAIL);
        moderator = queryUniqueFieldValue(XpathQuery.QUERY_XRX_MODERATOR);
        this.isInitialized = isInitialized;
    }

    @NotNull
    public String getModeratorName() {
        return moderator;
    }

    @NotNull
    public String getUserName() {
        return userName;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    @NotNull
    public List<AtomIdCharter> listBookmarkedCharterIds() {
        return parseToCharterIds(listQueryResultStrings(XpathQuery.QUERY_XRX_BOOKMARK));
    }

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
