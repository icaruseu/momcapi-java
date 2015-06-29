package eu.icarus.momca.momcapi.exist;

/**
 * Created by daniel on 27.06.2015.
 */
public enum MetadataCollectionName {

    METADATA_ANNOTATION("metadata.annotation"),
    METADATA_ARCHIVE_PUBLIC("metadata.archive.public"),
    METADATA_CHARTER_IMPORT("metadata.charter.import"),
    METADATA_CHARTER_IMPORT_UTIL("metadata.charter.import.util"),
    METADATA_CHARTER_PUBLIC("metadata.charter.public"),
    METADATA_CHARTER_SAVED("metadata.charter.saved"),
    METADATA_COLLECTION_PUBLIC("metadata.collection.public"),
    METADATA_FOND_PUBLIC("metadata.fond.public"),
    METADATA_IMAGECOLLECTIONS("metadata.imagecollections"),
    METADATA_MYCOLLECTION_PUBLIC("metadata.mycollection.public"),
    METADATA_PORTAL_PUBLIC("metadata.portal.public"),
    XRX_HTDOC("xrx.htdoc"),
    XRX_I18N(" xrx.i18n"),
    XRX_USER("xrx.user");

    private final String value;

    MetadataCollectionName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
