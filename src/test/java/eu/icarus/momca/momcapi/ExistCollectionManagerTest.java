package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.Region;
import eu.icarus.momca.momcapi.model.id.IdCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.Collection;
import eu.icarus.momca.momcapi.model.resource.ResourceRoot;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Created by djell on 11/08/2015.
 */
public class ExistCollectionManagerTest {

    private CollectionManager cm;
    private ExistMomcaConnection mc;

    @BeforeClass
    public void setUp() throws Exception {
        MomcaConnectionFactory factory = new MomcaConnectionFactory();
        mc = (ExistMomcaConnection) factory.getMomcaConnection();
        cm = mc.getCollectionManager();
        assertNotNull(cm, "MOM-CA connection not initialized.");
    }

    @Test
    public void testAddCollection() throws Exception {

        String identifier = "newcollection";
        String name = "A new collection";

        Collection collectionToAdd = new Collection(identifier, name);

        assertTrue(cm.add(collectionToAdd));

        Optional<Collection> collectionFromDbOptional = cm.get(collectionToAdd.getId());

        cm.delete(collectionToAdd.getId());

        assertTrue(collectionFromDbOptional.isPresent());
        Collection collectionFromDb = collectionFromDbOptional.get();

        assertEquals(collectionFromDb.getId(), new IdCollection(identifier));
        assertEquals(collectionFromDb.getName(), name);
        assertFalse(collectionFromDb.getCreator().isPresent());
        assertFalse(collectionFromDb.getCountry().isPresent());
        assertFalse(collectionFromDb.getRegion().isPresent());
        assertFalse(collectionFromDb.getImageServerAddress().isPresent());
        assertFalse(collectionFromDb.getImageFolderName().isPresent());
        assertFalse(collectionFromDb.getKeyword().isPresent());

        String newIdentifier = "newIdentifier";
        String newName = "New name";
        IdUser creator = new IdUser("user1.testuser@dev.monasterium.net");
        Country country = new Country(new CountryCode("DE"), "Deutschland");
        Region region = new Region("DE-BW", "Baden-Württemberg");
        String imageServerAddress = "http://images.icar-us.eu";
        String imageFolderName = "img/collections/newcollection";
        String keyword = "Random";

        collectionFromDb.setIdentifier(newIdentifier);
        collectionFromDb.setName(newName);
        collectionFromDb.setCreator(creator.getIdentifier());
        collectionFromDb.setCountry(country);
        collectionFromDb.setRegion(region);
        collectionFromDb.setImageServerAddress(imageServerAddress);
        collectionFromDb.setImageFolderName(imageFolderName);
        collectionFromDb.setKeyword(keyword);

        assertTrue(cm.add(collectionFromDb));

        Optional<Collection> changedCollectionOptional = cm.get(collectionFromDb.getId());
        cm.delete(collectionFromDb.getId());
        assertTrue(changedCollectionOptional.isPresent());
        Collection changedCollection = changedCollectionOptional.get();

        assertEquals(changedCollection.getId(), new IdCollection(newIdentifier));
        assertEquals(changedCollection.getName(), newName);
        assertEquals(changedCollection.getCreator().get(), creator);
        assertEquals(changedCollection.getCountry().get(), country);
        assertEquals(changedCollection.getRegion().get(), region);
        assertEquals(changedCollection.getImageServerAddress().get(), imageServerAddress);
        assertEquals(changedCollection.getImageFolderName().get(), imageFolderName);
        assertEquals(changedCollection.getKeyword().get(), keyword);

    }

    @Test
    public void testAddCollectionAlreadyExisting() throws Exception {
        Collection collection = new Collection("MedDocBulgEmp", "A new collection");
        assertFalse(cm.add(collection));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCollectionWithEmptyIdentifier() throws Exception {
        Collection collection = new Collection("", "A new collection");
        cm.add(collection);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCollectionWithEmptyName() throws Exception {
        Collection collection = new Collection("newCollection", "");
        cm.add(collection);
    }

    @Test
    public void testDeleteCollection() throws Exception {

        String identifier = "collectionToDelete";
        String name = "Collection to delete";
        Collection collection = new Collection(identifier, name);

        cm.add(collection);
        mc.createCollection(identifier, ResourceRoot.PUBLIC_CHARTERS.getUri()); // add charters collection to test removal

        assertTrue(cm.delete(collection.getId()));

        assertFalse(cm.get(new IdCollection(identifier)).isPresent());

        // Test if eXist collections are removed
        String collectionUri = ResourceRoot.ARCHIVAL_COLLECTIONS.getUri() + "/" + identifier;
        assertFalse(mc.readCollection(collectionUri).isPresent());
        String chartersLocationUri = ResourceRoot.PUBLIC_CHARTERS.getUri() + "/" + identifier;
        assertFalse(mc.readCollection(chartersLocationUri).isPresent());

    }

    @Test
    public void testDeleteCollectionWithExistingImportedCharters() throws Exception {
        IdCollection id = new IdCollection("MedDocBulgEmp");
        assertFalse(cm.delete(id));
    }

    @Test
    public void testDeleteCollectionWithExistingPublicCharters() throws Exception {
        IdCollection id = new IdCollection("AbteiEberbach");
        assertFalse(cm.delete(id));
    }

    @Test
    public void testGetCollection() throws Exception {

        Collection collection1 = cm.get(new IdCollection("AbteiEberbach")).get();
        assertEquals(collection1.getCountry().get(), new Country(new CountryCode("DE"), "Deutschland"));
        assertEquals(collection1.getRegion().get(), new Region("DE-NRW", "Nordrhein-Westfalen"));
        assertEquals(collection1.getId().getContentAsElement().getText(), "tag:www.monasterium.net,2011:/collection/AbteiEberbach");
        assertEquals(collection1.getIdentifier(), "AbteiEberbach");
        assertEquals(collection1.getName(), "Urkundenbuch der Abtei Eberbach (Google data)");
        assertFalse(collection1.getCreator().isPresent());
        assertEquals(collection1.getImageServerAddress().get(), "www.mom-image.uni-koeln.de");
        assertEquals(collection1.getImageFolderName().get(), "google/Teil1/AbteiEberbach");
        assertEquals(collection1.getKeyword().get(), "Retrodigitalisierte Urkundeneditionen");

        Collection collection2 = cm.get(new IdCollection("emptycollection")).get();
        assertFalse(collection2.getCountry().isPresent());
        assertFalse(collection2.getRegion().isPresent());
        assertEquals(collection2.getId().getContentAsElement().getText(), "tag:www.monasterium.net,2011:/collection/emptycollection");
        assertEquals(collection2.getIdentifier(), "emptycollection");
        assertEquals(collection2.getName(), "Empty Collection");
        assertEquals(collection2.getCreator().get().getIdentifier(), "admin");
        assertFalse(collection2.getImageServerAddress().isPresent());
        assertFalse(collection2.getImageFolderName().isPresent());
        assertFalse(collection2.getKeyword().isPresent());

    }

    @Test
    public void testIsCollectionExisting() throws Exception {
        assertTrue(cm.isExisting(new IdCollection("AbteiEberbach")));
        assertFalse(cm.isExisting(new IdCollection("NotExistingCollection")));
    }

    @Test
    public void testIsExisting() throws Exception {

        assertTrue(cm.isExisting(new IdCollection("AbteiEberbach")));
        assertFalse(cm.isExisting(new IdCollection("AbteiNotExisting")));

    }

    @Test
    public void testListCollections() throws Exception {
        assertEquals(cm.list().size(), 3);
    }

    @Test
    public void testListCollectionsForCountry() throws Exception {
        Country country = new Country(new CountryCode("BG"), "Bǎlgarija");
        assertEquals(cm.list(country).size(), 1);
    }

    @Test
    public void testListCollectionsForRegion() throws Exception {
        Region region = new Region("DE-NRW", "Nordrhein-Westfalen");
        assertEquals(cm.list(region).size(), 1);
    }

}