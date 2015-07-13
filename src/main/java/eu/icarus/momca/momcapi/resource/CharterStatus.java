package eu.icarus.momca.momcapi.resource;

/**
 * The status of a charter and the name of the database root collection associated with the status.
 *
 * @author Daniel Jeller
 *         Created on 29.06.2015.
 */
public enum CharterStatus {

    /**
     * An imported charter.
     */
    IMPORTED("metadata.charter.import"),

    /**
     * A charter created by a user.
     */
    PRIVATE("xrx.user"),

    /**
     * A publicly visible charter.
     */
    PUBLIC("metadata.charter.public"),

    /**
     * A saved charter.
     */
    SAVED("metadata.charter.saved");

    private final String parentCollection;

    CharterStatus(String parentCollection) {
        this.parentCollection = parentCollection;
    }

    /**
     * @return The name of the root collection associated with the charter status, e.g. {@code metadata.charter.saved} for a {@code SAVED} charter.
     */
    public String getRootCollection() {
        return parentCollection;
    }

}
