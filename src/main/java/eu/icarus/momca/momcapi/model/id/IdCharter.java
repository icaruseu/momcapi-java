package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.resource.ResourceType;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents the {@code atom:id} of a charter in MOM-CA, e.g.
 * {@code tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232}.
 *
 * @author Daniel Jeller
 *         Created on 25.06.2015.
 */
public class IdCharter extends IdAtomId {

    @NotNull
    private final Optional<IdCollection> idCollection;
    @NotNull
    private final Optional<IdFond> idFond;

    public IdCharter(@NotNull AtomId atomId) {

        super(atomId);

        if (getContentXml().getType() != ResourceType.CHARTER) {
            throw new IllegalArgumentException(getContentXml().getText() + " is not a charter atom:id.");
        }

        if (isInFond()) {
            idFond = Optional.of(getFondFromAtomId(atomId));
            idCollection = Optional.empty();
        } else {
            idFond = Optional.empty();
            idCollection = Optional.of(getCollectionFromAtomId(atomId));
        }

    }

    public IdCharter(@NotNull String archiveIdentifier, @NotNull String fondIdentifier, @NotNull String charterIdentifier) {
        super(initAtomIdForFond(archiveIdentifier, fondIdentifier, charterIdentifier));
        idFond = Optional.of(new IdFond(archiveIdentifier, fondIdentifier));
        idCollection = Optional.empty();
    }

    public IdCharter(@NotNull String collectionIdentifier, @NotNull String charterIdentifier) {
        super(initAtomIdForCollection(collectionIdentifier, charterIdentifier));
        idCollection = Optional.of(new IdCollection(collectionIdentifier));
        idFond = Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        IdCharter idCharter = (IdCharter) o;

        if (!idCollection.equals(idCharter.idCollection)) return false;
        return idFond.equals(idCharter.idFond);

    }

    @NotNull
    private IdCollection getCollectionFromAtomId(@NotNull AtomId atomId) {

        String[] parts = atomId.getText().split("/");
        String collectionIdentifier = Util.decode(parts[parts.length - 2]);
        return new IdCollection(collectionIdentifier);

    }

    @NotNull
    private IdFond getFondFromAtomId(@NotNull AtomId atomId) {

        String[] parts = atomId.getText().split("/");
        String archiveIdentifier = Util.decode(parts[parts.length - 3]);
        String fondIdentifier = Util.decode(parts[parts.length - 2]);

        return new IdFond(archiveIdentifier, fondIdentifier);

    }

    @NotNull
    public String getHierarchicalUriPart() {

        String idPart;

        if (isInFond()) {

            String archiveIdentifier = getIdFond().get().getIdArchive().getIdentifier();
            String fondIdentifier = getIdFond().get().getIdentifier();
            idPart = archiveIdentifier + "/" + fondIdentifier;

        } else {

            idPart = getIdCollection().get().getIdentifier();

        }

        return idPart;

    }

    @NotNull
    public Optional<IdCollection> getIdCollection() {
        return idCollection;
    }

    @NotNull
    public Optional<IdFond> getIdFond() {
        return idFond;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + idCollection.hashCode();
        result = 31 * result + idFond.hashCode();
        return result;
    }

    private static AtomId initAtomIdForCollection(@NotNull String collectionIdentifier, @NotNull String charterIdentifier) {

        if (collectionIdentifier.isEmpty() || charterIdentifier.isEmpty()) {
            throw new IllegalArgumentException("The identifiers are not allowed to be empty strings.");
        }

        if (collectionIdentifier.contains("/") || charterIdentifier.contains("/")) {
            throw new IllegalArgumentException("One of the identifiers contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/",
                AtomId.DEFAULT_PREFIX,
                ResourceType.CHARTER.getNameInId(),
                collectionIdentifier,
                charterIdentifier));

    }

    private static AtomId initAtomIdForFond(@NotNull String archiveIdentifier, @NotNull String fondIdentifier, @NotNull String charterIdentifier) {

        if (archiveIdentifier.isEmpty() || fondIdentifier.isEmpty() || charterIdentifier.isEmpty()) {
            throw new IllegalArgumentException("The identifiers are not allowed to be empty strings.");
        }

        if (archiveIdentifier.contains("/") || fondIdentifier.contains("/")) {
            throw new IllegalArgumentException("One of the identifiers contains '/'" +
                    " which is forbidden. Maybe the string is an atom:id text and not just an identifier?");
        }

        return new AtomId(String.join("/",
                AtomId.DEFAULT_PREFIX,
                ResourceType.CHARTER.getNameInId(),
                archiveIdentifier,
                fondIdentifier,
                charterIdentifier));

    }

    public boolean isInFond() {
        String[] parts = getContentXml().getText().split("/");
        return parts.length == ResourceType.CHARTER.getMaxIdParts();
    }

}
