package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the {@code atom:id} of a collection in MOM-CA, e.g.
 * {@code tag:www.monasterium.net,2011:/collection/MedDocBulgEmp}. This is not the id of an user defined collection
 * but of an external collection imported by a metadata manager.
 *
 * @author Daniel Jeller
 *         Created on 21.07.2015.
 * @see IdMyCollection
 */
public class IdCollection extends Id {

    private static final int VALID_ID_PARTS = 3;
    @NotNull
    private final String collectionIdentifier;

    /**
     * Instantiates a new collection id.
     *
     * @param collectionIdentifier The identifier to use. Can be either just the identifier of the collection,
     *                             e.g. {@code MedDocBulgEmp} or a full collection atom:id, e.g.
     *                             {@code tag:www.monasterium.net,2011:/collection/MedDocBulgEmp}
     */
    public IdCollection(@NotNull String collectionIdentifier) {

        super((collectionIdentifier.split("/").length == VALID_ID_PARTS)
                ? collectionIdentifier
                : String.format("%s/%s", ResourceType.COLLECTION.getNameInId(), collectionIdentifier));


        if (isId(collectionIdentifier) && !isCollectionId(collectionIdentifier)) {
            String message = String.format("'%s' is not a valid collection atom:id.", collectionIdentifier);
            throw new IllegalArgumentException(message);
        }

        String[] idParts = collectionIdentifier.split("/");
        this.collectionIdentifier = idParts[idParts.length - 1];

    }

    private boolean isCollectionId(@NotNull String collectionId) {
        String[] idParts = collectionId.split("/");
        return getType() == ResourceType.COLLECTION && idParts.length == VALID_ID_PARTS;
    }

    /**
     * @return The identifier of the collection, e.g. {@code MedDocBulgEmp}.
     */
    @NotNull
    public String getCollectionIdentifier() {
        return collectionIdentifier;
    }

    private boolean isId(String collectionIdentifier) {
        return collectionIdentifier.contains("/");
    }

    @Override
    public String toString() {
        return "IdCollection{" +
                "collectionIdentifier='" + collectionIdentifier + '\'' +
                "} " + super.toString();
    }

}
