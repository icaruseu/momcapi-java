package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 24.06.2015.
 */
public class User extends ExistResource {

    private static final AbstractMap.SimpleEntry<String, String> NAMESPACE_XRX = new AbstractMap.SimpleEntry<>("xrx", "http://www.monasterium.net/NS/xrx");

    private final String userId;

    public User(@NotNull ExistResource existResource) {
        super(existResource);
        userId = existResource.getResourceName().replace(".xml", "");
    }

    public String getUserId() {
        return userId;
    }

    @NotNull
    public List<CharterAtomId> listBookmarkedCharterIds() {
        return parseToCharterIds(queryContent("//xrx:bookmark/text()", NAMESPACE_XRX));
    }

    @NotNull
    public List<CharterAtomId> listSavedCharterIds() {
        return parseToCharterIds(queryContent("//xrx:saved/xrx:id/text()", NAMESPACE_XRX));
    }

    @NotNull
    private List<CharterAtomId> parseToCharterIds(@NotNull List<String> idStrings) {
        List<CharterAtomId> ids = new ArrayList<>();
        idStrings.forEach(s -> ids.add(new CharterAtomId(s)));
        return ids;
    }

}
