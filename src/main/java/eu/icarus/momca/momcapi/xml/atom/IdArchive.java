package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the {@code atom:id} of an archive in MOM-CA, e.g. {@code tag:www.monasterium.net,2011:/archive/CH-KAE}.
 *
 * @author Daniel Jeller
 *         Created on 20.07.2015.
 */
public class IdArchive extends Id {

    private static final int VALID_ID_PARTS = 3;
    @NotNull
    private final String archiveIdentifier;

    /**
     * Instantiates a new archive-atom:id.
     *
     * @param archiveIdentifier The identifier to use. Can be either just the short name of the archive,
     *                          e.g. {@code CH-KAE} or a full archive-atom:id, e.g.
     *                          {@code tag:www.monasterium.net,2011:/archive/CH-KAE}
     */
    public IdArchive(@NotNull String archiveIdentifier) {
        super(archiveIdentifier.split("/").length == VALID_ID_PARTS
                ? archiveIdentifier : String.format("%s/%s", ResourceType.ARCHIVE.getAtomIdPart(), archiveIdentifier));

        String[] idParts = archiveIdentifier.split("/");

        if (!isArchiveId(idParts)) {
            throw new IllegalArgumentException("Number of id parts not correct for an archive atom-id.");
        }

        this.archiveIdentifier = archiveIdentifier.split("/")[archiveIdentifier.split("/").length - 1];

    }

    private boolean isArchiveId(String[] idParts) {
        return idParts.length == 1 || idParts.length == VALID_ID_PARTS;
    }

    /**
     * @return The identifier of the archive, e.g. {@code CH-KAE}.
     */
    @NotNull
    public String getArchiveIdentifier() {
        return archiveIdentifier;
    }

}
