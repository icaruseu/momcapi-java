package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the {@code atom:id} of a fond in MOM-CA, e.g. {@code tag:www.monasterium.net,2011:/fond/RS-IAGNS/Charters}.
 *
 * @author Daniel Jeller
 *         Created on 21.07.2015.
 */
public class IdFond extends Id {

    private static final int VALID_FOND_ID_PART_COUNT = 4;
    @NotNull
    private final String archiveIdentifier;
    @NotNull
    private final String fondIdentifier;

    /**
     * Instantiates a new fond id using archiveIdentifier and fondIdentifier.
     *
     * @param archiveIdentifier The archiveIdentifier, e.g. {@code RS-IAGNS}
     * @param fondIdentifier    The fondIdentifier, e.g. {@code Charters}
     *                          .
     */
    public IdFond(@NotNull String archiveIdentifier, @NotNull String fondIdentifier) {
        super(String.format("%s/%s/%s", ResourceType.FOND.getNameInId(), archiveIdentifier, fondIdentifier));
        this.archiveIdentifier = archiveIdentifier;
        this.fondIdentifier = fondIdentifier;
    }

    /**
     * Instantiates a new fond id using another IdFond.
     *
     * @param fondId The atom:id, e.g.
     *               {@code tag:www.monasterium.net,2011:/fond/RS-IAGNS/Charters}.
     */
    public IdFond(@NotNull String fondId) {

        super(fondId);

        if (!isFondId(fondId)) {
            String message = String.format("'%s' is not a valid fond atom:id.", fondId);
            throw new IllegalArgumentException(message);
        }

        String[] idParts = fondId.split("/");

        this.archiveIdentifier = idParts[2];
        this.fondIdentifier = idParts[3];

    }

    /**
     * @return The archive identifier, e.g. {@code RS-IAGNS}.
     */
    @NotNull
    public String getArchiveIdentifier() {
        return archiveIdentifier;
    }

    /**
     * @return The fond identifier, e.g. {@code Charters}.
     */
    @NotNull
    public String getFondIdentifier() {
        return fondIdentifier;
    }

    private boolean isFondId(@NotNull String fondId) {
        String[] idParts = fondId.split("/");
        return getType() == ResourceType.FOND && idParts.length == VALID_FOND_ID_PART_COUNT;
    }

}
