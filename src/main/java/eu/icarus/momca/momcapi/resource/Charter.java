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
    private final CharterStatus status;

    public Charter(@NotNull ExistResource existResource) {

        super(existResource);
        List<String> atomQueryResults = queryContentXml(XpathQuery.QUERY_ATOM_ID);

        if (atomQueryResults.size() == 1) {
            this.atomId = new CharterAtomId(atomQueryResults.get(0));
        } else {
            throw new IllegalArgumentException("XML Content is not a valid for a charter");
        }

        this.status = initStatus();

    }

    @NotNull
    public CharterAtomId getAtomId() {
        return atomId;
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

}
