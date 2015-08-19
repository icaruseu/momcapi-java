package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.Util;
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
public class IdMyCollection extends IdAbstract {

    public IdMyCollection(@NotNull String identifier) {
        super(initAtomId(identifier), identifier);
    }

    public IdMyCollection(@NotNull AtomId atomId) {

        super(atomId, initIdentifier(atomId));

        if (getAtomId().getType() != ResourceType.MY_COLLECTION) {
            throw new IllegalArgumentException(getAtomId().getText() + " is not a myCollection atom:id text.");
        }

    }

    @NotNull
    private static String initIdentifier(@NotNull AtomId atomId) {
        String[] idParts = atomId.getText().split("/");
        return Util.decode(idParts[idParts.length - 1]);
    }

    private static AtomId initAtomId(@NotNull String identifier) {

        if (identifier.contains("/")) {
            throw new IllegalArgumentException("The myCollection identifier '" + identifier + "' contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/", AtomId.DEFAULT_PREFIX, ResourceType.MY_COLLECTION.getNameInId(), identifier));

    }

}
