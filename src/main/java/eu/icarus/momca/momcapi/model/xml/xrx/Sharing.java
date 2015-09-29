package eu.icarus.momca.momcapi.model.xml.xrx;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by djell on 29/09/2015.
 */
public class Sharing extends Element {

    public static final String URI = Namespace.XRX.getUri();

    @NotNull
    private final String content;

    @NotNull
    private final String user;

    public Sharing(@NotNull String content, @Nullable String user) {

        super("xrx:sharing", Namespace.XRX.getUri());

        Element visibility = new Element("xrx:visibility", URI);
        visibility.appendChild(content);
        appendChild(visibility);

        Element userElement = new Element("xrx:user", URI);
        if (user != null && !user.isEmpty()) {
            userElement.appendChild(user);
        }
        appendChild(userElement);

        this.content = content;
        this.user = (user == null) ? "" : user;

    }

    @NotNull
    public String getContent() {
        return content;
    }

    @NotNull
    public String getUser() {
        return user;
    }

}
