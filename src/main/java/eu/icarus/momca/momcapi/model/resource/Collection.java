package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.query.XpathQuery;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class Collection extends AtomResource {

    @NotNull
    private Optional<Country> country;
    @NotNull
    private Optional<String> imageFolderName;
    @NotNull
    private Optional<String> imageServerAddress;
    @NotNull
    private Optional<String> keyword;
    @NotNull
    private Optional<String> regionName;

    public Collection(@NotNull ExistResource existResource) {

        super(existResource);

        country = initCountry();
        regionName = initRegionName();
        creator = initAuthor();
        imageFolderName = initImageFolderName();
        imageServerAddress = initImageServerAddress();
        keyword = initKeyword();

        Optional<String> identifierOptional = getIdentifierFromXml(existResource);
        if (!identifierOptional.isPresent()) {
            throw new IllegalArgumentException("The content of the provided eXist resource is not valid for an eXist resource.");
        }

        setIdentifier(identifierOptional.get());

    }

    @NotNull
    public Optional<Country> getCountry() {
        return country;
    }

    public void setCountry(@NotNull Optional<Country> country) {
        this.country = country;
    }

    @NotNull
    public IdCollection getId() {
        return (IdCollection) id;
    }

    public void setId(@NotNull IdCollection id) {
        this.id = id;
    }

    @Override
    public void setIdentifier(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.id = new IdCollection(identifier);

    }

    @Override
    @NotNull
    Optional<String> getIdentifierFromXml(ExistResource existResource) {
        List<String> identifierList = existResource.queryContentAsList(XpathQuery.QUERY_CEI_PROVENANCE_ABBR);
        return identifierList.isEmpty() ? Optional.<String>empty() : Optional.of(identifierList.get(0));
    }

    @NotNull
    @Override
    Optional<String> getNameFromXml(ExistResource existResource) {

        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_CEI_PROVENANCE_TEXT);

        return queryResults.isEmpty() ?
                Optional.empty() :
                Optional.of(queryResults.get(0).replaceAll("\\s+", " ")); // Normalize whitespace due to nested elements in the xml content

    }

    @NotNull
    public Optional<String> getImageFolderName() {
        return imageFolderName;
    }

    public void setImageFolderName(@NotNull Optional<String> imageFolderName) {
        this.imageFolderName = imageFolderName;
    }

    @NotNull
    public Optional<String> getImageServerAddress() {
        return imageServerAddress;
    }

    public void setImageServerAddress(@NotNull Optional<String> imageServerAddress) {
        this.imageServerAddress = imageServerAddress;
    }

    @NotNull
    public Optional<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(@NotNull Optional<String> keyword) {
        this.keyword = keyword;
    }

    @NotNull
    public Optional<String> getRegionName() {
        return regionName;
    }

    public void setRegionName(@NotNull Optional<String> regionName) {
        this.regionName = regionName;
    }

    private Optional<IdUser> initAuthor() {

        Optional<IdUser> author = Optional.empty();
        String authorEmail = queryUniqueElement(XpathQuery.QUERY_ATOM_EMAIL);
        if (!authorEmail.isEmpty()) {
            author = Optional.of(new IdUser(authorEmail));
        }
        return author;

    }

    private Optional<Country> initCountry() {

        Optional<CountryCode> code = Optional.empty();
        List<String> codeResults = queryContentAsList(XpathQuery.QUERY_CEI_COUNTRY_ID);

        if (codeResults.size() == 1 && !codeResults.get(0).isEmpty()) {
            code = Optional.of(new CountryCode(codeResults.get(0)));
        }

        Optional<String> name = Optional.empty();
        List<String> nameResults = queryContentAsList(XpathQuery.QUERY_CEI_COUNTRY_TEXT);

        if (nameResults.size() == 1 && !nameResults.get(0).isEmpty()) {
            name = Optional.of(nameResults.get(0));
        }

        Optional<Country> country = Optional.empty();
        if (code.isPresent() && name.isPresent()) {
            country = Optional.of(new Country(code.get(), name.get()));
        }

        return country;

    }

    private Optional<String> initImageFolderName() {

        Optional<String> folder = Optional.empty();
        String folderText = queryUniqueElement(XpathQuery.QUERY_CEI_IMAGE_SERVER_FOLDER);
        if (!folderText.isEmpty()) {
            folder = Optional.of(folderText);
        }
        return folder;

    }

    private Optional<String> initImageServerAddress() {

        Optional<String> url = Optional.empty();
        String urlString = queryUniqueElement(XpathQuery.QUERY_CEI_IMAGE_SERVER_ADDRESS);
        if (!urlString.isEmpty()) {
            url = Optional.of(urlString);
        }
        return url;

    }

    private Optional<String> initKeyword() {

        Optional<String> keyword = Optional.empty();
        String keywordText = queryUniqueElement(XpathQuery.QUERY_XRX_KEYWORD);
        if (!keywordText.isEmpty()) {
            keyword = Optional.of(keywordText);
        }
        return keyword;

    }

    private Optional<String> initRegionName() {

        Optional<String> name = Optional.empty();

        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_CEI_REGION_TEXT);

        if (queryResults.size() == 1 && !queryResults.get(0).isEmpty()) {
            name = Optional.of(queryResults.get(0));
        }

        return name;

    }

}
