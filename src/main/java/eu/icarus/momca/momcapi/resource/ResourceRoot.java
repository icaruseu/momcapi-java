package eu.icarus.momca.momcapi.resource;

/**
 * A root collection used to store content in MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 27.06.2015.
 */
public enum ResourceRoot {

    METADATA_ANNOTATION("/db/mom-data/metadata.annotation"),
    METADATA_ARCHIVE_PUBLIC("/db/mom-data/metadata.archive.public"),
    METADATA_CHARTER_IMPORT("/db/mom-data/metadata.charter.import"),
    METADATA_CHARTER_IMPORT_UTIL("/db/mom-data/metadata.charter.import.util"),
    METADATA_CHARTER_PUBLIC("/db/mom-data/metadata.charter.public"),
    METADATA_CHARTER_SAVED("/db/mom-data/metadata.charter.saved"),
    METADATA_COLLECTION_PUBLIC("/db/mom-data/metadata.collection.public"),
    METADATA_FOND_PUBLIC("/db/mom-data/metadata.fond.public"),
    METADATA_IMAGE_COLLECTIONS("/db/mom-data/metadata.imagecollections"),
    METADATA_MY_COLLECTION_PUBLIC("/db/mom-data/metadata.mycollection.public"),
    METADATA_PORTAL_PUBLIC("/db/mom-data/metadata.portal.public"),
    XRX_HTDOC("/db/mom-data/xrx.htdoc"),
    XRX_I18N("/db/mom-data/xrx.i18n"),
    XRX_USER("/db/mom-data/xrx.user");

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
