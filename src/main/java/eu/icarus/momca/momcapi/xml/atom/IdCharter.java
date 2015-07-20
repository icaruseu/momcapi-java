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
public class IdCharter extends Id {

    private static final int MAX_ID_PARTS = 5;
    private static final int MIN_ID_PARTS = 4;
    @NotNull
    private final Optional<String> archiveIdentifier;
    @NotNull
    private final String charterIdentifier;
    @NotNull
    private final Optional<String> collectionIdentifier;
    @NotNull
    private final Optional<String> fondIdentifier;


    /**
     * Instantiates a new Atom id charter with an existing {@code atom:id String}.
     *
     * @param atomIdString A full {@code atom:id String}.
     */
    public IdCharter(@NotNull String atomIdString) {

        super(atomIdString);

        if (!isCharterId()) {
            String message = String.format("'%s' has the wrong ResourceType identifier, not %s.",
                    atomIdString,
                    ResourceType.CHARTER.getAtomIdPart());
            throw new IllegalArgumentException(message);
        }

        String[] idParts = atomIdString.split("/");

        switch (idParts.length) {

            case MIN_ID_PARTS:
                this.collectionIdentifier = Optional.of(Util.decode(idParts[2]));
                this.charterIdentifier = Util.decode(idParts[3]);
                this.archiveIdentifier = Optional.empty();
                this.fondIdentifier = Optional.empty();
                break;

            case MAX_ID_PARTS:
                this.archiveIdentifier = Optional.of(Util.decode(idParts[2]));
                this.fondIdentifier = Optional.of(Util.decode(idParts[3]));
                this.charterIdentifier = Util.decode(idParts[4]);
                this.collectionIdentifier = Optional.empty();
                break;

            default:
                String message = String.format("'%s' is not a valid charterIdentifier.", atomIdString);
                throw new IllegalArgumentException(message);

        }

    }

    /**
     * Instantiates a new IdCharter for a charter that belongs to an archival fond.
     *
     * @param archiveIdentifier The archive id.
     * @param fondIdentifier    The fond id.
     * @param charterIdentifier The charter id.
     */
    public IdCharter(@NotNull String archiveIdentifier, @NotNull String fondIdentifier, @NotNull String charterIdentifier) {

        super(ResourceType.CHARTER.getAtomIdPart(), archiveIdentifier, fondIdentifier, charterIdentifier);

        this.archiveIdentifier = Optional.of(archiveIdentifier);
        this.fondIdentifier = Optional.of(fondIdentifier);
        this.charterIdentifier = charterIdentifier;
        this.collectionIdentifier = Optional.empty();

    }

    /**
     * Instantiates a new IdCharter that belongs to a collection.
     *
     * @param collectionIdentifier The collection id.
     * @param charterIdentifier    The charter id.
     */
    public IdCharter(@NotNull String collectionIdentifier, @NotNull String charterIdentifier) {

        super(ResourceType.CHARTER.getAtomIdPart(), collectionIdentifier, charterIdentifier);

        this.collectionIdentifier = Optional.of(collectionIdentifier);
        this.charterIdentifier = charterIdentifier;
        this.archiveIdentifier = Optional.empty();
        this.fondIdentifier = Optional.empty();

    }

    /**
     * @return The archive identifier, e.g. {@code CH-KAE}.
     */
    @NotNull
    public Optional<String> getArchiveIdentifier() {
        return archiveIdentifier;
    }

    /**
     * @return The charters' base path, either {@code archive/fond} or {@code collection}
     */
    @NotNull
    public String getBasePath() {
        return isPartOfArchiveFond()
                ? getArchiveIdentifier().get() + "/" + getFondIdentifier().get() : getCollectionIdentifier().get();
    }

    /**
     * @return The charter identifier (mostly its signature).
     */
    @NotNull
    public String getCharterIdentifier() {
        return charterIdentifier;
    }

    /**
     * @return The collection identifier.
     */
    @NotNull
    public Optional<String> getCollectionIdentifier() {
        return collectionIdentifier;
    }

    /**
     * @return The fond identifier.
     */
    @NotNull
    public Optional<String> getFondIdentifier() {
        return fondIdentifier;
    }

    /**
     * @return {@code True} if part of an archival fond (as opposed to being part of a collection).
     */
    public boolean isPartOfArchiveFond() {
        return (archiveIdentifier.isPresent() && fondIdentifier.isPresent()) && !collectionIdentifier.isPresent();
    }

    @NotNull
    @Override
    public String toString() {

        return "IdCharter{" +
                "collectionIdentifier=" + collectionIdentifier +
                ", archiveIdentifier=" + archiveIdentifier +
                ", fondIdentifier=" + fondIdentifier +
                ", charterIdentifier='" + charterIdentifier + '\'' +
                '}';

    }

    private boolean isCharterId() {
        String typeToken = this.getAtomId().split("/")[1];
        return typeToken.equals(ResourceType.CHARTER.getAtomIdPart());
    }

}
