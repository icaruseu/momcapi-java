package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.model.resource.ResourceType;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
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
public class IdMyCollection extends IdAtomId {

    public IdMyCollection(@NotNull String identifier) {
        super(initAtomId(identifier));
    }

    public IdMyCollection(@NotNull AtomId atomId) {

        super(atomId);

        if (getContentAsElement().getType() != ResourceType.MY_COLLECTION) {
            throw new IllegalArgumentException(getContentAsElement().getText() + " is not a myCollection atom:id text.");
        }

    }

    private static AtomId initAtomId(@NotNull String identifier) {

        if (identifier.contains("/")) {
            throw new IllegalArgumentException("The myCollection identifier '" + identifier + "' contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/", AtomId.DEFAULT_PREFIX, ResourceType.MY_COLLECTION.getNameInId(), identifier));

    }

    @Override
    public String toString() {
        return "IdMyCollection{" + getAtomId() + "}";
    }

}
