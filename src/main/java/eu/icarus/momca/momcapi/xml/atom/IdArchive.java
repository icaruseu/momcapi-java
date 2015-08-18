package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the {@code atom:id} of an archive in MOM-CA, e.g. {@code tag:www.monasterium.net,2011:/archive/CH-KAE}.
 *
 * @author Daniel Jeller
 *         Created on 20.07.2015.
 */
public class IdArchive extends AtomId {

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
                ? archiveIdentifier : String.format("%s/%s", ResourceType.ARCHIVE.getNameInId(), archiveIdentifier));

        if (isId(archiveIdentifier) && !isIdArchive(archiveIdentifier)) {
            String message = String.format("'%s' is not a valid archive atom:id.", archiveIdentifier);
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

    @Override
    @NotNull
    public String toString() {
        return "IdArchive{" +
                "archiveIdentifier='" + archiveIdentifier + '\'' +
                "} " + super.toString();
    }

    private boolean isId(@NotNull String archiveIdentifier) {
        return archiveIdentifier.contains("/");
    }

    private boolean isIdArchive(@NotNull String idArchive) {
        String[] idParts = idArchive.split("/");
        return (getType() == ResourceType.ARCHIVE && idParts.length == VALID_ID_PARTS);
    }

}
