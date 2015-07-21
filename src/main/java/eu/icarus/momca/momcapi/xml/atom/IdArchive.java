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

        if (isAtomId(archiveIdentifier) && !isArchiveAtomId(archiveIdentifier)) {

            String message = String.format("Number of id parts (%d) not correct for an archive atom-id.",
                    archiveIdentifier.split("/").length);
            throw new IllegalArgumentException(message);

        }

        String[] idParts = archiveIdentifier.split("/");
        this.archiveIdentifier = idParts[idParts.length - 1];

    }

    /**
     * @return The identifier of the archive, e.g. {@code CH-KAE}.
     */
    @NotNull
    public String getArchiveIdentifier() {
        return archiveIdentifier;
    }

    private boolean isArchiveAtomId(@NotNull String archiveAtomId) {
        String[] idParts = archiveAtomId.split("/");
        return (idParts.length == VALID_ID_PARTS);
    }

    private boolean isAtomId(String archiveIdentifier) {
        return archiveIdentifier.contains("/");
    }

}
