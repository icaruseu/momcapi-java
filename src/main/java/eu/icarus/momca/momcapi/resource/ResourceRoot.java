package eu.icarus.momca.momcapi.resource;

/**
 * The root collections used to store content in MOM-CA.
 *
 * @author Daniel Jeller
 *         Created on 27.06.2015.
 */
public enum ResourceRoot {

    METADATA_ANNOTATION("metadata.annotation"),
    METADATA_ARCHIVE_PUBLIC("metadata.archive.public"),
    METADATA_CHARTER_IMPORT("metadata.charter.import"),
    METADATA_CHARTER_IMPORT_UTIL("metadata.charter.import.util"),
    METADATA_CHARTER_PUBLIC("metadata.charter.public"),
    METADATA_CHARTER_SAVED("metadata.charter.saved"),
    METADATA_COLLECTION_PUBLIC("metadata.collection.public"),
    METADATA_FOND_PUBLIC("metadata.fond.public"),
    METADATA_IMAGE_COLLECTIONS("metadata.imagecollections"),
    METADATA_MY_COLLECTION_PUBLIC("metadata.mycollection.public"),
    METADATA_PORTAL_PUBLIC("metadata.portal.public"),
    XRX_HTDOC("xrx.htdoc"),
    XRX_I18N(" xrx.i18n"),
    XRX_USER("xrx.user");

    private final String collectionName;

    ResourceRoot(String collectionName) {
        this.collectionName = collectionName;
    }

    /**
     * @return The name of the root collection in the database, e.g. {@code metadata.charter.public}.
     */
    public String getCollectionName() {
        return collectionName;
    }

}
