package eu.icarus.momca.momcapi.atomid;

import eu.icarus.momca.momcapi.resource.ResourceType;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

        String[] valueTokens = atomId.split("/");

        prefix = valueTokens[0];
        type = ResourceType.createFromValue(valueTokens[1]);

        List<String> cleanTokens = new ArrayList<>(0);
        for (int i = 0; i < valueTokens.length; i++) {

            if (i <= 1) {
                cleanTokens.add(valueTokens[i]);
            } else {
                try {
                    cleanTokens.add(URLEncoder.encode(valueTokens[i], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        this.atomId = String.join("/", cleanTokens);

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

        String qualifiedName = String.format("%s:id", eu.icarus.momca.momcapi.Namespace.ATOM.getPrefix());
        String namespaceUri = eu.icarus.momca.momcapi.Namespace.ATOM.getUri();
        Element xml = new Element(qualifiedName, namespaceUri);
        xml.appendChild(atomId);
        return xml;

    }

    @Override
    public int hashCode() {
        return atomId.hashCode();
    }
}
