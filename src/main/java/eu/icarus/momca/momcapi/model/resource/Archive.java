package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.Address;
import eu.icarus.momca.momcapi.model.ContactInformation;
import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.xml.Namespace;
import eu.icarus.momca.momcapi.model.xml.atom.AtomEntry;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.model.xml.eag.EagDesc;
import eu.icarus.momca.momcapi.query.XpathQuery;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created by daniel on 17.07.2015.
 */
public class Archive extends AtomResource {

    @NotNull
    String name;
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

        super(new IdArchive(identifier),
                String.format("%s/%s", ResourceRoot.ARCHIVES.getUri(), identifier),
                String.format("%s%s", identifier, ResourceType.ARCHIVE.getNameSuffix()));

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;
        this.country = country;

        updateXmlContent();

    }

    public Archive(@NotNull IdArchive id, @NotNull String xmlContent) {

        this(new ExistResource(
                String.format("%s%s", id.getIdentifier(), ResourceType.ARCHIVE.getNameSuffix()),
                String.format("%s/%s", ResourceRoot.ARCHIVES.getUri(), id.getIdentifier()),
                xmlContent));

    }

    public Archive(@NotNull ExistResource existResource) {

        super(existResource);

        Element xml = toDocument().getRootElement();
        Nodes descNodes = Util.queryXmlToNodes(xml, XpathQuery.QUERY_EAG_DESC);
        EagDesc eagDesc = new EagDesc((Element) descNodes.get(0));

        if (descNodes.size() != 1) {
            throw new IllegalArgumentException("The provided ExistResource content is not valid for an archive: "
                    + existResource.toDocument().toXML());
        }

        this.name = Util.queryXmlToOptional(xml, XpathQuery.QUERY_EAG_AUTFORM)
                .orElseThrow(IllegalArgumentException::new);
        this.country = readCountryFromXml(xml, eagDesc).orElseThrow(IllegalArgumentException::new);
        this.regionName = readRegionNameFromXml(eagDesc);
        this.creator = readCreatorFromXml(xml);
        this.address = readAddressFromXml(eagDesc);
        this.contactInformation = readContactInformationFromXml(eagDesc);
        this.logoUrl = readLogoUrlFromXml(eagDesc);

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

    @NotNull
    public Optional<ContactInformation> getContactInformation() {
        return contactInformation;
    }

    @NotNull
    public Country getCountry() {
        return country;
    }

    @NotNull
    @Override
    public IdArchive getId() {
        return (IdArchive) id;
    }

    @NotNull
    public Optional<String> getLogoUrl() {
        return logoUrl;
    }

    @NotNull
    public final String getName() {
        return name;
    }

    @NotNull
    public Optional<String> getRegionName() {
        return regionName;
    }

    @NotNull
    private Optional<Address> readAddressFromXml(@NotNull EagDesc eagDesc) {
        Address address = eagDesc.getAddress();
        return address.isEmpty() ? Optional.empty() : Optional.of(address);
    }

    @NotNull
    private Optional<ContactInformation> readContactInformationFromXml(@NotNull EagDesc eagDesc) {
        ContactInformation contactInformation = eagDesc.getContactInformation();
        return contactInformation.isEmpty() ? Optional.empty() : Optional.of(contactInformation);
    }

    @NotNull
    private Optional<Country> readCountryFromXml(@NotNull Element xml, @NotNull EagDesc eagDesc) {

        String countryCode = Util.queryXmlToString(xml, XpathQuery.QUERY_EAG_COUNTRYCODE);
        String countryName = eagDesc.getCountryName();

        Optional<Country> country = Optional.empty();

        if (!countryCode.isEmpty() && !countryName.isEmpty()) {
            country = Optional.of(new Country(new CountryCode(countryCode), countryName));
        }

        return country;

    }

    @NotNull
    private Optional<String> readLogoUrlFromXml(@NotNull EagDesc eagDesc) {
        String logoUrl = eagDesc.getLogoUrl();
        return logoUrl.isEmpty() ? Optional.empty() : Optional.of(logoUrl);
    }

    @NotNull
    private Optional<String> readRegionNameFromXml(@NotNull EagDesc eagDesc) {
        return eagDesc.getSubdivisionName().isEmpty() ? Optional.empty() : Optional.of(eagDesc.getSubdivisionName());
    }

    public void setAddress(@Nullable Address address) {

        if (address == null || address.isEmpty()) {
            this.address = Optional.empty();
        } else {
            this.address = Optional.of(address);
        }

        updateXmlContent();

    }

    public void setContactInformation(@Nullable ContactInformation contactInformation) {

        if (contactInformation == null || contactInformation.isEmpty()) {
            this.contactInformation = Optional.empty();
        } else {
            this.contactInformation = Optional.of(contactInformation);
        }

        updateXmlContent();

    }

    public void setCountry(@NotNull Country country) {
        this.country = country;
        updateXmlContent();
    }

    @Override
    public void setIdentifier(@NotNull String identifier) {

        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("The identifier is not allowed to be an empty string.");
        }

        this.id = new IdArchive(identifier);

        setResourceName(identifier + ResourceType.ARCHIVE.getNameSuffix());
        setParentUri(String.format("%s/%s", ResourceRoot.ARCHIVES.getUri(), identifier));

        updateXmlContent();

    }

    public void setLogoUrl(@Nullable String logoUrl) {

        if (logoUrl == null || logoUrl.isEmpty()) {
            this.logoUrl = Optional.empty();
        } else {
            this.logoUrl = Optional.of(logoUrl);
        }

        updateXmlContent();

    }

    public final void setName(@NotNull String name) {

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name is not allowed to be an empty string.");
        }

        this.name = name;

        updateXmlContent();

    }

    public void setRegionName(@Nullable String regionName) {

        if (regionName == null || regionName.isEmpty()) {
            this.regionName = Optional.empty();
        } else {
            this.regionName = Optional.of(regionName);
        }

        updateXmlContent();

    }

    @Override
    void updateXmlContent() {

        String regionNativeName = regionName.orElse("");
        String logoUrlString = logoUrl.orElse("");
        Address address = this.address.orElse(new Address("", "", ""));
        ContactInformation contactInformation = this.contactInformation.orElse(new ContactInformation("", "", "", ""));

        EagDesc eagDesc = new EagDesc(country.getNativeName(), regionNativeName, address, contactInformation, logoUrlString);
        Element eag = createEagElement(id.getIdentifier(), getName(), country.getCountryCode().getCode(), eagDesc);
        AtomId id = new AtomId(getId().getContentXml().getText());

        setXmlContent(new Document(new AtomEntry(id, createAtomAuthor(), AtomResource.localTime(), eag)));

    }

}
