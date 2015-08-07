package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.query.XpathQuery;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import eu.icarus.momca.momcapi.xml.eag.Address;
import eu.icarus.momca.momcapi.xml.eag.ContactInformation;
import eu.icarus.momca.momcapi.xml.eag.Desc;
import nu.xom.Element;
import nu.xom.Nodes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class Archive extends MomcaResource {

    @NotNull
    private final IdArchive atomId;
    @NotNull
    private final String countryCode;
    @NotNull
    private final Desc desc;
    @NotNull
    private final String name;
    @NotNull
    private final String shortName;

    public Archive(@NotNull MomcaResource momcaResource) {
        super(momcaResource);
        atomId = initAtomId();

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
        desc = new Desc((Element) descNodes.get(0));

    }

    @NotNull
    public Address getAddress() {
        return desc.getAddress();
    }

    @NotNull
    public IdArchive getAtomId() {
        return atomId;
    }

    @NotNull
    public ContactInformation getContactInformation() {
        return desc.getContactInformation();
    }

    @NotNull
    public String getCountryCode() {
        return countryCode;
    }

    @NotNull
    public String getCountrySubdivision() {
        return desc.getSubdivisionName();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getShortName() {
        return shortName;
    }

    private IdArchive initAtomId() {

        String idString = queryUniqueElement(XpathQuery.QUERY_ATOM_ID);

        if (idString.isEmpty()) {
            String errorMessage = String.format("No atom:id in xml content: '%s'", getXmlAsDocument().toXML());
            throw new IllegalArgumentException(errorMessage);
        } else {
            return new IdArchive(idString);
        }

    }

}
