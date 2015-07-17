package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * A representation of the {@code atom:autor} XML element as defined by the
 * <a href="http://atomenabled.org/developers/syndication/#recommendedFeedElements">Atom developer guidelines</a>.
 * It uses only the {@code atom:email} child element.<br/>
 * <br/>
 * Example in XML:<br/>
 * {@code <atom:author><atom:email>author@example.com</atom:email></atom:author>}
 *
 * @author Daniel Jeller
 *         Created on 09.07.2015.
 */
public class Author extends Element {

    @NotNull
    private final String email;

    /**
     * Instantiates a new Author.
     *
     * @param email The author's email address used as identification.
     */
    public Author(@NotNull String email) {

        super("atom:author", Namespace.ATOM.getUri());
        Element atomEmail = new Element("atom:email", Namespace.ATOM.getUri());
        atomEmail.appendChild(email);
        appendChild(atomEmail);

        this.email = email;

    }

    /**
     * @return The author's email.
     */
    @NotNull
    public String getEmail() {
        return email;
    }

    @NotNull
    @Override
    public String toString() {
        return "Author{" +
                "email='" + email + '\'' +
                "} " + super.toString();
    }

}
