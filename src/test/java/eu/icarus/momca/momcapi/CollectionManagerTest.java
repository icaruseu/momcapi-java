package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.resource.*;
import eu.icarus.momca.momcapi.xml.atom.IdCollection;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.stream.Collectors;

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
        String authorEmail = "user1.testuser@dev.monasterium.net";
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = country.getRegions().get(0);
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        Collection coll = cm.addCollection(identifier, name, authorEmail, country, region, imageServerAddress, imageFolderName, keyword);
        cm.deleteCollection(coll.getId());

        assertEquals(coll.getId().getAtomId().getText(), new IdCollection(identifier).getAtomId().getText());
        assertEquals(coll.getName(), name);
        assertEquals(coll.getAuthorName().get(), authorEmail);
        assertEquals(coll.getCountryCode().get(), country.getCountryCode());
        assertEquals(coll.getRegionName().get(), region.getNativeName());
        assertEquals(coll.getImageServerAddress().get(), imageServerAddress);
        assertEquals(coll.getImageFolderName().get(), imageFolderName);
        assertEquals(coll.getKeyword().get(), keyword);

    }

    @Test
    public void testAddCollection2() throws Exception {

        String identifier = "anothercollection";
        String name = "Another collection";
        String authorEmail = "admin";
        String imageServerAddress = "";
        String imageFolderName = "";
        String keyword = "";

        Collection coll = cm.addCollection(identifier, name, authorEmail, null, null, imageServerAddress, imageFolderName, keyword);
        cm.deleteCollection(coll.getId());

        assertEquals(coll.getId().getAtomId().getText(), new IdCollection(identifier).getAtomId().getText());
        assertEquals(coll.getName(), name);
        assertEquals(coll.getAuthorName().get(), authorEmail);
        assertFalse(coll.getCountryCode().isPresent());
        assertFalse(coll.getRegionName().isPresent());
        assertFalse(coll.getImageServerAddress().isPresent());
        assertFalse(coll.getImageFolderName().isPresent());
        assertFalse(coll.getKeyword().isPresent());

    }

    @Test
    public void testAddCollection3() throws Exception {

        String identifier = "yetanothercollection";
        String name = "Yet another collection";
        String authorEmail = "admin";

        Collection coll = cm.addCollection(identifier, name, authorEmail, null, null, null, null, null);
        cm.deleteCollection(coll.getId());

        assertEquals(coll.getId().getAtomId().getText(), new IdCollection(identifier).getAtomId().getText());
        assertEquals(coll.getName(), name);
        assertEquals(coll.getAuthorName().get(), authorEmail);
        assertFalse(coll.getCountryCode().isPresent());
        assertFalse(coll.getRegionName().isPresent());
        assertFalse(coll.getImageServerAddress().isPresent());
        assertFalse(coll.getImageFolderName().isPresent());
        assertFalse(coll.getKeyword().isPresent());

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCollectionAlreadyExisting() throws Exception {

        String identifier = "MedDocBulgEmp";
        String name = "A new collection";
        String authorEmail = "user1.testuser@dev.monasterium.net";
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = country.getRegions().get(0);
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        cm.addCollection(identifier, name, authorEmail, country, region, imageServerAddress, identifier, keyword);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCollectionWithEmptyAuthor() throws Exception {

        String identifier = "newcollection";
        String name = "New collection";
        String authorEmail = "";
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = country.getRegions().get(0);
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        cm.addCollection(identifier, name, authorEmail, country, region, imageServerAddress, identifier, keyword);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCollectionWithEmptyIdentifier() throws Exception {

        String identifier = "";
        String name = "A new collection";
        String authorEmail = "user1.testuser@dev.monasterium.net";
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = country.getRegions().get(0);
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        cm.addCollection(identifier, name, authorEmail, country, region, imageServerAddress, identifier, keyword);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCollectionWithEmptyName() throws Exception {

        String identifier = "newcollection";
        String name = "";
        String authorEmail = "user1.testuser@dev.monasterium.net";
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = country.getRegions().get(0);
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        cm.addCollection(identifier, name, authorEmail, country, region, imageServerAddress, identifier, keyword);

    }

    @Test
    public void testDeleteCollection() throws Exception {

        String identifier = "collectionToDelete";
        String name = "Collection to delete";
        String authorEmail = "admin";

        Collection coll = cm.addCollection(identifier, name, authorEmail, null, null, null, null, null);
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
        assertEquals(collection1.getCountryCode().get().getCode(), "DE");
        assertEquals(collection1.getRegionName().get(), "Bayern");
        assertEquals(collection1.getId().getAtomId().getText(), "tag:www.monasterium.net,2011:/collection/AbteiEberbach");
        assertEquals(collection1.getIdentifier(), "AbteiEberbach");
        assertEquals(collection1.getName(), "Urkundenbuch der Abtei Eberbach (Google data)");
        assertFalse(collection1.getAuthorName().isPresent());
        assertEquals(collection1.getImageServerAddress().get(), "www.mom-image.uni-koeln.de");
        assertEquals(collection1.getImageFolderName().get(), "google/Teil1/AbteiEberbach");
        assertEquals(collection1.getKeyword().get(), "Retrodigitalisierte Urkundeneditionen");

        Collection collection2 = cm.getCollection(new IdCollection("emptycollection")).get();
        assertFalse(collection2.getCountryCode().isPresent());
        assertFalse(collection2.getRegionName().isPresent());
        assertEquals(collection2.getId().getAtomId().getText(), "tag:www.monasterium.net,2011:/collection/emptycollection");
        assertEquals(collection2.getIdentifier(), "emptycollection");
        assertEquals(collection2.getName(), "Empty Collection");
        assertEquals(collection2.getAuthorName().get(), "admin");
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
        Country country = mc.getCountryManager().getCountry(new CountryCode("BG")).get();
        assertEquals(cm.listCollections(country).size(), 1);
    }

    @Test
    public void testListCollectionsForRegion() throws Exception {
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = country.getRegions().stream().filter(r -> r.getNativeName().equals("Bayern")).collect(Collectors.toList()).get(0);
        assertEquals(cm.listCollections(region).size(), 1);
    }

}