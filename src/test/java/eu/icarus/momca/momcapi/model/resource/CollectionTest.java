package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 22/08/2015.
 */
public class CollectionTest {

    private Collection collection;
    private Country country = new Country(new CountryCode("DE"), "Deutschland");
    private IdUser creator = new IdUser("user1.testuser@dev.monasterium.net");
    private String identifier = "MedDocBulgEmp";
    private String imageFolderName = "img/collections/MedDocBulgEmp";
    private String imageServerAddress = "http://images.icar-us.eu";
    private String keyword = "Random";
    private String name = "Documents of Bulgaria";
    private Region region = new Region("DE-BW", "Baden-Württemberg");
    private ExistResource resource;
    private String xml = "<?xml version=\"1.0\"?><atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"><atom:id>tag:www.monasterium.net,2011:/collection/MedDocBulgEmp</atom:id><atom:title /><atom:published>2015-08-22T20:05:58.053+02:00</atom:published><atom:updated>2015-08-22T20:05:58.053+02:00</atom:updated><atom:author><atom:email>user1.testuser@dev.monasterium.net</atom:email></atom:author><app:control xmlns:app=\"http://www.w3.org/2007/app\"><app:draft>no</app:draft></app:control><xrx:keyword xmlns:xrx=\"http://www.monasterium.net/NS/xrx\">Random</xrx:keyword><atom:content type=\"application/xml\"><cei:cei xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:teiHeader><cei:fileDesc><cei:sourceDesc><cei:p /></cei:sourceDesc></cei:fileDesc></cei:teiHeader><cei:text type=\"collection\"><cei:front><cei:image_server_address>http://images.icar-us.eu</cei:image_server_address><cei:image_server_folder>img/collections/MedDocBulgEmp</cei:image_server_folder><cei:user_name /><cei:password /><cei:provenance abbr=\"MedDocBulgEmp\">Documents of Bulgaria<cei:country id=\"DE\">Deutschland</cei:country><cei:region id=\"DE-BW\">Baden-Württemberg</cei:region></cei:provenance><cei:publicationStmt><cei:availability n=\"ENRICH\" status=\"restricted\" /></cei:publicationStmt><cei:div type=\"preface\" /></cei:front><cei:group /></cei:text></cei:cei></atom:content></atom:entry>";

    @BeforeMethod
    public void setUp() throws Exception {

        collection = new Collection(identifier, name);
        collection.setCreator(creator.getIdentifier());
        collection.setCountry(country);
        collection.setRegion(region);
        collection.setImageServerAddress(imageServerAddress);
        collection.setImageFolderName(imageFolderName);
        collection.setKeyword(keyword);

        resource = new ExistResource(
                String.format("%s%s", identifier, ResourceType.COLLECTION.getNameSuffix()),
                String.format("%s/%s", ResourceRoot.ARCHIVAL_COLLECTIONS, identifier),
                xml);

    }

    @Test
    public void testGetCountry() throws Exception {
        assertEquals(collection.getCountry().get(), country);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(collection.getId(), new IdCollection(identifier));
    }

    @Test
    public void testGetImageFolderName() throws Exception {
        assertEquals(collection.getImageFolderName().get(), imageFolderName);
    }

    @Test
    public void testGetImageServerAddress() throws Exception {
        assertEquals(collection.getImageServerAddress().get(), imageServerAddress);
    }

    @Test
    public void testGetKeyword() throws Exception {
        assertEquals(collection.getKeyword().get(), keyword);
    }

    @Test
    public void testGetRegion() throws Exception {
        assertEquals(collection.getRegion().get(), region);
    }

    @Test
    public void testReadIdentifierFromXml() throws Exception {
        assertEquals(collection.readIdentifierFromXml(resource).get(), identifier);
    }

    @Test
    public void testReadNameFromXml() throws Exception {
        assertEquals(collection.readNameFromXml(collection).get(), name);
    }

    @Test
    public void testSetCountry() throws Exception {

    }

    @Test
    public void testSetIdentifier() throws Exception {

    }

    @Test
    public void testSetImageFolderName() throws Exception {

    }

    @Test
    public void testSetImageServerAddress() throws Exception {

    }

    @Test
    public void testSetKeyword() throws Exception {

    }

    @Test
    public void testSetRegion() throws Exception {

    }

    @Test
    public void testToDocument() throws Exception {

    }
}