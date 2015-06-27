package eu.icarus.momca.momcapi.resource;

/**
 * Created by daniel on 25.06.2015.
 */
public enum ResourceType {

    ARCHIVE, CHARTER, COLLECTION, FOND;

    public String getValue() {
        return this.name().toLowerCase();
    }

}
