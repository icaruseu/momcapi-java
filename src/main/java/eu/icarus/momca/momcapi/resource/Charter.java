package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import eu.icarus.momca.momcapi.exist.MetadataCollectionName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by daniel on 25.06.2015.
 */
public class Charter extends ExistResource {

    @NotNull
    private final CharterAtomId atomId;
    @NotNull
    private final String authorName;
    @NotNull
    private final String idnoId;
    @NotNull
    private final String idnoText;
    @NotNull
    private final CharterStatus status;

    public Charter(@NotNull ExistResource existResource) {

        super(existResource);

        this.status = initStatus();

        this.atomId = initCharterAtomId();
        this.authorName = queryUniqueElement(XpathQuery.QUERY_ATOM_EMAIL);
        this.idnoText = queryUniqueElement(XpathQuery.QUERY_CEI_IDNO_TEXT);
        this.idnoId = queryUniqueElement(XpathQuery.QUERY_CEI_IDNO_ID);

    }

    @NotNull
    public CharterAtomId getAtomId() {
        return atomId;
    }

    @NotNull
    public String getAuthorName() {
        return authorName;
    }

    @NotNull
    public String getIdnoId() {
        return idnoId;
    }

    @NotNull
    public String getIdnoText() {
        return idnoText;
    }

    @NotNull
    public CharterStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Charter{" +
                "atomId=" + atomId +
                ", status=" + status +
                "} " + super.toString();
    }

    @NotNull
    private CharterAtomId initCharterAtomId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);
        if (idString.isEmpty()) {
            throw new IllegalArgumentException("No atom:id in charter.");
        } else {
            return new CharterAtomId(idString);
        }

    }

    private CharterStatus initStatus() {

        CharterStatus status;

        if (getParentUri().contains(MetadataCollectionName.METADATA_CHARTER_IMPORT.getValue())) {
            status = CharterStatus.IMPORTED;
        } else if (getParentUri().contains(MetadataCollectionName.XRX_USER.getValue())) {
            status = CharterStatus.PRIVATE;
        } else if (getParentUri().contains(MetadataCollectionName.METADATA_CHARTER_SAVED.getValue())) {
            status = CharterStatus.SAVED;
        } else {
            status = CharterStatus.PUBLIC;
        }

        return status;

    }

    @NotNull
    private String queryUniqueElement(@NotNull XpathQuery query) {

        List<String> atomQueryResults = queryContentXml(query);

        String result;

        switch (atomQueryResults.size()) {
            case 0:
                result = "";
                break;
            case 1:
                result = atomQueryResults.get(0);
                break;
            default:
                throw new IllegalArgumentException(String.format("More than one results for Query '%s'", query.getQuery()));

        }

        return result;

    }

}
