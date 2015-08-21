package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class Collection extends ExistResource {

    @NotNull
    private Optional<Country> country;
    @NotNull
    private Optional<IdUser> creator;
    @NotNull
    private IdCollection id;
    @NotNull
    private Optional<String> imageFolderName;
    @NotNull
    private Optional<String> imageServerAddress;
    @NotNull
    private Optional<String> keyword;
    @NotNull
    private String name;
    @NotNull
    private Optional<String> regionName;

    public Collection(@NotNull ExistResource existResource) {

        super(existResource);

        country = initCountry();
        regionName = initRegionName();
        id = initId();
        name = initName();
        creator = initAuthor();
        imageFolderName = initImageFolderName();
        imageServerAddress = initImageServerAddress();
        keyword = initKeyword();

    }

    @NotNull
    public Optional<Country> getCountry() {
        return country;
    }

    public void setCountry(@NotNull Optional<Country> country) {
        this.country = country;
    }

    @NotNull
    public Optional<IdUser> getCreator() {
        return creator;
    }

    public void setCreator(@Nullable String creator) {

        if (creator == null || creator.isEmpty()) {
            this.creator = Optional.empty();
        } else {
            this.creator = Optional.of(new IdUser(creator));
        }

    }

    @NotNull
    public IdCollection getId() {
        return id;
    }

    public void setId(@NotNull IdCollection id) {
        this.id = id;
    }

    public String getIdentifier() {
        return id.getIdentifier();
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
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
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

    private IdCollection initId() {

        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_ATOM_ID);

        if (queryResults.isEmpty()) {
            throw new IllegalArgumentException("The content of the resource doesn't contain an atom:id element");
        }

        return new IdCollection(new AtomId(queryResults.get(0)));

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

    private String initName() {

        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_CEI_PROVENANCE_TEXT);

        if (queryResults.isEmpty()) {
            throw new IllegalArgumentException("The content of the collection doesn't contain an name.");
        }

        return queryResults.get(0).replaceAll("\\s+", " "); // Normalize whitespace due to nested elements in the xml content

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
