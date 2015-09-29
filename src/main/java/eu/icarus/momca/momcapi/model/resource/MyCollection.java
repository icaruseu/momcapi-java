package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.id.IdMyCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.xml.atom.AtomEntry;
import eu.icarus.momca.momcapi.model.xml.xrx.Sharing;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.Document;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by djell on 29/09/2015.
 */
public class MyCollection extends AtomResource {

    public static final String CEI_TEMPLATE = "<cei:cei xmlns:cei='http://www.monasterium.net/NS/cei'><cei:teiHeader><cei:fileDesc><cei:titleStmt><cei:title>%s</cei:title></cei:titleStmt><cei:publicationStmt /></cei:fileDesc></cei:teiHeader><cei:text type='collection'><cei:front><cei:div type='preface' /></cei:front><cei:group><cei:text sameAs='' type='collection' /><cei:text sameAs='' type='charter' /></cei:group><cei:back /></cei:text></cei:cei>";
    public static final String CEI_TEMPLATE_WITH_PREFACE = "<cei:cei xmlns:cei='http://www.monasterium.net/NS/cei'><cei:teiHeader><cei:fileDesc><cei:titleStmt><cei:title>%s</cei:title></cei:titleStmt><cei:publicationStmt /></cei:fileDesc></cei:teiHeader><cei:text type='collection'><cei:front><cei:div type='preface' >%s</cei:div></cei:front><cei:group><cei:text sameAs='' type='collection' /><cei:text sameAs='' type='charter' /></cei:group><cei:back /></cei:text></cei:cei>";
    public static final String PATH_PART = "metadata.mycollection";
    @NotNull
    private String name;
    @NotNull
    private Optional<String> preface = Optional.empty();
    @NotNull
    private Sharing sharing = new Sharing("private", null);
    @NotNull
    private MyCollectionStatus status;

    public MyCollection(@NotNull String identifier, @NotNull String name, @NotNull IdUser creator,
                        @NotNull MyCollectionStatus status) {

        super(
                new IdMyCollection(identifier),
                createUri(identifier, creator, status),
                createResourceName(identifier));

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;
        this.creator = Optional.of(creator);
        this.status = status;

        regenerateXmlContent();

    }

    public MyCollection(@NotNull ExistResource existResource) {

        super(existResource);

        this.status = initStatusFromResource(existResource);

        Element xml = toDocument().getRootElement();

        this.name = Util.queryXmlForOptionalString(xml, XpathQuery.QUERY_CEI_TITLE)
                .orElseThrow(IllegalArgumentException::new).replaceAll("\\s+", " ");
        this.creator = initCreatorFromXml(xml);
        this.sharing = initSharingFromXml(xml);
        this.preface = initPrefaceFromXml(xml);


    }

    @NotNull
    private static String createResourceName(@NotNull String identifier) {
        return identifier + ResourceType.MY_COLLECTION.getNameSuffix();
    }

    private static String createUri(String identifier, IdUser creator, MyCollectionStatus status) {

        String uri = "";

        switch (status) {

            case PRIVATE:
                uri = String.format("%s/%s/%s/%s",
                        ResourceRoot.USER_DATA.getUri(),
                        creator.getIdentifier(),
                        PATH_PART,
                        identifier);
                break;
            case PUBLISHED:
                uri = String.format("%s/%s",
                        ResourceRoot.PUBLISHED_USER_COLLECTIONS.getUri(),
                        identifier);
                break;

        }

        return uri;

    }

    @NotNull
    @Override
    public IdMyCollection getId() {
        return (IdMyCollection) id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Optional<String> getPreface() {
        return preface;
    }

    @NotNull
    public Sharing getSharing() {
        return sharing;
    }

    @NotNull
    public MyCollectionStatus getStatus() {
        return status;
    }

    private Optional<String> initPrefaceFromXml(Element xml) {

        return Util.queryXmlForOptionalElement(xml, XpathQuery.QUERY_CEI_FRONT_PREFACE)
                .map(Util::joinChildNodes)
                .filter(s -> !s.isEmpty());

    }

    private Sharing initSharingFromXml(Element xml) {

        String visibility = Util.queryXmlForString(xml, XpathQuery.QUERY_XRX_VISIBILITY);
        String user = Util.queryXmlForString(xml, XpathQuery.QUERY_XRX_USER);

        return new Sharing(visibility, user);

    }

    private MyCollectionStatus initStatusFromResource(ExistResource existResource) {

        String uri = existResource.getParentUri();

        if (uri.startsWith(ResourceRoot.PUBLISHED_USER_COLLECTIONS.getUri())) {
            return MyCollectionStatus.PUBLISHED;
        } else if (uri.startsWith(ResourceRoot.USER_DATA.getUri())) {
            return MyCollectionStatus.PRIVATE;
        } else {
            throw new MomcaException("Unable to determine MyCollection status from resource uri '" + uri + "'!");
        }

    }

    @Override
    public void regenerateXmlContent() {

        Element cei = Util.parseToElement(
                preface.isPresent() ?
                        String.format(CEI_TEMPLATE_WITH_PREFACE, name, preface.get()) :
                        String.format(CEI_TEMPLATE, name));

        String published = getPublished();
        String updated = getUpdated();


        Element resourceContent = new AtomEntry(
                getId().getContentXml(),
                createAtomAuthor(),
                (published.isEmpty()) ? AtomResource.localTime() : published,
                (updated.isEmpty()) ? AtomResource.localTime() : updated,
                cei);

        resourceContent.insertChild(sharing.copy(), 6);

        setXmlContent(new Document(resourceContent));

    }

    @Override
    public void setIdentifier(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.id = new IdMyCollection(identifier);

        setResourceName(createResourceName(identifier));
        setParentUri(createUri(identifier, creator.get(), status));

        regenerateXmlContent();

    }

    public void setName(@NotNull String name) {

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;
        regenerateXmlContent();

    }

    public void setPreface(@NotNull String preface) {

        if (preface.isEmpty()) {
            this.preface = Optional.empty();
        } else {
            this.preface = Optional.of(preface);
        }

        regenerateXmlContent();

    }

    public void setSharing(@NotNull Sharing sharing) {
        this.sharing = sharing;
        regenerateXmlContent();
    }

    public void setStatus(@NotNull MyCollectionStatus status) {
        setParentUri(createUri(getIdentifier(), creator.get(), status));
        this.status = status;
    }

}
