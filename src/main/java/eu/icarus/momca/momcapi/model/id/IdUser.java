package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.model.xml.atom.AtomAuthor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by djell on 19/08/2015.
 */
public class IdUser extends IdAbstract {

    public IdUser(@NotNull String identifier) {
        super(initAtomAuthor(identifier), identifier);
    }

    public IdUser(@NotNull AtomAuthor atomAuthor) {
        super(atomAuthor, initIdentifier(atomAuthor));
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdUser otherUser = (IdUser) o;

        return getIdentifier().equals(otherUser.getIdentifier());

    }

    @NotNull
    @Override
    public AtomAuthor getContentAsElement() {
        AtomAuthor author = (AtomAuthor) contentXml;
        author.detach();
        return author;
    }

    @Override
    public int hashCode() {
        return getIdentifier().hashCode();
    }

    private static AtomAuthor initAtomAuthor(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The user identifier is not allowed to be an empty string.");
        }

        return new AtomAuthor(identifier);

    }

    @NotNull
    private static String initIdentifier(@NotNull AtomAuthor atomAuthor) {
        return atomAuthor.getEmail();
    }

    @Override
    public String toString() {
        return "IdUser{" + identifier + "}";
    }

}
