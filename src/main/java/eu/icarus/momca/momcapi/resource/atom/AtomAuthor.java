package eu.icarus.momca.momcapi.resource.atom;

import eu.icarus.momca.momcapi.resource.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 09.07.2015.
 */
public class AtomAuthor extends Element {

    @NotNull
    private final String email;

    public AtomAuthor(@NotNull String email) {

        super("atom:author", Namespace.ATOM.getUri());
        Element atomEmail = new Element("atom:email", Namespace.ATOM.getUri());
        atomEmail.appendChild(email);
        appendChild(atomEmail);

        this.email = email;

    }

    @NotNull
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "AtomAuthor{" +
                "email='" + email + '\'' +
                "} " + super.toString();
    }

}
