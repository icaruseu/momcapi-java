package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.id.*;
import eu.icarus.momca.momcapi.model.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Created by djell on 21/08/2015.
 */
public abstract class AtomResource extends ExistResource {

    @NotNull
    Optional<IdUser> creator = Optional.empty();
    @NotNull
    IdAtomId id;

    AtomResource(@NotNull IdAtomId id, @NotNull ResourceType resourceType, @NotNull String baseUri) {

        super(new ExistResource(
                String.format("%s%s", id.getIdentifier(), resourceType.getNameSuffix()),
                createResourceUri(id, resourceType, baseUri), "<empty />"));

        this.id = id;

    }

    AtomResource(@NotNull ExistResource existResource) {

        super(existResource);

        String atomIdString = Util.queryXmlToString(existResource.toDocument().getRootElement(), XpathQuery.QUERY_ATOM_ID);

        if (atomIdString.isEmpty()) {
            throw new IllegalArgumentException("The provided atom resource content does not contain an atom:id: "
                    + existResource.toDocument().toXML());
        }

        getIdFromString(atomIdString);

    }

    private static String createResourceUri(IdAtomId id, ResourceType resourceType, String baseUri) {

        String url = "";
        String identifier = id.getIdentifier();

        switch (resourceType) {

            case ANNOTATION_IMAGE:
                url = ""; // TODO
                break;
            case ARCHIVE:
                url = String.format("%s/%s", baseUri, identifier);
                break;
            case CHARTER:
                IdCharter idCharter = (IdCharter) id;
                if (idCharter.isInFond()) {
                    String archiveIdentifier = idCharter.getIdFond().get().getIdArchive().getIdentifier();
                    String fondIdentifier = idCharter.getIdFond().get().getIdentifier();
                    url = String.format("%s/%s/%s", baseUri, archiveIdentifier, fondIdentifier);
                } else {
                    String collectionIdentifier = idCharter.getIdCollection().get().getIdentifier();
                    url = String.format("%s/%s", baseUri, collectionIdentifier);
                }
                break;
            case COLLECTION:
                url = String.format("%s/%s", baseUri, identifier);
                break;
            case FOND:
                IdFond idFond = (IdFond) id;
                String archiveIdentifier = idFond.getIdArchive().getIdentifier();
                url = String.format("%s/%s/%s", baseUri, archiveIdentifier, identifier);
                break;
            case MY_COLLECTION:
                url = String.format("%s/%s", baseUri, identifier);
                break;
            case SVG:
                url = ""; // TODO
                break;

        }

        return url;

    }

    @NotNull
    static String localTime() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @NotNull
    AtomAuthor createAtomAuthor() {
        return new AtomAuthor(getCreator().map(IdAbstract::getIdentifier).orElse(""));
    }

    @NotNull
    public final Optional<IdUser> getCreator() {
        return creator;
    }

    @NotNull
    public abstract IdAtomId getId();

    private void getIdFromString(String atomIdString) {

        AtomId atomId = new AtomId(atomIdString);

        switch (atomId.getType()) {

            case ANNOTATION_IMAGE:
                this.id = new IdAnnotation(atomId);
                break;
            case ARCHIVE:
                this.id = new IdArchive(atomId);
                break;
            case CHARTER:
                this.id = new IdCharter(atomId);
                break;
            case COLLECTION:
                this.id = new IdCollection(atomId);
                break;
            case FOND:
                this.id = new IdFond(atomId);
                break;
            case MY_COLLECTION:
                this.id = new IdMyCollection(atomId);
                break;
            case SVG:
                this.id = new IdSvg(atomId);
                break;

        }

    }

    @NotNull
    public final String getIdentifier() {
        return id.getIdentifier();
    }

    @NotNull
    Optional<IdUser> readCreatorFromXml(@NotNull Element xml) {

        Optional<IdUser> result = Optional.empty();
        String queryResult = Util.queryXmlToString(xml, XpathQuery.QUERY_ATOM_EMAIL);

        if (!queryResult.isEmpty()) {
            result = Optional.of(new IdUser(queryResult));
        }

        return result;

    }

    public final void setCreator(@Nullable String creator) {

        if (creator == null || creator.isEmpty()) {
            this.creator = Optional.empty();
        } else {
            this.creator = Optional.of(new IdUser(creator));
        }

        updateXmlContent();

    }

    public abstract void setIdentifier(@NotNull String identifier);

    abstract void updateXmlContent();

}
