package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.resource.ResourceType;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
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
public class IdCollection extends IdAtomId {

    public IdCollection(@NotNull String identifier) {
        super(initAtomId(identifier), identifier);
    }

    public IdCollection(@NotNull AtomId atomId) {

        super(atomId, initIdentifier(atomId));

        if (getContentXml().getType() != ResourceType.COLLECTION) {
            throw new IllegalArgumentException(getContentXml().getText() + " is not a collection atom:id text.");
        }

    }

    private static AtomId initAtomId(@NotNull String identifier) {

        if (identifier.contains("/")) {
            throw new IllegalArgumentException("The collection identifier '" + identifier + "' contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/", AtomId.DEFAULT_PREFIX, ResourceType.COLLECTION.getNameInId(), identifier));

    }

    @NotNull
    private static String initIdentifier(@NotNull AtomId atomId) {
        String[] idParts = atomId.getText().split("/");
        return Util.decode(idParts[idParts.length - 1]);
    }

}
