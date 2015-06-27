package eu.icarus.momca.momcapi.id;

import eu.icarus.momca.momcapi.resource.ResourceType;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 25.06.2015.
 */
public class AtomId {

    static final String DEFAULT_PREFIX = "tag:www.monasterium.net,2011:";
    @NotNull
    private final String atomId;
    @NotNull
    private final String prefix;
    @NotNull
    private final ResourceType type;

    AtomId(@NotNull String atomId) {
        this.atomId = atomId;
        String[] valueTokens = atomId.split("/");
        prefix = valueTokens[0];
        type = ResourceType.valueOf(valueTokens[1].toUpperCase());
    }

    @NotNull
    public String getAtomId() {
        return atomId;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    @NotNull
    public ResourceType getType() {
        return type;
    }

    public Element getXml() {

        String qualifiedName = String.format("%s:id", eu.icarus.momca.momcapi.Namespace.ATOM.getPrefix());
        String namespaceUri = eu.icarus.momca.momcapi.Namespace.ATOM.getUri();
        Element xml = new Element(qualifiedName, namespaceUri);
        xml.appendChild(atomId);
        return xml;

    }

}
