package eu.icarus.momca.momcapi.atomid;

import eu.icarus.momca.momcapi.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by daniel on 25.06.2015.
 */
public class CharterAtomId extends AtomId {

    @NotNull
    private final Optional<String> archiveId;
    @NotNull
    private final String charterId;
    @NotNull
    private final Optional<String> collectionId;
    @NotNull
    private final Optional<String> fondId;

    public CharterAtomId(@NotNull String value) {

        super(value);
        String[] valueTokens = value.split("/");

        if (!valueTokens[1].equals(ResourceType.CHARTER.getValue())) {
            throw new IllegalArgumentException(String.format("'%s' identifies a '%s', not a charter.", value, valueTokens[1]));
        }

        switch (valueTokens.length) {

            case 4:
                this.collectionId = Optional.of(valueTokens[2]);
                this.charterId = valueTokens[3];
                this.archiveId = Optional.empty();
                this.fondId = Optional.empty();
                break;
            case 5:
                this.archiveId = Optional.of(valueTokens[2]);
                this.fondId = Optional.of(valueTokens[3]);
                this.charterId = valueTokens[4];
                this.collectionId = Optional.empty();
                break;
            default:
                throw new IllegalArgumentException(String.format("'%s' is not a valid charterId.", value));

        }

    }

    public CharterAtomId(@NotNull String archiveId, @NotNull String fondId, @NotNull String charterId) {
        this(String.join("/", DEFAULT_PREFIX, ResourceType.CHARTER.getValue(), archiveId, fondId, charterId));
    }

    public CharterAtomId(@NotNull String collectionId, @NotNull String charterId) {
        this(String.join("/", DEFAULT_PREFIX, ResourceType.CHARTER.getValue(), collectionId, charterId));
    }

    @NotNull
    public Optional<String> getArchiveId() {
        return archiveId;
    }

    @NotNull
    public String getBasePath() {
        return isPartOfArchiveFond() ? getArchiveId().get() + "/" + getFondId().get() : getCollectionId().get();
    }

    @NotNull
    public String getCharterId() {
        return charterId;
    }

    @NotNull
    public Optional<String> getCollectionId() {
        return collectionId;
    }

    @NotNull
    public Optional<String> getFondId() {
        return fondId;
    }

    public boolean isPartOfArchiveFond() {
        return (archiveId.isPresent() && fondId.isPresent()) && !collectionId.isPresent();
    }

    public boolean isPartOfCollection() {
        return (!archiveId.isPresent() && !fondId.isPresent()) && collectionId.isPresent();
    }

    @Override
    public String toString() {
        return "CharterAtomId{" +
                "collectionId=" + collectionId +
                ", archiveId=" + archiveId +
                ", fondId=" + fondId +
                ", charterId='" + charterId + '\'' +
                '}';
    }

}