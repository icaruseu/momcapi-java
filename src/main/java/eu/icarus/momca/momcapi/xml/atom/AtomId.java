package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.resource.ResourceType;
import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created by daniel on 25.06.2015.
 */
public class AtomId extends Element {

    @NotNull
    private static final String DEFAULT_PREFIX = "tag:www.monasterium.net,2011:";
    @NotNull
    private final String atomId;
    @NotNull
    private final String prefix;
    @NotNull
    private final ResourceType type;

    AtomId(@NotNull String atomId) {
        this(atomId.replace(DEFAULT_PREFIX + "/", "").split("/"));
    }

    AtomId(@NotNull String... idParts) {

        super("atom:id", Namespace.ATOM.getUri());

        if (idParts.length >= 3 && idParts.length <= 4) {

            prefix = DEFAULT_PREFIX;
            type = ResourceType.createFromValue(idParts[0]);

            StringBuilder idBuilder = new StringBuilder(DEFAULT_PREFIX);
            for (String idPart : idParts) {
                idBuilder.append("/");
                idBuilder.append(Util.encode(idPart));
            }
            this.atomId = idBuilder.toString();
            appendChild(this.atomId);

        } else {
            throw new IllegalArgumentException("'" + Arrays.asList(idParts) + "' has not the right amount of parts; probably not a valid atom:id");
        }

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

    @NotNull
    public Element getXml() {

        String qualifiedName = String.format("%s:id", Namespace.ATOM.getPrefix());
        String namespaceUri = Namespace.ATOM.getUri();
        Element xml = new Element(qualifiedName, namespaceUri);
        xml.appendChild(atomId);
        return xml;

    }

}
