package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 11/08/2015.
 */
public class CollectionManagerTest {

    private CollectionManager cm;
    private MomcaConnection mc;

    @BeforeClass
    public void setUp() throws Exception {
        mc = TestUtils.initMomcaConnection();
        cm = mc.getCollectionManager();
        assertNotNull(cm, "MOM-CA connection not initialized.");
    }

    @Test
    public void testAddCollection1() throws Exception {

        String identifier = "newcollection";
        String name = "A new collection";
        IdUser author = new IdUser("user1.testuser@dev.monasterium.net");
        Country country = new Country(new CountryCode("DE"), "Deutschland");
        Region region = new Region("DE-BW", "Baden-Württemberg");
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        Collection coll = cm.addCollection(identifier, name, author, country, region, imageServerAddress, imageFolderName, keyword);
        cm.deleteCollection(coll.getId());

        assertEquals(coll.getId().getContentXml().getText(), new IdCollection(identifier).getContentXml().getText());
        assertEquals(coll.getName(), name);
        assertEquals(coll.getCreator().get(), author);
        assertEquals(coll.getCountry().get(), country);
        assertEquals(coll.getRegionName().get(), region.getNativeName());
        assertEquals(coll.getImageServerAddress().get(), imageServerAddress);
        assertEquals(coll.getImageFolderName().get(), imageFolderName);
        assertEquals(coll.getKeyword().get(), keyword);

    }

    @Test
    public void testAddCollection2() throws Exception {

        String identifier = "anothercollection";
        String name = "Another collection";
        IdUser author = new IdUser("admin");
        String imageServerAddress = "";
        String imageFolderName = "";
        String keyword = "";

        Collection coll = cm.addCollection(identifier, name, author, null, null, imageServerAddress, imageFolderName, keyword);
        cm.deleteCollection(coll.getId());

        assertEquals(coll.getId().getContentXml().getText(), new IdCollection(identifier).getContentXml().getText());
        assertEquals(coll.getName(), name);
        assertEquals(coll.getCreator().get(), author);
        assertFalse(coll.getCountry().isPresent());
        assertFalse(coll.getRegionName().isPresent());
        assertFalse(coll.getImageServerAddress().isPresent());
        assertFalse(coll.getImageFolderName().isPresent());
        assertFalse(coll.getKeyword().isPresent());

    }

    @Test
    public void testAddCollection3() throws Exception {

        String identifier = "yetanothercollection";
        String name = "Yet another collection";
        IdUser author = new IdUser("admin");

        Collection coll = cm.addCollection(identifier, name, author, null, null, null, null, null);
        cm.deleteCollection(coll.getId());

        assertEquals(coll.getId().getContentXml().getText(), new IdCollection(identifier).getContentXml().getText());
        assertEquals(coll.getName(), name);
        assertEquals(coll.getCreator().get(), author);
        assertFalse(coll.getCountry().isPresent());
        assertFalse(coll.getRegionName().isPresent());
        assertFalse(coll.getImageServerAddress().isPresent());
        assertFalse(coll.getImageFolderName().isPresent());
        assertFalse(coll.getKeyword().isPresent());

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCollectionAlreadyExisting() throws Exception {

        String identifier = "MedDocBulgEmp";
        String name = "A new collection";
        IdUser author = new IdUser("user1.testuser@dev.monasterium.net");
        Country country = new Country(new CountryCode("DE"), "Deutschland");
        Region region = new Region("DE-BW", "Baden-Württemberg");
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        cm.addCollection(identifier, name, author, country, region, imageServerAddress, identifier, keyword);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCollectionWithEmptyAuthor() throws Exception {

        String identifier = "newcollection";
        String name = "New collection";
        IdUser author = new IdUser("");
        Country country = new Country(new CountryCode("DE"), "Deutschland");
        Region region = new Region("DE-BW", "Baden-Württemberg");
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        cm.addCollection(identifier, name, author, country, region, imageServerAddress, identifier, keyword);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCollectionWithEmptyIdentifier() throws Exception {

        String identifier = "";
        String name = "A new collection";
        IdUser author = new IdUser("user1.testuser@dev.monasterium.net");
        Country country = new Country(new CountryCode("DE"), "Deutschland");
        Region region = new Region("DE-BW", "Baden-Württemberg");
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        cm.addCollection(identifier, name, author, country, region, imageServerAddress, identifier, keyword);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCollectionWithEmptyName() throws Exception {

        String identifier = "newcollection";
        String name = "";
        IdUser author = new IdUser("user1.testuser@dev.monasterium.net");
        Country country = new Country(new CountryCode("DE"), "Deutschland");
        Region region = new Region("DE-BW", "Baden-Württemberg");
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        cm.addCollection(identifier, name, author, country, region, imageServerAddress, identifier, keyword);

    }

    @Test
    public void testDeleteCollection() throws Exception {

        String identifier = "collectionToDelete";
        String name = "Collection to delete";
        IdUser author = new IdUser("admin");

        Collection coll = cm.addCollection(identifier, name, author, null, null, null, null, null);
        mc.addCollection(identifier, ResourceRoot.PUBLIC_CHARTERS.getUri()); // add charters collection to test removal
        cm.deleteCollection(coll.getId());

        assertFalse(cm.getCollection(new IdCollection(identifier)).isPresent());

        // Test if collections are removed
        String collectionUri = ResourceRoot.ARCHIVAL_COLLECTIONS.getUri() + "/" + identifier;
        assertFalse(mc.getCollection(collectionUri).isPresent());
        String chartersLocationUri = ResourceRoot.PUBLIC_CHARTERS.getUri() + "/" + identifier;
        assertFalse(mc.getCollection(chartersLocationUri).isPresent());

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDeleteCollectionWithExistingImportedCharters() throws Exception {
        IdCollection id = new IdCollection("MedDocBulgEmp");
        cm.deleteCollection(id);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDeleteCollectionWithExistingPublicCharters() throws Exception {
        IdCollection id = new IdCollection("AbteiEberbach");
        cm.deleteCollection(id);
    }

    @Test
    public void testGetCollection() throws Exception {

        Collection collection1 = cm.getCollection(new IdCollection("AbteiEberbach")).get();
        assertEquals(collection1.getCountry().get(), new Country(new CountryCode("DE"), "Deutschland"));
        assertEquals(collection1.getRegionName().get(), "Nordrhein-Westfalen");
        assertEquals(collection1.getId().getContentXml().getText(), "tag:www.monasterium.net,2011:/collection/AbteiEberbach");
        assertEquals(collection1.getIdentifier(), "AbteiEberbach");
        assertEquals(collection1.getName(), "Urkundenbuch der Abtei Eberbach (Google data)");
        assertFalse(collection1.getCreator().isPresent());
        assertEquals(collection1.getImageServerAddress().get(), "www.mom-image.uni-koeln.de");
        assertEquals(collection1.getImageFolderName().get(), "google/Teil1/AbteiEberbach");
        assertEquals(collection1.getKeyword().get(), "Retrodigitalisierte Urkundeneditionen");

        Collection collection2 = cm.getCollection(new IdCollection("emptycollection")).get();
        assertFalse(collection2.getCountry().isPresent());
        assertFalse(collection2.getRegionName().isPresent());
        assertEquals(collection2.getId().getContentXml().getText(), "tag:www.monasterium.net,2011:/collection/emptycollection");
        assertEquals(collection2.getIdentifier(), "emptycollection");
        assertEquals(collection2.getName(), "Empty Collection");
        assertEquals(collection2.getCreator().get().getIdentifier(), "admin");
        assertFalse(collection2.getImageServerAddress().isPresent());
        assertFalse(collection2.getImageFolderName().isPresent());
        assertFalse(collection2.getKeyword().isPresent());

    }

    @Test
    public void testListCollections() throws Exception {
        assertEquals(cm.listCollections().size(), 3);
    }

    @Test
    public void testListCollectionsForCountry() throws Exception {
        Country country = new Country(new CountryCode("BG"), "Bǎlgarija");
        assertEquals(cm.listCollections(country).size(), 1);
    }

    @Test
    public void testListCollectionsForRegion() throws Exception {
        Country country = new Country(new CountryCode("DE"), "Deutschland");
        Region region = new Region("DE-NRW", "Nordrhein-Westfalen");
        assertEquals(cm.listCollections(region).size(), 1);
    }

}