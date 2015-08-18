package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the {@code atom:id} of an archive in MOM-CA, e.g. {@code tag:www.monasterium.net,2011:/archive/CH-KAE}.
 *
 * @author Daniel Jeller
 *         Created on 20.07.2015.
 */
public class IdArchive extends IdAbstract {

    public IdArchive(@NotNull String identifier) {
        super(initAtomId(identifier), identifier);
    }

    public IdArchive(@NotNull AtomId atomId) {

        super(atomId, initIdentifier(atomId));

        if(getAtomId().getType() != ResourceType.ARCHIVE) {
            throw new IllegalArgumentException(getAtomId().getText() + " is not a archive atom:id text.");
        }

    }

    @NotNull
    private static String initIdentifier(@NotNull AtomId atomId) {
        String[] idParts = atomId.getText().split("/");
        return idParts[idParts.length - 1];
    }

    private static AtomId initAtomId(@NotNull String identifier) {

        if (identifier.contains("/")) {
            throw new IllegalArgumentException("The archive identifier '" + identifier + "' contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/", AtomId.DEFAULT_PREFIX, ResourceType.ARCHIVE.getNameInId(), identifier));

    }



}
