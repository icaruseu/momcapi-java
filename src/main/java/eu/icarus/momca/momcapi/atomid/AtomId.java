package eu.icarus.momca.momcapi.atomid;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.resource.Namespace;
import eu.icarus.momca.momcapi.resource.ResourceType;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by daniel on 25.06.2015.
 */
class AtomId {

    @NotNull
    private static final String DEFAULT_PREFIX = "tag:www.monasterium.net,2011:";
    @NotNull
    private final String atomId;
    @NotNull
    private final String prefix;
    @NotNull
    private final ResourceType type;

    AtomId(@NotNull String atomId) {
        String[] valueTokens = atomId.split("/");
        this.atomId = atomId;
        prefix = valueTokens[0];
        type = ResourceType.createFromValue(valueTokens[1]);
    }

    AtomId(@NotNull String... idParts) {

        if (idParts.length >= 3 && idParts.length <= 4) {

            prefix = DEFAULT_PREFIX;
            type = ResourceType.createFromValue(idParts[0]);

            StringBuilder idBuilder = new StringBuilder(DEFAULT_PREFIX);
            for (String idPart : idParts) {
                try {
                    idBuilder.append("/");
                    idBuilder.append(Util.encode(idPart));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            this.atomId = idBuilder.toString();

        } else {
            throw new IllegalArgumentException("'" + Arrays.asList(idParts) + "' has not the right amount of parts; probably not a valid atom:id");
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AtomId atomId1 = (AtomId) o;

        return atomId.equals(atomId1.atomId);

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

    @Override
    public int hashCode() {
        return atomId.hashCode();
    }
}
