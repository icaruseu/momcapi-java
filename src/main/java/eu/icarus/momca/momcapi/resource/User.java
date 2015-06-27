package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.Namespace;
import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 24.06.2015.
 */
public class User extends ExistResource {

    private final String userId;

    public User(@NotNull ExistResource existResource) {
        super(existResource);
        userId = existResource.getName().replace(".xml", "");
    }

    public String getUserId() {
        return userId;
    }

    @NotNull
    public List<CharterAtomId> listBookmarkedCharterIds() {
        return parseToCharterIds(queryContentXml("//xrx:bookmark/text()", Namespace.XRX));
    }

    @NotNull
    public List<CharterAtomId> listSavedCharterIds() {
        return parseToCharterIds(queryContentXml("//xrx:saved/xrx:id/text()", Namespace.XRX));
    }

    @NotNull
    private List<CharterAtomId> parseToCharterIds(@NotNull List<String> idStrings) {
        List<CharterAtomId> ids = new ArrayList<>();
        idStrings.forEach(s -> ids.add(new CharterAtomId(s)));
        return ids;
    }

}
