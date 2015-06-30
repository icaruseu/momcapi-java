package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 24.06.2015.
 */
public class User extends ExistResource {

    private final String userName;

    public User(@NotNull ExistResource existResource) {

        super(existResource);
        List<String> nameQueryResults = queryContentXml(XpathQuery.QUERY_XRX_EMAIL);
        if (nameQueryResults.size() == 1) {
            userName = nameQueryResults.get(0);
        } else {
            throw new IllegalArgumentException("The XML content of the resource doesn't have an xrx:name element. It's probably not a valid user resource.");
        }

    }

    public String getUserName() {
        return userName;
    }

    @NotNull
    public List<CharterAtomId> listBookmarkedCharterIds() {
        return parseToCharterIds(queryContentXml(XpathQuery.QUERY_XRX_BOOKMARK));
    }

    @NotNull
    public List<CharterAtomId> listSavedCharterIds() {
        return parseToCharterIds(queryContentXml(XpathQuery.QUERY_XRX_SAVED));
    }

    @NotNull
    private List<CharterAtomId> parseToCharterIds(@NotNull List<String> idStrings) {
        List<CharterAtomId> ids = new ArrayList<>();
        idStrings.forEach(s -> ids.add(new CharterAtomId(s)));
        return ids;
    }

}
