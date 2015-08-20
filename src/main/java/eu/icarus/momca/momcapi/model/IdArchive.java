package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
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

        if (getContentXml().getType() != ResourceType.ARCHIVE) {
            throw new IllegalArgumentException(getContentXml().getText() + " is not a archive atom:id text.");
        }

    }

    private static AtomId initAtomId(@NotNull String identifier) {

        if (identifier.contains("/")) {
            throw new IllegalArgumentException("The archive identifier '" + identifier + "' contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/", AtomId.DEFAULT_PREFIX, ResourceType.ARCHIVE.getNameInId(), identifier));

    }

    @NotNull
    private static String initIdentifier(@NotNull AtomId atomId) {
        String[] idParts = atomId.getText().split("/");
        return Util.decode(idParts[idParts.length - 1]);
    }

    @NotNull
    @Override
    public AtomId getContentXml() {
        return (AtomId) contentXml;
    }

}