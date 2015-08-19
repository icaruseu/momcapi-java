package eu.icarus.momca.momcapi.model;

import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 18/08/2015.
 */
public abstract class IdAbstract {

    @NotNull
    final Element contentXml;
    @NotNull
    private final String identifier;

    public IdAbstract(@NotNull Element contentXml, @NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.contentXml = contentXml;
        this.identifier = identifier;

    }

    @NotNull
    public abstract Element getContentXml();

    @NotNull
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdAbstract that = (IdAbstract) o;

        return identifier.equals(that.identifier);

    }

}
