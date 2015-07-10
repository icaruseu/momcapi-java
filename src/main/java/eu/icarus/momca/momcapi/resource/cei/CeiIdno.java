package eu.icarus.momca.momcapi.resource.cei;

import eu.icarus.momca.momcapi.resource.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 09.07.2015.
 */
public class CeiIdno extends Element {

    @NotNull
    private final String id;
    @NotNull
    private final String text;

    public CeiIdno(@NotNull String id, @NotNull String text) {

        super("cei:idno", Namespace.CEI.getUri());
        addAttribute(new Attribute("id", id));
        appendChild(text);

        this.text = text;
        this.id = id;

    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getText() {
        return text;
    }

    @NotNull
    @Override
    public String toString() {

        return "CeiIdno{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                "} " + super.toString();

    }

}
