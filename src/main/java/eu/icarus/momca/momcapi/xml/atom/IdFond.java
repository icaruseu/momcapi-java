package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the {@code atom:id} of a fond in MOM-CA, e.g. {@code tag:www.monasterium.net,2011:/fond/RS-IAGNS/Charters}.
 *
 * @author Daniel Jeller
 *         Created on 21.07.2015.
 */
public class IdFond extends IdAbstract {

    @NotNull
    private final IdArchive idArchive;

    public IdFond(@NotNull AtomId atomId) {

        super(atomId, initIdentifier(atomId));

        if (getAtomId().getType() != ResourceType.FOND) {
            throw new IllegalArgumentException(getAtomId().getText() + " is not a fond atom:id text.");
        }

        idArchive = getArchiveFromAtomId(atomId);

    }

    public IdFond(@NotNull String archiveIdentifier, @NotNull String fondIdentifier) {
        super(initAtomId(archiveIdentifier, fondIdentifier), fondIdentifier);
        idArchive = new IdArchive(archiveIdentifier);
    }

    @NotNull
    private static String initIdentifier(@NotNull AtomId atomId) {
        String[] idParts = atomId.getText().split("/");
        return Util.decode(idParts[idParts.length - 1]);
    }

    private static AtomId initAtomId(@NotNull String archiveIdentifier, @NotNull String fondIdentifier) {

        if (archiveIdentifier.contains("/") || fondIdentifier.contains("/")) {
            throw new IllegalArgumentException("One of the identifiers contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/",
                AtomId.DEFAULT_PREFIX,
                ResourceType.FOND.getNameInId(),
                archiveIdentifier,
                fondIdentifier));

    }

    @NotNull
    private IdArchive getArchiveFromAtomId(@NotNull AtomId atomId) {
        String[] parts = atomId.getText().split("/");
        return new IdArchive(Util.decode(parts[parts.length - 2]));
    }

    @NotNull
    public IdArchive getIdArchive() {
        return idArchive;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        IdFond idFond = (IdFond) o;

        return idArchive.equals(idFond.idArchive);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + idArchive.hashCode();
        return result;
    }

}
