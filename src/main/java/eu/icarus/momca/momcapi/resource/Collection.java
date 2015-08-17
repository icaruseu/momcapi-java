package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.Author;
import eu.icarus.momca.momcapi.xml.atom.IdCollection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class Collection extends MomcaResource {

    @NotNull
    private final Optional<Author> author;
    @NotNull
    private final Optional<CountryCode> countryCode;
    @NotNull
    private final IdCollection id;
    @NotNull
    private final Optional<String> imageFolderName;
    @NotNull
    private final Optional<String> imageServerAddress;
    @NotNull
    private final String name;
    @NotNull
    private final Optional<String> regionName;
    @NotNull
    private Optional<String> keyword;

    public Collection(@NotNull MomcaResource momcaResource) {

        super(momcaResource);

        countryCode = initCountryCode();
        regionName = initRegionName();
        id = initId();
        name = initName();
        author = initAuthor();
        imageFolderName = initImageFolderName();
        imageServerAddress = initImageServerAddress();
        keyword = initKeyword();

    }

    @NotNull
    public Optional<String> getAuthorName() {
        return author.map(Author::getEmail);
    }

    @NotNull
    public Optional<CountryCode> getCountryCode() {
        return countryCode;
    }

    @NotNull
    public IdCollection getId() {
        return id;
    }

    public String getIdentifier() {
        return id.getCollectionIdentifier();
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
    public Optional<String> getRegionName() {
        return regionName;
    }

    private Optional<Author> initAuthor() {

        Optional<Author> author = Optional.empty();
        String authorEmail = queryUniqueElement(XpathQuery.QUERY_ATOM_EMAIL);
        if (!authorEmail.isEmpty()) {
            author = Optional.of(new Author(authorEmail));
        }
        return author;

    }

    private Optional<CountryCode> initCountryCode() {

        Optional<CountryCode> code = Optional.empty();
        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_CEI_COUNTRY_ID);

        if (queryResults.size() == 1 && !queryResults.get(0).isEmpty()) {
            code = Optional.of(new CountryCode(queryResults.get(0)));
        }

        return code;

    }

    private IdCollection initId() {

        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_ATOM_ID);

        if (queryResults.isEmpty()) {
            throw new IllegalArgumentException("The content of the resource doesn't contain an atom:id element");
        }

        return new IdCollection(queryResults.get(0));

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
