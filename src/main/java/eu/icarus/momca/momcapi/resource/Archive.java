package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import eu.icarus.momca.momcapi.xml.eag.EagDesc;
import nu.xom.Element;
import nu.xom.Nodes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by daniel on 17.07.2015.
 */
public class Archive extends MomcaResource {

    @NotNull
    private final String countryCode;
    @NotNull
    private final EagDesc eagDesc;
    @NotNull
    private final IdArchive id;
    @NotNull
    private final String name;
    @NotNull
    private final String shortName;

    public Archive(@NotNull MomcaResource momcaResource) {
        super(momcaResource);
        id = initId();

        List<String> shortNameList = momcaResource.queryContentAsList(XpathQuery.QUERY_EAG_REPOSITORID);
        List<String> nameList = momcaResource.queryContentAsList(XpathQuery.QUERY_EAG_AUTFORM);
        List<String> countryCodeList = momcaResource.queryContentAsList(XpathQuery.QUERY_EAG_COUNTRYCODE);
        Nodes descNodes = momcaResource.queryContentAsNodes(XpathQuery.QUERY_EAG_DESC);

        if (shortNameList.size() != 1 || nameList.size() != 1 || countryCodeList.size() != 1 || descNodes.size() != 1) {
            throw new IllegalArgumentException("The provided MomcaResource content is not valid for an archive: "
                    + momcaResource.getXmlAsDocument().toXML());
        }

        shortName = shortNameList.get(0);
        name = nameList.get(0);
        countryCode = countryCodeList.get(0);
        eagDesc = new EagDesc((Element) descNodes.get(0));

    }

    @NotNull
    public Address getAddress() {
        return eagDesc.getAddress();
    }

    @NotNull
    public ContactInformation getContactInformation() {
        return eagDesc.getContactInformation();
    }

    @NotNull
    public String getCountryCode() {
        return countryCode;
    }

    @NotNull
    public IdArchive getId() {
        return id;
    }

    @NotNull
    public String getLogoUrl() {
        return eagDesc.getLogoUrl();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getShortName() {
        return shortName;
    }

    @NotNull
    public String getSubdivisionNativeForm() {
        return eagDesc.getSubdivisionName();
    }

    @NotNull
    @Override
    public String toString() {

        return "Archive{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", eagDesc=" + eagDesc +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                "} " + super.toString();
    }

    @NotNull
    private IdArchive initId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", getXmlAsDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdArchive(idString);
        }

    }

}
