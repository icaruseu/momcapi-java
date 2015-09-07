package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.Address;
import eu.icarus.momca.momcapi.model.ContactInformation;
import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.id.IdUser;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Created by djell on 23/08/2015.
 */
public class ArchiveTest {

    private static final Address ADDRESS = new Address("Einsiedeln", "CH-8840", "Kloster Einsiedeln");
    private static final ContactInformation CONTACT_INFORMATION = new ContactInformation("http://www.klosterarchiv.ch", "0041 55 4186112", "0041 55 4186111", "archivar@klosterarchiv.ch");
    private static final Country COUNTRY = new Country(new CountryCode("CH"), "Schweiz");
    private static final IdUser CREATOR = new IdUser("archiv.data@kirche.at");
    private static final String IDENTIFIER = "CH-KAE";
    private static final String LOGO_URL = "http://example.com/image.png";
    private static final String NAME = "Klosterarchiv Einsiedeln";
    private static final String REGION_NAME = "Schwyz";
    private static final String XML = "<?xml version=\"1.0\"?>\n<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"><atom:id>tag:www.monasterium.net,2011:/archive/CH-KAE</atom:id><atom:title /><atom:published>2011-05-30T20:31:19.638+02:00</atom:published><atom:updated>2012-08-30T10:42:48.717+02:00</atom:updated><atom:author><atom:email>archiv.data@kirche.at</atom:email></atom:author><app:control xmlns:app=\"http://www.w3.org/2007/app\"><app:draft>no</app:draft></app:control><atom:content type=\"application/xml\"><eag:eag xmlns:eag=\"http://www.archivgut-online.de/eag\"><eag:archguide><eag:identity><eag:repositorid countrycode=\"CH\">CH-KAE</eag:repositorid><eag:autform>Klosterarchiv Einsiedeln</eag:autform></eag:identity><eag:desc><eag:country>Schweiz</eag:country><eag:firstdem>Schwyz</eag:firstdem><eag:street>Kloster Einsiedeln</eag:street><eag:postalcode>CH-8840</eag:postalcode><eag:municipality>Einsiedeln</eag:municipality><eag:telephone>0041 55 4186111</eag:telephone><eag:fax>0041 55 4186112</eag:fax><eag:email>archivar@klosterarchiv.ch</eag:email><eag:webpage>http://www.klosterarchiv.ch</eag:webpage><eag:repositorguides><eag:repositorguide /></eag:repositorguides><eag:extptr href=\"http://example.com/image.png\" /></eag:desc></eag:archguide></eag:eag></atom:content></atom:entry>";
    private Archive archive;
    private ExistResource resource;

    @BeforeMethod
    public void setUp() throws Exception {

        archive = new Archive(IDENTIFIER, NAME, COUNTRY);
        archive.setAddress(ADDRESS);
        archive.setContactInformation(CONTACT_INFORMATION);
        archive.setCreator(CREATOR.getIdentifier());
        archive.setLogoUrl(LOGO_URL);
        archive.setRegionName(REGION_NAME);

        resource = new ExistResource(
                String.format("%s%s", IDENTIFIER, ResourceType.ARCHIVE.getNameSuffix()),
                String.format("%s/%s", ResourceRoot.ARCHIVES.getUri(), IDENTIFIER),
                XML);

    }

    @Test
    public void testConstructor() throws Exception {

        assertEquals(archive.getAddress().get(), ADDRESS);
        assertEquals(archive.getContactInformation().get(), CONTACT_INFORMATION);
        assertEquals(archive.getCountry(), COUNTRY);
        assertEquals(archive.getCreator().get(), CREATOR);
        assertEquals(archive.getIdentifier(), IDENTIFIER);
        assertEquals(archive.getId(), new IdArchive(IDENTIFIER));
        assertEquals(archive.getLogoUrl().get(), LOGO_URL);
        assertEquals(archive.getName(), NAME);
        assertEquals(archive.getRegionName().get(), REGION_NAME);

        archive = new Archive(resource);
        assertEquals(archive.getAddress().get(), ADDRESS);
        assertEquals(archive.getContactInformation().get(), CONTACT_INFORMATION);
        assertEquals(archive.getCountry(), COUNTRY);
        assertEquals(archive.getCreator().get(), CREATOR);
        assertEquals(archive.getIdentifier(), IDENTIFIER);
        assertEquals(archive.getId(), new IdArchive(IDENTIFIER));
        assertEquals(archive.getLogoUrl().get(), LOGO_URL);
        assertEquals(archive.getName(), NAME);
        assertEquals(archive.getRegionName().get(), REGION_NAME);

        archive = new Archive(new IdArchive(IDENTIFIER), XML);
        assertEquals(archive.getAddress().get(), ADDRESS);
        assertEquals(archive.getContactInformation().get(), CONTACT_INFORMATION);
        assertEquals(archive.getCountry(), COUNTRY);
        assertEquals(archive.getCreator().get(), CREATOR);
        assertEquals(archive.getIdentifier(), IDENTIFIER);
        assertEquals(archive.getId(), new IdArchive(IDENTIFIER));
        assertEquals(archive.getLogoUrl().get(), LOGO_URL);
        assertEquals(archive.getName(), NAME);
        assertEquals(archive.getRegionName().get(), REGION_NAME);

        archive = new Archive(archive);
        assertEquals(archive.getAddress().get(), ADDRESS);
        assertEquals(archive.getContactInformation().get(), CONTACT_INFORMATION);
        assertEquals(archive.getCountry(), COUNTRY);
        assertEquals(archive.getCreator().get(), CREATOR);
        assertEquals(archive.getIdentifier(), IDENTIFIER);
        assertEquals(archive.getId(), new IdArchive(IDENTIFIER));
        assertEquals(archive.getLogoUrl().get(), LOGO_URL);
        assertEquals(archive.getName(), NAME);
        assertEquals(archive.getRegionName().get(), REGION_NAME);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithoutName() throws Exception {
        new Archive(IDENTIFIER, "", COUNTRY);
    }

    @Test
    public void testSetAddress() throws Exception {

        Address newAddress = new Address("Wien", "1010", "Schottenkloster");
        archive.setAddress(newAddress);
        assertEquals(archive.getAddress().get(), newAddress);
        archive.setAddress(null);
        assertFalse(archive.getAddress().isPresent());

    }

    @Test
    public void testSetContactInformation() throws Exception {

        ContactInformation newContactInformation = new ContactInformation("http://www.schottenkloster.at", "+430123344", "+4301233441", "archivar@schottenkloster.at");
        archive.setContactInformation(newContactInformation);
        assertEquals(archive.getContactInformation().get(), newContactInformation);
        archive.setContactInformation(null);
        assertFalse(archive.getContactInformation().isPresent());

    }

    @Test
    public void testSetCountry() throws Exception {
        Country newCountry = new Country(new CountryCode("IT"), "Italia");
        archive.setCountry(newCountry);
        assertEquals(archive.getCountry(), newCountry);
    }

    @Test
    public void testSetIdentifier() throws Exception {

        String newIdentifier = "newIdentifier";
        archive.setIdentifier(newIdentifier);
        assertEquals(archive.getIdentifier(), newIdentifier);
        assertEquals(archive.getId(), new IdArchive(newIdentifier));
        assertEquals(archive.getUri(), "/db/mom-data/metadata.archive.public/newIdentifier/newIdentifier.eag.xml");

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetIdentifierEmpty() throws Exception {
        archive.setIdentifier("");
    }

    @Test
    public void testSetLogoUrl() throws Exception {

        String newLogoUrl = "http://something/img.png";
        archive.setLogoUrl(newLogoUrl);
        assertEquals(archive.getLogoUrl().get(), newLogoUrl);
        archive.setLogoUrl("");
        assertFalse(archive.getLogoUrl().isPresent());

    }

    @Test
    public void testSetName() throws Exception {
        String newName = "New name";
        archive.setName(newName);
        assertEquals(archive.getName(), newName);
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetNameEmpty() throws Exception {
        archive.setName("");
    }

    @Test
    public void testSetRegionName() throws Exception {

        String newRegionName = "Campania";
        archive.setRegionName(newRegionName);
        assertEquals(archive.getRegionName().get(), newRegionName);
        archive.setRegionName("");
        assertFalse(archive.getRegionName().isPresent());

    }

}