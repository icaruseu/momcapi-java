package eu.icarus.momca.momcapi.resource;

/**
 * A root collection used to store content in MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 27.06.2015.
 */
public enum ResourceRoot {

    ANNOTATIONS("/db/mom-data/metadata.annotation"),
    ARCHIVAL_CHARTERS_BEING_EDITED("/db/mom-data/metadata.charter.saved"),
    ARCHIVAL_COLLECTIONS("/db/mom-data/metadata.collection.public"),
    ARCHIVAL_FONDS("/db/mom-data/metadata.fond.public"),
    ARCHIVES("/db/mom-data/metadata.archive.public"),
    HTDOC("/db/mom-data/xrx.htdoc"),
    I18N("/db/mom-data/xrx.i18n"),
    IMAGE_COLLECTIONS("/db/mom-data/metadata.imagecollections"),
    IMPORTED_ARCHIVAL_CHARTERS("/db/mom-data/metadata.charter.import"),
    PORTAL_HIERARCHY("/db/mom-data/metadata.portal.public"),
    PUBLIC_CHARTERS("/db/mom-data/metadata.charter.public"),
    PUBLISHED_USER_COLLECTIONS("/db/mom-data/metadata.mycollection.public"),
    SAVED_IMPORT_SOURCE_FILES("/db/mom-data/metadata.charter.import.util"),
    USER_DATA("/db/mom-data/xrx.user");

    private final String uri;

    ResourceRoot(String uri) {
        this.uri = uri;
    }

    /**
     * @return The name of the root collection in the database, e.g. {@code metadata.charter.public}.
     */
    public String getUri() {
        return uri;
    }

}
