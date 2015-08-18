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
public class IdCollection extends IdAbstract {

    private static final int VALID_ID_PARTS = 3;

    public IdCollection(@NotNull String identifier) {
        super(initAtomId(identifier), identifier);
    }

    public IdCollection(@NotNull AtomId atomId) {
        super(atomId, initIdentifier(atomId));
    }

    @NotNull
    private static String initIdentifier(@NotNull AtomId atomId) {

        String[] idParts = atomId.getText().split("/");

        if (atomId.getType() != ResourceType.COLLECTION || idParts.length != VALID_ID_PARTS) {
            throw new IllegalArgumentException(atomId.getText() + " is not a valid collection id.");
        }

        return idParts[idParts.length - 1];

    }

    private static AtomId initAtomId(@NotNull String identifier) {

        if (identifier.contains("/")) {
            throw new IllegalArgumentException("The collection identifier '" + identifier + "' contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/", AtomId.DEFAULT_PREFIX, ResourceType.COLLECTION.getNameInId(), identifier));

    }

}
