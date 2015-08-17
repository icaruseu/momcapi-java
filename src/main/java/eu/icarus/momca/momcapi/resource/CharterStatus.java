package eu.icarus.momca.momcapi.resource;

/**
 * The status of a charter and the name of the database root collection associated with the status.
 *
 * @author Daniel Jeller
 *         Created on 29.06.2015.
 */
public enum CharterStatus {

    IMPORTED(ResourceRoot.IMPORTED_ARCHIVAL_CHARTERS),
    PRIVATE(ResourceRoot.USER_DATA),
    PUBLIC(ResourceRoot.PUBLIC_CHARTERS),
    SAVED(ResourceRoot.ARCHIVAL_CHARTERS_BEING_EDITED);

    private final ResourceRoot resourceRoot;

    CharterStatus(ResourceRoot resourceRoot) {
        this.resourceRoot = resourceRoot;
    }

    /**
     * @return The ResourceRoot associated with the charter status, e.g. {@code ARCHIVAL_CHARTERS_BEING_EDITED}
     * for a {@code SAVED} charter.
     */
    public ResourceRoot getResourceRoot() {
        return resourceRoot;
    }

}
