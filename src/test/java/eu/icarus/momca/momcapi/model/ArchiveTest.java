package eu.icarus.momca.momcapi.model;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 07/08/2015.
 */
public class ArchiveTest {

    private static final String CORRECT_XML = "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"> <atom:id>tag:www.monasterium.net,2011:/archive/CH-KAE</atom:id> <atom:title /> <atom:published>2011-05-30T20:31:19.638+02:00</atom:published> <atom:updated>2012-08-30T10:42:48.717+02:00</atom:updated> <atom:author> <atom:email>archiv.data@kirche.at</atom:email> </atom:author> <app:control xmlns:app=\"http://www.w3.org/2007/app\"> <app:draft>no</app:draft> </app:control> <atom:content type=\"application/xml\"> <eag:eag xmlns:eag=\"http://www.archivgut-online.de/eag\"> <eag:archguide> <eag:identity> <eag:repositorid countrycode=\"CH\">CH-KAE</eag:repositorid> <eag:autform>Klosterarchiv Einsiedeln</eag:autform> </eag:identity> <eag:desc> <eag:country>Schweiz</eag:country> <eag:firstdem>Schwyz</eag:firstdem> <eag:street>Kloster Einsiedeln</eag:street> <eag:postalcode>CH-8840</eag:postalcode> <eag:municipality>Einsiedeln</eag:municipality> <eag:telephone>0041 55 4186111</eag:telephone> <eag:fax>0041 55 4186112</eag:fax> <eag:email>archivar@klosterarchiv.ch</eag:email> <eag:webpage>http://www.klosterarchiv.ch</eag:webpage> <eag:repositorguides> <eag:repositorguide /> </eag:repositorguides> <eag:extptr href=\"http://example.com/image.png\" /> </eag:desc> </eag:archguide> </eag:eag> </atom:content> </atom:entry>";

    private Archive correctArchive;

    @BeforeClass
    public void setUp() throws Exception {
        correctArchive = new Archive(new MomcaResource("CH-KAE.eag.xml", "/db/mom-data/metadata.archive.public/CH-KAE", CORRECT_XML));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongData() throws Exception {
        String xmlWithoutCountrycode = CORRECT_XML.replace("countrycode=\"CH\"", "");
        new Archive(new MomcaResource("CH-KAE.eag.xml", "/db/mom-data/metadata.archive.public/CH-KAE", xmlWithoutCountrycode));
    }

    @Test
    public void testGetAddress() throws Exception {
        Address correctAddress = new Address("Einsiedeln", "CH-8840", "Kloster Einsiedeln");
        assertEquals(correctArchive.getAddress(), correctAddress);
    }

    @Test
    public void testGetContactInformation() throws Exception {

        ContactInformation correctContactInformation =
                new ContactInformation(
                        "http://www.klosterarchiv.ch",
                        "0041 55 4186112",
                        "0041 55 4186111",
                        "archivar@klosterarchiv.ch");
        assertEquals(correctArchive.getContactInformation(), correctContactInformation);

    }

    @Test
    public void testGetCountryCode() throws Exception {
        assertEquals(correctArchive.getCountryCode(), new CountryCode("CH"));
    }

    @Test
    public void testGetCountryRegion() throws Exception {
        assertEquals(correctArchive.getRegionName().get(), "Schwyz");
    }

    @Test
    public void testGetId() throws Exception {
        String correctId = "tag:www.monasterium.net,2011:/archive/CH-KAE";
        assertEquals(correctArchive.getId().getContentXml().getText(), correctId);
    }

    @Test
    public void testGetLogoUrl() throws Exception {
        assertEquals(correctArchive.getLogoUrl(), "http://example.com/image.png");
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(correctArchive.getName(), "Klosterarchiv Einsiedeln");
    }

    @Test
    public void testGetShortName() throws Exception {
        assertEquals(correctArchive.getIdentifier(), "CH-KAE");
    }

}