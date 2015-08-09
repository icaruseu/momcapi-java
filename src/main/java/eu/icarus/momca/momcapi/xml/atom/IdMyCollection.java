package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the {@code atom:id} of a myCollection in MOM-CA, e.g.
 * {@code tag:www.monasterium.net,2011:/mycollection/67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3}. This is the id of an
 * user defined collection and not of an external collection imported by a metadata manager.
 *
 * @author Daniel Jeller
 *         Created on 21.07.2015.
 * @see IdCollection
 */
public class IdMyCollection extends Id {

    private static final int VALID_ID_PARTS = 3;
    @NotNull
    private final String myCollectionIdentifier;

    /**
     * Instantiates a new collection id.
     *
     * @param myCollectionIdentifier The identifier to use. Can be either just the identifier of the myCollection,
     *                               e.g. {@code 67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3} or a full myCollection atom:id, e.g.
     *                               {@code tag:www.monasterium.net,2011:/collection/67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3}
     */
    public IdMyCollection(@NotNull String myCollectionIdentifier) {

        super((myCollectionIdentifier.split("/").length == VALID_ID_PARTS)
                ? myCollectionIdentifier
                : String.format("%s/%s", ResourceType.MY_COLLECTION.getNameInId(), myCollectionIdentifier));

        if (isId(myCollectionIdentifier) && !isMyCollectionId(myCollectionIdentifier)) {
            String message = String.format("'%s' is not a valid myCollection atom:id.", myCollectionIdentifier);
            throw new IllegalArgumentException(message);
        }

        String[] idParts = myCollectionIdentifier.split("/");
        this.myCollectionIdentifier = idParts[idParts.length - 1];

    }

    /**
     * @return The identifier of the myCollection, e.g. {@code 67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3}.
     */
    @NotNull
    public String getMyCollectionIdentifier() {
        return myCollectionIdentifier;
    }

    private boolean isId(@NotNull String myCollectionIdentifier) {
        return myCollectionIdentifier.contains("/");
    }

    private boolean isMyCollectionId(@NotNull String myCollectionId) {
        String[] idParts = myCollectionId.split("/");
        return getType() == ResourceType.MY_COLLECTION && idParts.length == VALID_ID_PARTS;
    }

}
