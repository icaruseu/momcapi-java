package eu.icarus.momca.momcapi.model.id;

import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 18/08/2015.
 */
public abstract class IdAbstract {

    @NotNull
    final String identifier;
    @NotNull
    Element contentXml;

    public IdAbstract(@NotNull Element contentXml, @NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.contentXml = contentXml;
        this.identifier = identifier;

    }

    @Override
    public abstract boolean equals(Object o);

    @NotNull
    public abstract Element getContentAsElement();

    @NotNull
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public abstract int hashCode();

    @Override
    public String toString() {
        return "IdAbstract{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
