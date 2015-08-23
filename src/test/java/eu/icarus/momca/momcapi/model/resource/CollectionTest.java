package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Created by djell on 22/08/2015.
 */
public class CollectionTest {

    private static final Country COUNTRY = new Country(new CountryCode("DE"), "Deutschland");
    private static final IdUser CREATOR = new IdUser("user1.testuser@dev.monasterium.net");
    private static final String IDENTIFIER = "MedDocBulgEmp";
    private static final String IMAGE_FOLDER_NAME = "img/collections/MedDocBulgEmp";
    private static final String IMAGE_SERVER_ADDRESS = "http://images.icar-us.eu";
    private static final String KEYWORD = "Random";
    private static final String NAME = "Documents of Bulgaria";
    private static final Region REGION = new Region("DE-BW", "Baden-Württemberg");
    private static final String XML = "<?xml version=\"1.0\"?>\n<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"><atom:id>tag:www.monasterium.net,2011:/collection/MedDocBulgEmp</atom:id><atom:title /><atom:published>2015-08-22T20:05:58.053+02:00</atom:published><atom:updated>2015-08-22T20:05:58.053+02:00</atom:updated><atom:author><atom:email>user1.testuser@dev.monasterium.net</atom:email></atom:author><app:control xmlns:app=\"http://www.w3.org/2007/app\"><app:draft>no</app:draft></app:control><xrx:keyword xmlns:xrx=\"http://www.monasterium.net/NS/xrx\">Random</xrx:keyword><atom:content type=\"application/xml\"><cei:cei xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:teiHeader><cei:fileDesc><cei:sourceDesc><cei:p /></cei:sourceDesc></cei:fileDesc></cei:teiHeader><cei:text type=\"collection\"><cei:front><cei:image_server_address>http://images.icar-us.eu</cei:image_server_address><cei:image_server_folder>img/collections/MedDocBulgEmp</cei:image_server_folder><cei:user_name /><cei:password /><cei:provenance abbr=\"MedDocBulgEmp\">Documents of Bulgaria<cei:country id=\"DE\">Deutschland</cei:country><cei:region id=\"DE-BW\">Baden-Württemberg</cei:region></cei:provenance><cei:publicationStmt><cei:availability n=\"ENRICH\" status=\"restricted\" /></cei:publicationStmt><cei:div type=\"preface\" /></cei:front><cei:group /></cei:text></cei:cei></atom:content></atom:entry>";
    private Collection collection;
    private ExistResource resource;

    @BeforeMethod
    public void setUp() throws Exception {

        collection = new Collection(IDENTIFIER, NAME);
        collection.setCreator(CREATOR.getIdentifier());
        collection.setCountry(COUNTRY);
        collection.setRegion(REGION);
        collection.setImageServerAddress(IMAGE_SERVER_ADDRESS);
        collection.setImageFolderName(IMAGE_FOLDER_NAME);
        collection.setKeyword(KEYWORD);

        resource = new ExistResource(
                String.format("%s%s", IDENTIFIER, ResourceType.COLLECTION.getNameSuffix()),
                String.format("%s/%s", ResourceRoot.ARCHIVAL_COLLECTIONS, IDENTIFIER),
                XML);

    }

    @Test
    public void testConstructor() throws Exception {

        assertEquals(collection.getName(), NAME);
        assertEquals(collection.getCountry().get(), COUNTRY);
        assertEquals(collection.getIdentifier(), IDENTIFIER);
        assertEquals(collection.getId(), new IdCollection(IDENTIFIER));
        assertEquals(collection.getImageFolderName().get(), IMAGE_FOLDER_NAME);
        assertEquals(collection.getImageServerAddress().get(), IMAGE_SERVER_ADDRESS);
        assertEquals(collection.getKeyword().get(), KEYWORD);
        assertEquals(collection.getRegion().get(), REGION);

        collection = new Collection(resource);
        assertEquals(collection.getName(), NAME);
        assertEquals(collection.getCountry().get(), COUNTRY);
        assertEquals(collection.getIdentifier(), IDENTIFIER);
        assertEquals(collection.getId(), new IdCollection(IDENTIFIER));
        assertEquals(collection.getImageFolderName().get(), IMAGE_FOLDER_NAME);
        assertEquals(collection.getImageServerAddress().get(), IMAGE_SERVER_ADDRESS);
        assertEquals(collection.getKeyword().get(), KEYWORD);
        assertEquals(collection.getRegion().get(), REGION);

        collection = new Collection(new IdCollection(IDENTIFIER), XML);
        assertEquals(collection.getName(), NAME);
        assertEquals(collection.getCountry().get(), COUNTRY);
        assertEquals(collection.getIdentifier(), IDENTIFIER);
        assertEquals(collection.getId(), new IdCollection(IDENTIFIER));
        assertEquals(collection.getImageFolderName().get(), IMAGE_FOLDER_NAME);
        assertEquals(collection.getImageServerAddress().get(), IMAGE_SERVER_ADDRESS);
        assertEquals(collection.getKeyword().get(), KEYWORD);
        assertEquals(collection.getRegion().get(), REGION);

        collection = new Collection(collection);
        assertEquals(collection.getName(), NAME);
        assertEquals(collection.getCountry().get(), COUNTRY);
        assertEquals(collection.getIdentifier(), IDENTIFIER);
        assertEquals(collection.getId(), new IdCollection(IDENTIFIER));
        assertEquals(collection.getImageFolderName().get(), IMAGE_FOLDER_NAME);
        assertEquals(collection.getImageServerAddress().get(), IMAGE_SERVER_ADDRESS);
        assertEquals(collection.getKeyword().get(), KEYWORD);
        assertEquals(collection.getRegion().get(), REGION);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithoutName() throws Exception {
        new Collection(IDENTIFIER, "");
    }

    @Test
    public void testSetCountry() throws Exception {
        Country newCountry = new Country(new CountryCode("IT"), "Italia");
        collection.setCountry(newCountry);
        assertEquals(collection.getCountry().get(), newCountry);
    }

    @Test
    public void testSetIdentifier() throws Exception {

        String newIdentifier = "newIdentifier";
        collection.setIdentifier(newIdentifier);
        assertEquals(collection.getIdentifier(), newIdentifier);
        assertEquals(collection.getId(), new IdCollection(newIdentifier));

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetIdentifierEmpty() throws Exception {
        collection.setIdentifier("");
    }

    @Test
    public void testSetImageFolderName() throws Exception {

        String newImageFolderName = "newImagefolderName";
        collection.setImageFolderName(newImageFolderName);
        assertEquals(collection.getImageFolderName().get(), newImageFolderName);
        collection.setImageFolderName("");
        assertFalse(collection.getImageFolderName().isPresent());
        collection.setImageFolderName(null);
        assertFalse(collection.getImageFolderName().isPresent());

    }

    @Test
    public void testSetImageServerAddress() throws Exception {

        String newImageServerAddress = "newImageServerAddress";
        collection.setImageServerAddress(newImageServerAddress);
        assertEquals(collection.getImageServerAddress().get(), newImageServerAddress);
        collection.setImageServerAddress("");
        assertFalse(collection.getImageServerAddress().isPresent());
        collection.setImageServerAddress(null);
        assertFalse(collection.getImageServerAddress().isPresent());

    }

    @Test
    public void testSetKeyword() throws Exception {

        String newKeyword = "newKeyword";
        collection.setKeyword(newKeyword);
        assertEquals(collection.getKeyword().get(), newKeyword);
        collection.setKeyword("");
        assertFalse(collection.getKeyword().isPresent());
        collection.setKeyword(null);
        assertFalse(collection.getKeyword().isPresent());

    }

    @Test
    public void testSetName() throws Exception {
        String newName = "New name";
        collection.setName(newName);
        assertEquals(collection.getName(), newName);
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetNameEmpty() throws Exception {
        collection.setName("");
    }

    @Test
    public void testSetRegion() throws Exception {

        Region newRegion = new Region("IT-CAM", "Campania");
        collection.setRegion(newRegion);
        assertEquals(collection.getRegion().get(), newRegion);
        collection.setRegion(null);
        assertFalse(collection.getRegion().isPresent());

    }

}