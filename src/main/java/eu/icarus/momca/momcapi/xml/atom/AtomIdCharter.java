package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents the {@code atom:id} of a charter in MOM-CA, e.g.
 * {@code tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232}.
 *
 * @author Daniel Jeller
 *         Created on 25.06.2015.
 */
public class AtomIdCharter extends AtomId {

    @NotNull
    private final Optional<String> archiveId;
    @NotNull
    private final String charterId;
    @NotNull
    private final Optional<String> collectionId;
    @NotNull
    private final Optional<String> fondId;


    /**
     * Instantiates a new Atom id charter with an existing {@code atom:id String}.
     *
     * @param atomIdString A full {@code atom:id String}.
     */
    public AtomIdCharter(@NotNull String atomIdString) {

        super(atomIdString);

        if (!isCharterId()) {
            String message = String.format("'%s' has the wrong ResourceType identifier, not %s.",
                    atomIdString,
                    ResourceType.CHARTER.getAtomIdPart());
            throw new IllegalArgumentException(message);
        }

        String[] idParts = atomIdString.split("/");

        switch (idParts.length) {

            case 4:
                this.collectionId = Optional.of(Util.decode(idParts[2]));
                this.charterId = Util.decode(idParts[3]);
                this.archiveId = Optional.empty();
                this.fondId = Optional.empty();
                break;

            case 5:
                this.archiveId = Optional.of(Util.decode(idParts[2]));
                this.fondId = Optional.of(Util.decode(idParts[3]));
                this.charterId = Util.decode(idParts[4]);
                this.collectionId = Optional.empty();
                break;

            default:
                String message = String.format("'%s' is not a valid charterId.", atomIdString);
                throw new IllegalArgumentException(message);

        }

    }

    /**
     * Instantiates a new AtomIdCharter for a charter that belongs to an archival fond.
     *
     * @param archiveId The archive id.
     * @param fondId    The fond id.
     * @param charterId The charter id.
     */
    public AtomIdCharter(@NotNull String archiveId, @NotNull String fondId, @NotNull String charterId) {

        super(ResourceType.CHARTER.getAtomIdPart(), archiveId, fondId, charterId);

        this.archiveId = Optional.of(archiveId);
        this.fondId = Optional.of(fondId);
        this.charterId = charterId;
        this.collectionId = Optional.empty();

    }

    /**
     * Instantiates a new AtomIdCharter that belongs to a collection.
     *
     * @param collectionId The collection id.
     * @param charterId    The charter id.
     */
    public AtomIdCharter(@NotNull String collectionId, @NotNull String charterId) {

        super(ResourceType.CHARTER.getAtomIdPart(), collectionId, charterId);

        this.collectionId = Optional.of(collectionId);
        this.charterId = charterId;
        this.archiveId = Optional.empty();
        this.fondId = Optional.empty();

    }

    /**
     * @return The archive id.
     */
    @NotNull
    public Optional<String> getArchiveId() {
        return archiveId;
    }

    /**
     * @return The charters' base path, either {@code archive/fond} or {@code collection}
     */
    @NotNull
    public String getBasePath() {
        return isPartOfArchiveFond() ? getArchiveId().get() + "/" + getFondId().get() : getCollectionId().get();
    }

    /**
     * @return The charter id.
     */
    @NotNull
    public String getCharterId() {
        return charterId;
    }

    /**
     * @return The collection id.
     */
    @NotNull
    public Optional<String> getCollectionId() {
        return collectionId;
    }

    /**
     * @return The fond id.
     */
    @NotNull
    public Optional<String> getFondId() {
        return fondId;
    }

    /**
     * @return {@code True} if part of an archival fond (as opposed to being part of a collection).
     */
    public boolean isPartOfArchiveFond() {
        return (archiveId.isPresent() && fondId.isPresent()) && !collectionId.isPresent();
    }

    @NotNull
    @Override
    public String toString() {

        return "AtomIdCharter{" +
                "collectionId=" + collectionId +
                ", archiveId=" + archiveId +
                ", fondId=" + fondId +
                ", charterId='" + charterId + '\'' +
                '}';

    }

    private boolean isCharterId() {
        String typeToken = this.getAtomId().split("/")[1];
        return typeToken.equals(ResourceType.CHARTER.getAtomIdPart());
    }

}
