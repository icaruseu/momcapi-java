package eu.icarus.momca.momcapi.model;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Created by djell on 07/08/2015.
 */
public class ArchiveTest {

    public static final IdUser CREATOR = new IdUser("archiv.data@kirche.at");
    public static final String IDENTIFIER = "CH-KAE";
    public static final String LOGO_URL = "http://example.com/image.png";
    public static final String NAME = "Klosterarchiv Einsiedeln";
    private static final String CORRECT_XML = "<?xml version=\"1.0\"?>\n<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"><atom:id>tag:www.monasterium.net,2011:/archive/CH-KAE</atom:id><atom:title /><atom:published>2011-05-30T20:31:19.638+02:00</atom:published><atom:updated>2012-08-30T10:42:48.717+02:00</atom:updated><atom:author><atom:email>archiv.data@kirche.at</atom:email></atom:author><app:control xmlns:app=\"http://www.w3.org/2007/app\"><app:draft>no</app:draft></app:control><atom:content type=\"application/xml\"><eag:eag xmlns:eag=\"http://www.archivgut-online.de/eag\"><eag:archguide><eag:identity><eag:repositorid countrycode=\"CH\">CH-KAE</eag:repositorid><eag:autform>Klosterarchiv Einsiedeln</eag:autform></eag:identity><eag:desc><eag:country>Schweiz</eag:country><eag:firstdem>Schwyz</eag:firstdem><eag:street>Kloster Einsiedeln</eag:street><eag:postalcode>CH-8840</eag:postalcode><eag:municipality>Einsiedeln</eag:municipality><eag:telephone>0041 55 4186111</eag:telephone><eag:fax>0041 55 4186112</eag:fax><eag:email>archivar@klosterarchiv.ch</eag:email><eag:webpage>http://www.klosterarchiv.ch</eag:webpage><eag:repositorguides><eag:repositorguide /></eag:repositorguides><eag:extptr href=\"http://example.com/image.png\" /></eag:desc></eag:archguide></eag:eag></atom:content></atom:entry>";
    ContactInformation CONTACT_INFORMATION = new ContactInformation("http://www.klosterarchiv.ch", "0041 55 4186112", "0041 55 4186111", "archivar@klosterarchiv.ch");
    private Address ADDRESS = new Address("Einsiedeln", "CH-8840", "Kloster Einsiedeln");
    private Country COUNTRY = new Country(new CountryCode("CH"), "Schweiz");
    private IdArchive ID = new IdArchive(IDENTIFIER);
    private String REGION_NAME = "Schwyz";
    private Archive archive1;
    private Archive archive2;

    @BeforeMethod
    public void setUp() throws Exception {
        archive1 = new Archive(new ExistResource("CH-KAE.eag.xml", "/db/mom-data/metadata.archive.public/CH-KAE", CORRECT_XML));
        archive2 = new Archive(IDENTIFIER, NAME, COUNTRY);
    }

    @Test
    public void testGetters1() throws Exception {

        assertEquals(archive1.getId(), ID);
        assertEquals(archive1.getIdentifier(), IDENTIFIER);
        assertEquals(archive1.getName(), NAME);
        assertEquals(archive1.getCountry().getCountryCode(), COUNTRY.getCountryCode());

        assertEquals(archive1.getCreator().get(), CREATOR);
        assertEquals(archive1.getAddress().get(), ADDRESS);
        assertEquals(archive1.getContactInformation().get(), CONTACT_INFORMATION);
        assertEquals(archive1.getRegionName().get(), REGION_NAME);
        assertEquals(archive1.getLogoUrl().get(), LOGO_URL);

    }

    @Test
    public void testGetters2() throws Exception {

        assertEquals(archive2.getId(), ID);
        assertEquals(archive2.getIdentifier(), IDENTIFIER);
        assertEquals(archive2.getName(), NAME);
        assertEquals(archive2.getCountry().getCountryCode(), COUNTRY.getCountryCode());

        assertFalse(archive2.getCreator().isPresent());
        assertFalse(archive2.getAddress().isPresent());
        assertFalse(archive2.getContactInformation().isPresent());
        assertFalse(archive2.getRegionName().isPresent());
        assertFalse(archive2.getLogoUrl().isPresent());

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetIdentifierToEmpty() throws Exception {
        archive1.setIdentifier("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetNameToEmpty() throws Exception {
        archive1.setName("");
    }

    @Test
    public void testSetters1() throws Exception {

        archive1.setCreator("");
        assertFalse(archive1.getCreator().isPresent());
        archive1.setCreator(null);
        assertFalse(archive1.getCreator().isPresent());

        archive1.setAddress(new Address("", "", ""));
        assertFalse(archive1.getAddress().isPresent());
        archive1.setAddress(null);
        assertFalse(archive1.getAddress().isPresent());

        archive1.setContactInformation(new ContactInformation("", "", "", ""));
        assertFalse(archive1.getContactInformation().isPresent());
        archive1.setContactInformation(null);
        assertFalse(archive1.getContactInformation().isPresent());

        archive1.setRegionName("");
        assertFalse(archive1.getRegionName().isPresent());
        archive1.setRegionName(null);
        assertFalse(archive1.getRegionName().isPresent());

        archive1.setLogoUrl("");
        assertFalse(archive1.getLogoUrl().isPresent());
        archive1.setLogoUrl(null);
        assertFalse(archive1.getLogoUrl().isPresent());

    }

    @Test
    public void testSetters2() throws Exception {

        archive2.setCreator(CREATOR.getIdentifier());
        assertEquals(archive2.getCreator().get(), CREATOR);

        archive2.setAddress(ADDRESS);
        assertEquals(archive2.getAddress().get(), ADDRESS);

        archive2.setContactInformation(CONTACT_INFORMATION);
        assertEquals(archive2.getContactInformation().get(), CONTACT_INFORMATION);

        archive2.setRegionName(REGION_NAME);
        assertEquals(archive2.getRegionName().get(), REGION_NAME);

        archive2.setLogoUrl(LOGO_URL);
        assertEquals(archive2.getLogoUrl().get(), LOGO_URL);

    }

    @Test
    public void testSetters3() throws Exception {

        IdArchive newId = new IdArchive("NewArchive");
        String newName = "New Archive";
        Country newCountry = new Country(new CountryCode("AT"), "Österreich");

        archive1.setIdentifier(newId.getIdentifier());
        assertEquals(archive1.getId(), newId);
        assertEquals(archive1.getIdentifier(), newId.getIdentifier());

        archive1.setName(newName);
        assertEquals(archive1.getName(), newName);

        archive1.setCountry(newCountry);
        assertEquals(archive1.getCountry().getCountryCode(), newCountry.getCountryCode());

    }

}