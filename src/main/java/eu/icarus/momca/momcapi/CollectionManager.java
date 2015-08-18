package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.*;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.xml.atom.Entry;
import eu.icarus.momca.momcapi.xml.atom.IdCollection;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by djell on 11/08/2015.
 */
public class CollectionManager extends AbstractManager {

    private static final String COLLECTION_TEMPLATE = "<cei:cei xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:teiHeader><cei:fileDesc><cei:sourceDesc><cei:p /></cei:sourceDesc></cei:fileDesc></cei:teiHeader><cei:text type=\"collection\"><cei:front><cei:image_server_address>%s</cei:image_server_address><cei:image_server_folder>%s</cei:image_server_folder><cei:user_name /><cei:password /><cei:provenance abbr=\"%s\">%s%s%s</cei:provenance><cei:publicationStmt><cei:availability n=\"ENRICH\" status=\"restricted\" /></cei:publicationStmt><cei:div type=\"preface\" /></cei:front><cei:group /></cei:text></cei:cei>";

    public CollectionManager(@NotNull MomcaConnection momcaConnection) {
        super(momcaConnection);
    }

    @NotNull
    public Collection addCollection(@NotNull String identifier, @NotNull String name, @NotNull String authorEmail,
                                    @Nullable Country country, @Nullable Region region, @Nullable String imageServerAddress,
                                    @Nullable String imageFolderName, @Nullable String keyWord) {

        IdCollection id = new IdCollection(identifier);

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name of the new collection is not allowed to be an empty string.");
        }

        if (authorEmail.isEmpty()) {
            throw new IllegalArgumentException("AuthorEmail is not allowed to be an empty string.");
        }

        if (getCollection(id).isPresent()) {
            String message = String.format("An collection for the id '%s' is already existing.", id.getId());
            throw new IllegalArgumentException(message);
        }

        MomcaResource resource = createNewCollectionResource(identifier, name, authorEmail, country, region,
                imageServerAddress, imageFolderName, keyWord, id);

        momcaConnection.addCollection(identifier, ResourceRoot.ARCHIVAL_COLLECTIONS.getUri());
        momcaConnection.storeExistResource(resource);

        return getCollection(id).orElseThrow(RuntimeException::new);

    }

    public void deleteCollection(@NotNull IdCollection idCollection) {

        if (!momcaConnection.getCharterManager().listChartersPublic(idCollection).isEmpty()
                || !momcaConnection.getCharterManager().listChartersImport(idCollection).isEmpty()) {
            String message = String.format("There are still existing charters for collection '%s'",
                    idCollection.getCollectionIdentifier());
            throw new IllegalArgumentException(message);
        }

        momcaConnection.deleteCollection(String.format("%s/%s",
                ResourceRoot.PUBLIC_CHARTERS.getUri(), idCollection.getCollectionIdentifier()));
        momcaConnection.deleteCollection(String.format("%s/%s",
                ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(), idCollection.getCollectionIdentifier()));

    }

    @NotNull
    public Optional<Collection> getCollection(@NotNull IdCollection idCollection) {
        return getMomcaResource(idCollection).map(Collection::new);
    }

    @NotNull
    public List<IdCollection> listCollections(@NotNull Country country) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listCollectionsForCountry(country.getCountryCode()));
        return queryResults.stream().map(IdCollection::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCollection> listCollections(@NotNull Region region) {
        List<String> queryResults = momcaConnection.queryDatabase(
                ExistQueryFactory.listCollectionsForRegion(region.getNativeName()));
        return queryResults.stream().map(IdCollection::new).collect(Collectors.toList());
    }

    @NotNull
    public List<IdCollection> listCollections() {
        List<String> queryResults = momcaConnection.queryDatabase(ExistQueryFactory.listCollections());
        return queryResults.stream().map(IdCollection::new).collect(Collectors.toList());
    }

    @NotNull
    private Element createCeiElement(@NotNull String identifier, @NotNull String name, @Nullable Country country,
                                     @Nullable Region region, @Nullable String imageServerAddress,
                                     @Nullable String imageFolderName) {

        String countryElement = (country == null) ?
                "" :
                String.format("<cei:country id=\"%s\">%s</cei:country>",
                        country.getCountryCode().getCode(),
                        country.getNativeName());

        String regionElement = (region == null) ?
                "" :
                String.format("<cei:region %s>%s</cei:region>",
                        region.getCode().isPresent() ?
                                String.format("id=\"%s\"", region.getCode().get()) :
                                "",
                        region.getNativeName());

        String content = String.format(COLLECTION_TEMPLATE,
                (imageServerAddress == null) ? "" : imageServerAddress,
                (imageFolderName == null) ? "" : imageFolderName,
                identifier,
                name,
                countryElement,
                regionElement);


        return Util.parseToElement(content);

    }

    @NotNull
    private Optional<Element> createKeywordsElement(@Nullable String keyword) {

        Optional<Element> keywordsOptional = Optional.empty();

        if (keyword != null && !keyword.isEmpty()) {

            Element keywordsElement = new Element("xrx:keywords", Namespace.XRX.getUri());
            Element keywordElement = new Element("xrx:keyword", Namespace.XRX.getUri());
            keywordElement.appendChild(keyword);
            keywordsElement.appendChild(keywordElement);
            keywordsOptional = Optional.of(keywordsElement);

        }

        return keywordsOptional;

    }

    @NotNull
    private MomcaResource createNewCollectionResource(@NotNull String identifier, @NotNull String name,
                                                      @NotNull String authorEmail, @Nullable Country country,
                                                      @Nullable Region region, @Nullable String imageServerAddress,
                                                      @Nullable String imageFolderName, @Nullable String keyWord,
                                                      @NotNull IdCollection id) {

        String collectionUri = String.format("%s/%s", ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(), identifier);
        String resourceName = identifier + ".cei.xml";

        Optional<Element> keywords = createKeywordsElement(keyWord);
        Element cei = createCeiElement(identifier, name, country, region, imageServerAddress, imageFolderName);

        AtomAuthor atomAuthor = new AtomAuthor(authorEmail);
        String now = momcaConnection.queryDatabase(ExistQueryFactory.getCurrentDateTime()).get(0);
        Element resourceContent = new Entry(id, atomAuthor, now, cei);

        keywords.ifPresent(element -> resourceContent.insertChild(element, 6));

        return new MomcaResource(resourceName, collectionUri, resourceContent.toXML());

    }

}
