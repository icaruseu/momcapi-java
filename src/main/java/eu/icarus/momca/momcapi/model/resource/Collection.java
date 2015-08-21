package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.query.XpathQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class Collection extends AtomResource {

    @NotNull
    private Optional<Country> country = Optional.empty();
    @NotNull
    private Optional<String> imageFolderName = Optional.empty();
    @NotNull
    private Optional<String> imageServerAddress = Optional.empty();
    @NotNull
    private Optional<String> keyword = Optional.empty();
    @NotNull
    private Optional<String> regionName = Optional.empty();

    public Collection(@NotNull ExistResource existResource) {

        super(existResource);

        setCreator(readCreatorFromXml());
        readCountryFromXml().ifPresent(this::setCountry);
        setRegionName(readRegionNameFromXml());
        setImageFolderName(readImageFolderNameFromXml());
        setImageServerAddress(readImageServerAddressFromXml());
        setKeyword(readKeywordFromXml());

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
    public Optional<String> getRegionName() {
        return regionName;
    }

    @NotNull
    private Optional<Country> readCountryFromXml() {

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

    @NotNull
    private String readCreatorFromXml() {
        return queryUniqueElement(XpathQuery.QUERY_ATOM_EMAIL);
    }

    @Override
    @NotNull
    Optional<String> readIdentifierFromXml(ExistResource existResource) {
        List<String> identifierList = existResource.queryContentAsList(XpathQuery.QUERY_CEI_PROVENANCE_ABBR);
        return identifierList.isEmpty() ? Optional.<String>empty() : Optional.of(identifierList.get(0));
    }

    @NotNull
    private String readImageFolderNameFromXml() {
        return queryUniqueElement(XpathQuery.QUERY_CEI_IMAGE_SERVER_FOLDER);
    }

    @NotNull
    private String readImageServerAddressFromXml() {
        return queryUniqueElement(XpathQuery.QUERY_CEI_IMAGE_SERVER_ADDRESS);
    }

    @NotNull
    private String readKeywordFromXml() {
        return queryUniqueElement(XpathQuery.QUERY_XRX_KEYWORD);
    }

    @NotNull
    @Override
    Optional<String> readNameFromXml(ExistResource existResource) {

        List<String> queryResults = queryContentAsList(XpathQuery.QUERY_CEI_PROVENANCE_TEXT);

        return queryResults.isEmpty() ?
                Optional.empty() :
                Optional.of(queryResults.get(0).replaceAll("\\s+", " ")); // Normalize whitespace due to nested elements in the xml content

    }

    @NotNull
    private String readRegionNameFromXml() {
        return queryUniqueElement(XpathQuery.QUERY_CEI_REGION_TEXT);
    }

    public void setCountry(@NotNull Country country) {
        this.country = Optional.of(country);
    }

    @Override
    public void setIdentifier(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.id = new IdCollection(identifier);

    }

    public void setImageFolderName(@Nullable String imageFolderName) {

        if (imageFolderName == null || imageFolderName.isEmpty()) {
            this.imageFolderName = Optional.empty();
        } else {
            this.imageFolderName = Optional.of(imageFolderName);
        }

    }

    public void setImageServerAddress(@Nullable String imageServerAddress) {

        if (imageServerAddress == null || imageServerAddress.isEmpty()) {
            this.imageServerAddress = Optional.empty();
        } else {
            this.imageServerAddress = Optional.of(imageServerAddress);
        }
    }

    public void setKeyword(@Nullable String keyword) {

        if (keyword == null || keyword.isEmpty()) {
            this.keyword = Optional.empty();
        } else {
            this.keyword = Optional.of(keyword);
        }

    }

    public void setRegionName(@Nullable String regionName) {

        if (regionName == null || regionName.isEmpty()) {
            this.regionName = Optional.empty();
        } else {
            this.regionName = Optional.of(regionName);
        }

    }

}
