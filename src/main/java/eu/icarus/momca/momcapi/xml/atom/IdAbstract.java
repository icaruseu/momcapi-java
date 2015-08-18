package eu.icarus.momca.momcapi.xml.atom;

import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 18/08/2015.
 */
public abstract class IdAbstract {

    @NotNull
    private final AtomId atomId;
    @NotNull
    private final String identifier;

    public IdAbstract(@NotNull AtomId atomId, @NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.atomId = atomId;
        this.identifier = identifier;

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdAbstract that = (IdAbstract) o;

        return identifier.equals(that.identifier);

    }

    @NotNull
    public AtomId getAtomId() {
        return atomId;
    }

    @NotNull
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

}
