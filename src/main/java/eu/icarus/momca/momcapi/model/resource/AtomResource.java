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

    AtomResource(@NotNull IdAtomId id, @NotNull String parentUri, @NotNull String resourceName) {
        super(new ExistResource(resourceName, parentUri, "<empty />"));
        this.id = id;
    }

    AtomResource(@NotNull ExistResource existResource) {

        super(existResource);

        String atomIdString = Util.queryXmlForString(existResource.toDocument().getRootElement(), XpathQuery.QUERY_ATOM_ID);

        if (atomIdString.isEmpty()) {
            throw new IllegalArgumentException("The provided atom resource content does not contain an atom:id: "
                    + existResource.toDocument().toXML());
        }

        getIdFromString(atomIdString);

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
    Optional<IdUser> initCreatorFromXml(@NotNull Element xml) {

        Optional<IdUser> result = Optional.empty();
        String queryResult = Util.queryXmlForString(xml, XpathQuery.QUERY_ATOM_EMAIL);

        if (!queryResult.isEmpty()) {
            result = Optional.of(new IdUser(queryResult));
        }

        return result;

    }

    @NotNull
    static String localTime() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public abstract void regenerateXmlContent();

    public final void setCreator(@Nullable String creator) {

        if (creator == null || creator.isEmpty()) {
            this.creator = Optional.empty();
        } else {
            this.creator = Optional.of(new IdUser(creator));
        }

        regenerateXmlContent();

    }

    public abstract void setIdentifier(@NotNull String identifier);

}
