package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.Address;
import eu.icarus.momca.momcapi.model.ContactInformation;
import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.model.xml.atom.AtomEntry;
import eu.icarus.momca.momcapi.model.xml.eag.EagDesc;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Created by daniel on 17.07.2015.
 */
public class Archive extends AtomResource {

    @NotNull
    private Optional<Address> address = Optional.empty();
    @NotNull
    private Optional<ContactInformation> contactInformation = Optional.empty();
    @NotNull
    private Country country;
    @NotNull
    private Optional<String> logoUrl = Optional.empty();
    @NotNull
    private Optional<String> regionName = Optional.empty();

    public Archive(@NotNull String identifier, @NotNull String name, @NotNull Country country) {

        super(identifier, name, ResourceType.ARCHIVE);
        setCountry(country);

    }

    public Archive(@NotNull ExistResource existResource) {

        super(existResource);


        List<String> authorEmailList = existResource.queryContentAsList(XpathQuery.QUERY_ATOM_EMAIL);
        List<String> countryCodeList = existResource.queryContentAsList(XpathQuery.QUERY_EAG_COUNTRYCODE);
        Nodes descNodes = existResource.queryContentAsNodes(XpathQuery.QUERY_EAG_DESC);

        if (countryCodeList.size() != 1 || descNodes.size() != 1) {
            throw new IllegalArgumentException("The provided ExistResource content is not valid for an archive: "
                    + existResource.toDocument().toXML());
        }

        EagDesc eagDesc = new EagDesc((Element) descNodes.get(0));

        setAddress(eagDesc.getAddress());
        setContactInformation(eagDesc.getContactInformation());
        setCreator(authorEmailList.isEmpty() ? "" : authorEmailList.get(0));
        setCountry(new Country(new CountryCode(countryCodeList.get(0)), eagDesc.getCountryName()));
        setRegionName(eagDesc.getSubdivisionName().isEmpty() ? "" : eagDesc.getSubdivisionName());
        setLogoUrl(eagDesc.getLogoUrl().isEmpty() ? "" : eagDesc.getLogoUrl());

    }

    @NotNull
    private Element createEagElement(@NotNull String shortName, @NotNull String archiveName,
                                     @NotNull String countrycode, @NotNull EagDesc eagDesc) {

        String eagUri = Namespace.EAG.getUri();

        Element eagEag = new Element("eag:eag", eagUri);

        Element eagArchguide = new Element("eag:archguide", eagUri);
        eagEag.appendChild(eagArchguide);

        Element eagIdentity = new Element("eag:identity", eagUri);
        eagArchguide.appendChild(eagIdentity);

        Element eagRepositorid = new Element("eag:repositorid", eagUri);
        eagRepositorid.addAttribute(new Attribute("countrycode", countrycode));
        eagRepositorid.appendChild(shortName);
        eagIdentity.appendChild(eagRepositorid);

        Element eagAutform = new Element("eag:autform", eagUri);
        eagIdentity.appendChild(eagAutform);
        eagAutform.appendChild(archiveName);

        eagArchguide.appendChild(eagDesc);

        return eagEag;

    }

    @NotNull
    public Optional<Address> getAddress() {
        return address;
    }

    public void setAddress(@Nullable Address address) {

        if (address == null ||
                (address.getStreet().isEmpty() &&
                        address.getMunicipality().isEmpty() &&
                        address.getMunicipality().isEmpty())) {

            this.address = Optional.empty();

        } else {
            this.address = Optional.of(address);
        }
    }

    @NotNull
    public Optional<ContactInformation> getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(@Nullable ContactInformation contactInformation) {

        if (contactInformation == null ||
                (contactInformation.getEmail().isEmpty() &&
                        contactInformation.getFax().isEmpty() &&
                        contactInformation.getTelephone().isEmpty() &&
                        contactInformation.getWebpage().isEmpty())) {

            this.contactInformation = Optional.empty();

        } else {
            this.contactInformation = Optional.of(contactInformation);
        }
    }

    @NotNull
    public Country getCountry() {
        return country;
    }

    public void setCountry(@NotNull Country country) {
        this.country = country;
    }

    @NotNull
    @Override
    public IdArchive getId() {
        return (IdArchive) id;
    }

    @Override
    public void setIdentifier(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.id = new IdArchive(identifier);

    }

    @NotNull
    public Optional<String> getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(@Nullable String logoUrl) {

        if (logoUrl == null || logoUrl.isEmpty()) {
            this.logoUrl = Optional.empty();
        } else {
            this.logoUrl = Optional.of(logoUrl);
        }

    }

    @NotNull
    public Optional<String> getRegionName() {
        return regionName;
    }

    public void setRegionName(@Nullable String regionName) {

        if (regionName == null || regionName.isEmpty()) {
            this.regionName = Optional.empty();
        } else {
            this.regionName = Optional.of(regionName);
        }

    }

    @NotNull
    public Document toDocument() {

        String now = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String regionNativeName = regionName.orElse("");
        String logoUrlString = logoUrl.orElse("");
        Address address = this.address.orElse(new Address("", "", ""));
        ContactInformation contactInformation = this.contactInformation.orElse(new ContactInformation("", "", "", ""));

        EagDesc eagDesc = new EagDesc(country.getNativeName(), regionNativeName, address, contactInformation, logoUrlString);
        Element eag = createEagElement(id.getIdentifier(), getName(), country.getCountryCode().getCode(), eagDesc);

        AtomAuthor author = getCreator().isPresent() ? getCreator().get().getContentXml() : new AtomAuthor("");

        return new Document(new AtomEntry(id.getContentXml(), author, now, eag));

    }

}
