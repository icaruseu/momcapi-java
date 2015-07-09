package eu.icarus.momca.momcapi.resource;


import eu.icarus.momca.momcapi.exist.MetadataCollectionName;
import eu.icarus.momca.momcapi.resource.atom.AtomAuthor;
import eu.icarus.momca.momcapi.resource.atom.CharterAtomId;
import eu.icarus.momca.momcapi.resource.cei.CeiIdno;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by daniel on 25.06.2015.
 */
public class Charter extends ExistResource {

    @NotNull
    private final AtomAuthor atomAuthor;
    @NotNull
    private final CharterAtomId atomId;
    @NotNull
    private final CeiIdno ceiIdno;
    @NotNull
    private final CharterStatus status;

    public Charter(@NotNull ExistResource existResource) {

        super(existResource);

        this.status = initStatus();

        this.atomId = initCharterAtomId();
        this.atomAuthor = new AtomAuthor(queryUniqueElement(XpathQuery.QUERY_ATOM_EMAIL));
        this.ceiIdno = new CeiIdno(queryUniqueElement(XpathQuery.QUERY_CEI_IDNO_ID), queryUniqueElement(XpathQuery.QUERY_CEI_IDNO_TEXT));

    }

    @NotNull
    public AtomAuthor getAtomAuthor() {
        return atomAuthor;
    }

    @NotNull
    public CharterAtomId getAtomId() {
        return atomId;
    }

    @NotNull
    public CeiIdno getCeiIdno() {
        return ceiIdno;
    }

    @NotNull
    public CharterStatus getStatus() {
        return status;
    }

    @NotNull
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
