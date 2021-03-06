package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.model.xml.atom.AtomEntry;
import eu.icarus.momca.momcapi.model.xml.xrx.Keywords;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.Document;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class Collection extends AtomResource {

    private static final String COLLECTION_TEMPLATE = "<cei:cei xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:teiHeader><cei:fileDesc><cei:sourceDesc><cei:p /></cei:sourceDesc></cei:fileDesc></cei:teiHeader><cei:text type=\"collection\"><cei:front><cei:image_server_address>%s</cei:image_server_address><cei:image_server_folder>%s</cei:image_server_folder><cei:user_name /><cei:password /><cei:provenance abbr=\"%s\">%s%s%s</cei:provenance><cei:publicationStmt><cei:availability n=\"ENRICH\" status=\"restricted\" /></cei:publicationStmt><cei:div type=\"preface\" /></cei:front><cei:group /></cei:text></cei:cei>";
    @NotNull
    String name;
    @NotNull
    private Optional<Country> country = Optional.empty();
    @NotNull
    private Optional<String> imageFolderName = Optional.empty();
    @NotNull
    private Optional<String> imageServerAddress = Optional.empty();
    @NotNull
    private Optional<String> keyword = Optional.empty();
    @NotNull
    private Optional<Region> region = Optional.empty();

    public Collection(@NotNull String identifier, @NotNull String name) {

        super(new IdCollection(identifier),
                String.format("%s/%s", ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(), identifier),
                String.format("%s%s", identifier, ResourceType.COLLECTION.getNameSuffix()));

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;

        regenerateXmlContent();

    }

    public Collection(@NotNull IdCollection id, @NotNull String xmlContent) {

        this(new ExistResource(
                String.format("%s%s", id.getIdentifier(), ResourceType.COLLECTION.getNameSuffix()),
                String.format("%s/%s", ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(), id.getIdentifier()),
                xmlContent));

    }

    public Collection(@NotNull ExistResource existResource) {

        super(existResource);

        Element xml = toDocument().getRootElement();

        this.name = Util.queryXmlForOptionalString(xml, XpathQuery.QUERY_CEI_PROVENANCE_TEXT)
                .orElseThrow(IllegalArgumentException::new).replaceAll("\\s+", " ");
        this.creator = readCreatorFromXml(xml);
        this.country = readCountryFromXml(xml);
        this.region = readRegionFromXml(xml);
        this.imageFolderName = Util.queryXmlForOptionalString(xml, XpathQuery.QUERY_CEI_IMAGE_SERVER_FOLDER);
        this.imageServerAddress = Util.queryXmlForOptionalString(xml, XpathQuery.QUERY_CEI_IMAGE_SERVER_ADDRESS);
        this.keyword = Util.queryXmlForOptionalString(xml, XpathQuery.QUERY_XRX_KEYWORD);

    }

    @NotNull
    private Element createCeiElement() {

        String countryElement = country.map(c ->
                String.format("<cei:country id=\"%s\">%s</cei:country>",
                        c.getCountryCode().getCode(),
                        c.getNativeName())).orElse("");


        String regionElement = region.map(r ->
                String.format("<cei:region %s>%s</cei:region>",
                        r.getCode().isPresent() ? String.format("id=\"%s\"", r.getCode().get()) : "",
                        r.getNativeName()))
                .orElse("");

        String ceiElement = String.format(COLLECTION_TEMPLATE,
                imageServerAddress.orElse(""),
                imageFolderName.orElse(""),
                id.getIdentifier(),
                name,
                countryElement,
                regionElement);


        return Util.parseToElement(ceiElement);

    }

    @NotNull
    public Optional<Country> getCountry() {
        return country;
    }

    @NotNull
    public IdCollection getId() {
        return (IdCollection) id;
    }

    @NotNull
    public Optional<String> getImageFolderName() {
        return imageFolderName;
    }

    @NotNull
    public Optional<String> getImageServerAddress() {
        return imageServerAddress;
    }

    @NotNull
    public Optional<String> getKeyword() {
        return keyword;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Optional<Region> getRegion() {
        return region;
    }

    @NotNull
    private Optional<Country> readCountryFromXml(@NotNull Element xml) {

        Optional<CountryCode> code = Optional.empty();
        List<String> codeResults = Util.queryXmlForList(xml, XpathQuery.QUERY_CEI_COUNTRY_ID);

        if (codeResults.size() == 1 && !codeResults.get(0).isEmpty()) {
            code = Optional.of(new CountryCode(codeResults.get(0)));
        }

        Optional<String> name = Optional.empty();
        List<String> nameResults = Util.queryXmlForList(xml, XpathQuery.QUERY_CEI_COUNTRY_TEXT);

        if (nameResults.size() == 1 && !nameResults.get(0).isEmpty()) {
            name = Optional.of(nameResults.get(0));
        }

        Optional<Country> country = Optional.empty();
        if (code.isPresent() && name.isPresent()) {
            country = Optional.of(new Country(code.get(), name.get()));
        }

        return country;

    }

    @NotNull
    private Optional<Region> readRegionFromXml(@NotNull Element xml) {

        Optional<Region> region = Optional.empty();

        String regionName = Util.queryXmlForString(xml, XpathQuery.QUERY_CEI_REGION_TEXT);
        String regionCode = Util.queryXmlForString(xml, XpathQuery.QUERY_CEI_REGION_ID);

        if (!regionName.isEmpty()) {
            region = Optional.of(new Region(regionCode, regionName));
        }

        return region;

    }

    @Override
    public void regenerateXmlContent() {

        Element cei = createCeiElement();

        String published = getPublished();
        String updated = getUpdated();

        Element resourceContent =
                new AtomEntry(
                        getId().getContentAsElement(),
                        createAtomAuthor(),
                        (published.isEmpty()) ? AtomResource.localTime() : published,
                        (updated.isEmpty()) ? AtomResource.localTime() : updated,
                        cei);

        keyword.ifPresent(s -> resourceContent.insertChild(new Keywords(s), 6));

        setXmlContent(new Document(resourceContent));

    }

    public void setCountry(@NotNull Country country) {
        this.country = Optional.of(country);
        regenerateXmlContent();
    }

    @Override
    public void setIdentifier(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.id = new IdCollection(identifier);

        setResourceName(identifier + ResourceType.COLLECTION.getNameSuffix());
        setParentUri(String.format("%s/%s", ResourceRoot.ARCHIVAL_COLLECTIONS.getUri(), identifier));

        regenerateXmlContent();

    }

    public void setImageFolderName(@Nullable String imageFolderName) {

        if (imageFolderName == null || imageFolderName.isEmpty()) {
            this.imageFolderName = Optional.empty();
        } else {
            this.imageFolderName = Optional.of(imageFolderName);
        }

        regenerateXmlContent();

    }

    public void setImageServerAddress(@Nullable String imageServerAddress) {

        if (imageServerAddress == null || imageServerAddress.isEmpty()) {
            this.imageServerAddress = Optional.empty();
        } else {
            this.imageServerAddress = Optional.of(imageServerAddress);
        }

        regenerateXmlContent();

    }

    public void setKeyword(@Nullable String keyword) {

        if (keyword == null || keyword.isEmpty()) {
            this.keyword = Optional.empty();
        } else {
            this.keyword = Optional.of(keyword);
        }

        regenerateXmlContent();

    }

    public final void setName(@NotNull String name) {

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;

        regenerateXmlContent();

    }

    public void setRegion(@Nullable Region region) {

        if (region == null) {
            this.region = Optional.empty();
        } else {
            this.region = Optional.of(region);
        }

        regenerateXmlContent();

    }

}
