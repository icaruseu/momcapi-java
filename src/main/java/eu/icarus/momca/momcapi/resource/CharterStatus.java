package eu.icarus.momca.momcapi.resource;

/**
 * The status of a charter and the name of the database root collection associated with the status.
 *
 * @author Daniel Jeller
 *         Created on 29.06.2015.
 */
public enum CharterStatus {

    IMPORTED(ResourceRoot.METADATA_CHARTER_IMPORT),
    PRIVATE(ResourceRoot.XRX_USER),
    PUBLIC(ResourceRoot.METADATA_CHARTER_PUBLIC),
    SAVED(ResourceRoot.METADATA_CHARTER_SAVED);

    private final ResourceRoot resourceRoot;

    CharterStatus(ResourceRoot resourceRoot) {
        this.resourceRoot = resourceRoot;
    }

    /**
     * @return The ResourceRoot associated with the charter status, e.g. {@code METADATA_CHARTER_SAVED}
     * for a {@code SAVED} charter.
     */
    public ResourceRoot getResourceRoot() {
        return resourceRoot;
    }

}
