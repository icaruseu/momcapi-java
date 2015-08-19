package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import eu.icarus.momca.momcapi.xml.eag.Desc;
import nu.xom.Element;
import nu.xom.Nodes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by daniel on 17.07.2015.
 */
public class Archive extends MomcaResource {

    @NotNull
    private final CountryCode countryCode;
    @NotNull
    private final Desc desc;
    @NotNull
    private final IdArchive id;
    @NotNull
    private final String identifier;
    @NotNull
    private final String name;

    public Archive(@NotNull MomcaResource momcaResource) {
        super(momcaResource);
        id = initId();

        List<String> identifierList = momcaResource.queryContentAsList(XpathQuery.QUERY_EAG_REPOSITORID);
        List<String> nameList = momcaResource.queryContentAsList(XpathQuery.QUERY_EAG_AUTFORM);
        List<String> countryCodeList = momcaResource.queryContentAsList(XpathQuery.QUERY_EAG_COUNTRYCODE);
        Nodes descNodes = momcaResource.queryContentAsNodes(XpathQuery.QUERY_EAG_DESC);

        if (identifierList.size() != 1 || nameList.size() != 1 || countryCodeList.size() != 1 || descNodes.size() != 1) {
            throw new IllegalArgumentException("The provided MomcaResource content is not valid for an archive: "
                    + momcaResource.getXmlAsDocument().toXML());
        }

        identifier = identifierList.get(0);
        name = nameList.get(0);
        countryCode = new CountryCode(countryCodeList.get(0));
        desc = new Desc((Element) descNodes.get(0));

    }

    @NotNull
    public Address getAddress() {
        return desc.getAddress();
    }

    @NotNull
    public ContactInformation getContactInformation() {
        return desc.getContactInformation();
    }

    @NotNull
    public CountryCode getCountryCode() {
        return countryCode;
    }

    @NotNull
    public IdArchive getId() {
        return id;
    }

    @NotNull
    public String getIdentifier() {
        return identifier;
    }

    @NotNull
    public String getLogoUrl() {
        return desc.getLogoUrl();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Optional<String> getRegionName() {
        return desc.getSubdivisionName().isEmpty() ? Optional.empty() : Optional.of(desc.getSubdivisionName());
    }

    @NotNull
    private IdArchive initId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", getXmlAsDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdArchive(new AtomId(idString));
        }

    }

    @NotNull
    @Override
    public String toString() {

        return "Archive{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", desc=" + desc +
                ", name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                "} " + super.toString();
    }

}
