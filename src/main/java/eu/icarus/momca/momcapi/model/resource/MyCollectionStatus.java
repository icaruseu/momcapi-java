package eu.icarus.momca.momcapi.model.resource;

/**
 * Created by djell on 29/09/2015.
 */
public enum MyCollectionStatus {

    PRIVATE(ResourceRoot.USER_DATA), PUBLISHED(ResourceRoot.PUBLISHED_USER_COLLECTIONS);

    private final ResourceRoot resourceRoot;

    MyCollectionStatus(ResourceRoot resourceRoot) {
        this.resourceRoot = resourceRoot;
    }

    public ResourceRoot getResourceRoot() {
        return resourceRoot;
    }

}
