package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.Namespace;
import eu.icarus.momca.momcapi.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.xml.atom.AtomEntry;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import eu.icarus.momca.momcapi.xml.eag.EagDesc;
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
public class Archive extends MomcaResource {

    @NotNull
    private Optional<Address> address = Optional.empty();
    @NotNull
    private Optional<ContactInformation> contactInformation = Optional.empty();
    @NotNull
    private Country country;
    @NotNull
    private Optional<IdUser> creator = Optional.empty();
    @NotNull
    private IdArchive id;
    @NotNull
    private Optional<String> logoUrl = Optional.empty();
    @NotNull
    private String name;
    @NotNull
    private Optional<String> regionName = Optional.empty();

    public Archive(@NotNull String identifier, @NotNull String name, @NotNull Country country) {

        super(new MomcaResource(
                identifier + ResourceType.ARCHIVE.getNameSuffix(),
                ResourceRoot.ARCHIVES.getUri() + "/" + identifier,
                "<empty/>"));

        setIdentifier(identifier);
        setName(name);
        setCountry(country);

    }

    public Archive(@NotNull MomcaResource momcaResource) {

        super(momcaResource);

        List<String> identifierList = momcaResource.queryContentAsList(XpathQuery.QUERY_EAG_REPOSITORID);
        List<String> nameList = momcaResource.queryContentAsList(XpathQuery.QUERY_EAG_AUTFORM);
        List<String> authorEmailList = momcaResource.queryContentAsList(XpathQuery.QUERY_ATOM_EMAIL);
        List<String> countryCodeList = momcaResource.queryContentAsList(XpathQuery.QUERY_EAG_COUNTRYCODE);
        Nodes descNodes = momcaResource.queryContentAsNodes(XpathQuery.QUERY_EAG_DESC);

        if (identifierList.size() != 1 || nameList.size() != 1 || countryCodeList.size() != 1 || descNodes.size() != 1) {
            throw new IllegalArgumentException("The provided MomcaResource content is not valid for an archive: "
                    + momcaResource.toDocument().toXML());
        }

        setIdentifier(identifierList.get(0));

        EagDesc eagDesc = new EagDesc((Element) descNodes.get(0));

        setAddress(eagDesc.getAddress());
        setContactInformation(eagDesc.getContactInformation());
        setName(nameList.get(0));
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
    public IdArchive getId() {
        return id;
    }

    @NotNull
    public String getIdentifier() {
        return id.getIdentifier();
    }

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
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;

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
    private IdArchive initId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", toDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdArchive(new AtomId(idString));
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
        Element eag = createEagElement(id.getIdentifier(), name, country.getCountryCode().getCode(), eagDesc);

        AtomAuthor author = creator.isPresent() ? creator.get().getContentXml() : new AtomAuthor("");

        return new Document(new AtomEntry(id.getContentXml(), author, now, eag));

    }

}
