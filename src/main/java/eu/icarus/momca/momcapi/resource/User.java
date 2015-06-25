package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.id.CharterId;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 24.06.2015.
 */
public class User extends ExistResource {

    private static final AbstractMap.SimpleEntry<String, String> NAMESPACE_XRX = new AbstractMap.SimpleEntry<>("xrx", "http://www.monasterium.net/NS/xrx");

    public User(@NotNull ExistResource existResource) {
        super(existResource);
    }

    @NotNull
    public List<CharterId> listBookmarkedCharterIds() {
        return parseToCharterIds(queryContent("//xrx:bookmark/text()", NAMESPACE_XRX));
    }

    @NotNull
    public List<CharterId> listSavedCharterIds() {
        return parseToCharterIds(queryContent("//xrx:saved/xrx:id/text()", NAMESPACE_XRX));
    }

    @NotNull
    private List<CharterId> parseToCharterIds(@NotNull List<String> idStrings) {
        List<CharterId> ids = new ArrayList<>();
        idStrings.forEach(s -> ids.add(new CharterId(s)));
        return ids;
    }

}
