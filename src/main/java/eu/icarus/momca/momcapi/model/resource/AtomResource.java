package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.id.*;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.Element;
import nu.xom.Elements;
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
        this.id = readIdAtomIdFromXml(existResource.toDocument().getRootElement());
    }

    @NotNull
    AtomAuthor createAtomAuthor() {
        return new AtomAuthor(getCreator().map(IdAbstract::getIdentifier).orElse(""));
    }

    private static IdAtomId createIdFromString(String atomIdString) {

        AtomId atomId = new AtomId(atomIdString);

        IdAtomId id = null;

        switch (atomId.getType()) {

            case ANNOTATION_IMAGE:
                id = new IdAnnotation(atomId);
                break;
            case ARCHIVE:
                id = new IdArchive(atomId);
                break;
            case CHARTER:
                id = new IdCharter(atomId);
                break;
            case COLLECTION:
                id = new IdCollection(atomId);
                break;
            case FOND:
                id = new IdFond(atomId);
                break;
            case MY_COLLECTION:
                id = new IdMyCollection(atomId);
                break;
            case SVG:
                id = new IdSvg(atomId);
                break;

        }

        return id;

    }

    @NotNull
    public Element getContent() {

        Element content = toDocument().getRootElement().getFirstChildElement("content", Namespace.ATOM.getUri());

        if (content == null) {
            throw new MomcaException("No 'atom:content' present in xml.");
        }

        Elements childElements = content.getChildElements();

        if (childElements.size() != 1) {
            throw new MomcaException("The 'atom:content' element must have exactly one child element!");
        }

        return (Element) childElements.get(0).copy();

    }

    @NotNull
    public final Optional<IdUser> getCreator() {
        return creator;
    }

    @NotNull
    public abstract IdAtomId getId();

    @NotNull
    public final String getIdentifier() {
        return id.getIdentifier();
    }

    public String getPublished() {
        return Util.queryXmlForString(toDocument().getRootElement(), XpathQuery.QUERY_ATOM_PUBLISHED);
    }

    public String getUpdated() {
        return Util.queryXmlForString(toDocument().getRootElement(), XpathQuery.QUERY_ATOM_UPDATED);
    }

    @NotNull
    static String localTime() {
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @NotNull
    static Optional<IdUser> readCreatorFromXml(@NotNull Element xml) {

        Optional<IdUser> result = Optional.empty();
        String queryResult = Util.queryXmlForString(xml, XpathQuery.QUERY_ATOM_EMAIL);

        if (!queryResult.isEmpty()) {
            result = Optional.of(new IdUser(queryResult));
        }

        return result;

    }

    static IdAtomId readIdAtomIdFromXml(Element element) {
        String atomIdString = Util.queryXmlForString(element, XpathQuery.QUERY_ATOM_ID);

        if (atomIdString.isEmpty()) {
            throw new IllegalArgumentException("The provided atom resource content does not contain an atom:id: "
                    + element.toXML());
        }

        return createIdFromString(atomIdString);
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
