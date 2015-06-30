package eu.icarus.momca.momcapi.atomid;

import eu.icarus.momca.momcapi.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;

/**
 * Created by daniel on 25.06.2015.
 */
public class CharterAtomId extends AtomId {

    @NotNull
    private static final String DEFAULT_ENCODING = "UTF-8";
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

        try {

            switch (valueTokens.length) {

                case 4:
                    this.collectionId = Optional.of(URLDecoder.decode(valueTokens[2], DEFAULT_ENCODING));
                    this.charterId = URLDecoder.decode(valueTokens[3], DEFAULT_ENCODING);
                    this.archiveId = Optional.empty();
                    this.fondId = Optional.empty();
                    break;
                case 5:
                    this.archiveId = Optional.of(URLDecoder.decode(valueTokens[2], DEFAULT_ENCODING));
                    this.fondId = Optional.of(URLDecoder.decode(valueTokens[3], DEFAULT_ENCODING));
                    this.charterId = URLDecoder.decode(valueTokens[4], DEFAULT_ENCODING);
                    this.collectionId = Optional.empty();
                    break;
                default:
                    throw new IllegalArgumentException(String.format("'%s' is not a valid charterId.", value));

            }

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public CharterAtomId(@NotNull String archiveId, @NotNull String fondId, @NotNull String charterId) {

        super(ResourceType.CHARTER.getValue(), archiveId, fondId, charterId);
        this.archiveId = Optional.of(archiveId);
        this.fondId = Optional.of(fondId);
        this.charterId = charterId;
        this.collectionId = Optional.empty();

    }

    public CharterAtomId(@NotNull String collectionId, @NotNull String charterId) {

        super(ResourceType.CHARTER.getValue(), collectionId, charterId);
        this.collectionId = Optional.of(collectionId);
        this.charterId = charterId;
        this.archiveId = Optional.empty();
        this.fondId = Optional.empty();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CharterAtomId that = (CharterAtomId) o;

        if (!archiveId.equals(that.archiveId)) return false;
        if (!charterId.equals(that.charterId)) return false;
        if (!collectionId.equals(that.collectionId)) return false;
        return fondId.equals(that.fondId);

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

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + archiveId.hashCode();
        result = 31 * result + charterId.hashCode();
        result = 31 * result + collectionId.hashCode();
        result = 31 * result + fondId.hashCode();
        return result;
    }

    public boolean isPartOfArchiveFond() {
        return (archiveId.isPresent() && fondId.isPresent()) && !collectionId.isPresent();
    }

    public boolean isPartOfCollection() {
        return (!archiveId.isPresent() && !fondId.isPresent()) && collectionId.isPresent();
    }

    @NotNull
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
