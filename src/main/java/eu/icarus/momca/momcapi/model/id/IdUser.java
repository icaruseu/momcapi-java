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

    @NotNull
    @Override
    public AtomAuthor getContentXml() {
        return (AtomAuthor) contentXml;
    }

}
