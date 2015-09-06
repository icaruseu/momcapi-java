package eu.icarus.momca.momcapi.model.xml.ead;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Attribute;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 06/09/2015.
 */
public class Did extends Element {

    private static final String EAD_URI = Namespace.EAD.getUri();
    @NotNull
    private final String identifier;
    @NotNull
    private final String name;

    public Did(@NotNull String identifier, @NotNull String name) {

        super("ead:did", EAD_URI);

        if (identifier.isEmpty() || name.isEmpty()) {
            throw new IllegalArgumentException("Identifier and name are not allowed to be empty strings.");
        }

        this.identifier = identifier;
        this.name = name;

        Element unitid = new Element("ead:unitid", EAD_URI);
        unitid.addAttribute(new Attribute("identifier", identifier));
        unitid.appendChild(identifier);

        Element unittitle = new Element("ead:unittitle", EAD_URI);
        unittitle.appendChild(name);

        this.appendChild(unitid);
        this.appendChild(unittitle);

    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

}
