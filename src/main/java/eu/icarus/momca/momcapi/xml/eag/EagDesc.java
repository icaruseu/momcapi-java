package eu.icarus.momca.momcapi.xml.eag;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.resource.Address;
import eu.icarus.momca.momcapi.resource.ContactInformation;
import nu.xom.Element;
import nu.xom.Elements;
import org.jetbrains.annotations.NotNull;

/**
 * Created by daniel on 22.07.2015.
 */
public class EagDesc extends Element {

    @NotNull
    private static final String XML_TEMPLATE =
            "<eag:desc xmlns:eag='http://www.archivgut-online.de/eag'>" +
                    "<eag:country>%s</eag:country>" +
                    "<eag:firstdem>%s</eag:firstdem>" +
                    "<eag:street>%s</eag:street>" +
                    "<eag:postalcode>%s</eag:postalcode>" +
                    "<eag:municipality>%s</eag:municipality>" +
                    "<eag:telephone>%s</eag:telephone>" +
                    "<eag:fax>%s</eag:fax>" +
                    "<eag:email>%s</eag:email>" +
                    "<eag:webpage>%s</eag:webpage>" +
                    "<eag:extptr href='%s' />" +
                    "</eag:desc>";
    @NotNull
    private Address address = new Address("", "", "");
    @NotNull
    private ContactInformation contactInformation = new ContactInformation("", "", "", "");
    @NotNull
    private String countryName = "";
    @NotNull
    private String logoUrl = "";
    @NotNull
    private String subdivisionName = "";

    public EagDesc(@NotNull Element descElement) {

        super(descElement);

        if (!getQualifiedName().equals("eag:desc")) {
            throw new IllegalArgumentException("Not a valid eag:desc element.");
        }

        initContentFromXml(descElement);

    }

    public EagDesc(@NotNull String countryName, @NotNull String subdivisionName, @NotNull Address address,
                   @NotNull ContactInformation contactInformation, @NotNull String logoUrl) {

        super(createXml(countryName, subdivisionName, address, contactInformation, logoUrl));

        this.countryName = countryName;
        this.address = address;
        this.contactInformation = contactInformation;
        this.subdivisionName = subdivisionName;
        this.logoUrl = logoUrl;

    }

    @NotNull
    private static Element createXml(@NotNull String countryName, @NotNull String subdivisionName,
                                     @NotNull Address address, @NotNull ContactInformation contactInformation,
                                     @NotNull String logoUrl) {

        String xml = String.format(
                XML_TEMPLATE,
                countryName,
                subdivisionName,
                address.getStreet(),
                address.getPostalcode(),
                address.getMunicipality(),
                contactInformation.getTelephone(),
                contactInformation.getFax(),
                contactInformation.getEmail(),
                contactInformation.getWebpage(),
                logoUrl
        );

        return Util.parseXml(xml);

    }

    @NotNull
    public Address getAddress() {
        return address;
    }

    @NotNull
    public ContactInformation getContactInformation() {
        return contactInformation;
    }

    @NotNull
    public String getCountryName() {
        return countryName;
    }

    @NotNull
    public String getLogoUrl() {
        return logoUrl;
    }

    @NotNull
    public String getSubdivisionName() {
        return subdivisionName;
    }

    private void initContentFromXml(@NotNull Element descElement) {

        String street = "";
        String postalcode = "";
        String municipality = "";
        String telephone = "";
        String fax = "";
        String email = "";
        String webpage = "";

        Elements elements = descElement.getChildElements();

        for (int i = 0; i < elements.size(); i++) {

            Element element = elements.get(i);
            switch (element.getLocalName()) {

                case "country":
                    this.countryName = element.getValue();
                    break;
                case "firstdem":
                    this.subdivisionName = element.getValue();
                    break;
                case "street":
                    street = element.getValue();
                    break;
                case "postalcode":
                    postalcode = element.getValue();
                    break;
                case "municipality":
                    municipality = element.getValue();
                    break;
                case "telephone":
                    telephone = element.getValue();
                    break;
                case "fax":
                    fax = element.getValue();
                    break;
                case "email":
                    email = element.getValue();
                    break;
                case "webpage":
                    webpage = element.getValue();
                    break;
                case "extptr":
                    this.logoUrl = element.getAttributeValue("href");
                default:
                    break;

            }

            this.address = new Address(municipality, postalcode, street);
            this.contactInformation = new ContactInformation(webpage, fax, telephone, email);

        }

    }
}
