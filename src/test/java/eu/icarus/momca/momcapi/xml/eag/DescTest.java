package eu.icarus.momca.momcapi.xml.eag;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.Address;
import eu.icarus.momca.momcapi.model.ContactInformation;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 22.07.2015.
 */
public class DescTest {

    @NotNull
    private static final String CORRECT_XML = "<eag:desc xmlns:eag=\"http://www.archivgut-online.de/eag\"><eag:country>Schweiz</eag:country><eag:firstdem>Schwyz</eag:firstdem><eag:street>0041 55 4186111</eag:street><eag:postalcode>CH-8840</eag:postalcode><eag:municipality>Einsiedeln</eag:municipality><eag:telephone>0041 55 4186111</eag:telephone><eag:fax>0041 55 4186112</eag:fax><eag:email>archivar@klosterarchiv.ch</eag:email><eag:webpage>http://www.klosterarchiv.ch</eag:webpage><eag:extptr href=\"http://example.com/img.png\" /></eag:desc>";
    @NotNull
    private static final Desc EAG_DESC = new Desc(Util.parseToElement(CORRECT_XML));

    @Test
    public void testConstructorWithDetails() throws Exception {

        String countryName = "Schweiz";
        String subdivisionName = "Schwyz";
        Address address = new Address("Einsiedeln", "CH-8840", "0041 55 4186111");
        ContactInformation contactInformation = new ContactInformation("http://www.klosterarchiv.ch", "0041 55 4186112",
                "0041 55 4186111", "archivar@klosterarchiv.ch");
        String logoUrl = "http://example.com/img.png";

        Desc desc = new Desc(countryName, subdivisionName, address, contactInformation, logoUrl);

        assertEquals(desc.toXML(), CORRECT_XML);

    }

    @Test
    public void testConstructorWithXml() throws Exception {
        Desc desc = new Desc(Util.parseToElement(CORRECT_XML));
        assertEquals(desc.toXML(), CORRECT_XML);
    }

    @Test
    public void testGetAddress() throws Exception {
        assertEquals(EAG_DESC.getAddress(), new Address("Einsiedeln", "CH-8840", "0041 55 4186111"));
    }

    @Test
    public void testGetContactInformation() throws Exception {
        assertEquals(EAG_DESC.getContactInformation(), new ContactInformation("http://www.klosterarchiv.ch", "0041 55 4186112",
                "0041 55 4186111", "archivar@klosterarchiv.ch"));
    }

    @Test
    public void testGetCountryName() throws Exception {
        assertEquals(EAG_DESC.getCountryName(), "Schweiz");
    }

    @Test
    public void testGetLogoUrl() throws Exception {
        assertEquals(EAG_DESC.getLogoUrl(), "http://example.com/img.png");
    }

    @Test
    public void testGetSubdivisionName() throws Exception {
        assertEquals(EAG_DESC.getSubdivisionName(), "Schwyz");
    }
}