package eu.icarus.momca.momcapi.resource;

/**
 * Created by daniel on 29.06.2015.
 */
public enum CharterStatus {

    IMPORTED("metadata.charter.import"), PRIVATE("xrx.user"), PUBLIC("metadata.charter.public"), SAVED("metadata.charter.saved");

    private final String parentCollection;

    CharterStatus(String parentCollection) {
        this.parentCollection = parentCollection;
    }

    public String getParentCollection() {
        return parentCollection;
    }

}
