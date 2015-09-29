package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.id.IdMyCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.MyCollection;
import eu.icarus.momca.momcapi.model.resource.MyCollectionStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Created by djell on 29/09/2015.
 */
public class MyCollectionManagerTest {

    private MomcaConnection mc;
    private MyCollectionManager mm;

    @BeforeClass
    public void setUp() throws Exception {
        mc = TestUtils.initMomcaConnection();
        mm = mc.getMyCollectionManager();
        assertNotNull(mm, "MOM-CA connection not initialized.");

    }

    @Test
    public void testAddMyCollection() throws Exception {

        MyCollection myCollection1 = new MyCollection("newCollection", "A very new collection",
                new IdUser("admin"), MyCollectionStatus.PRIVATE);

        mm.addMyCollection(myCollection1);

        MyCollection myCollection2 = new MyCollection("newCollection", "A very new collection",
                new IdUser("admin"), MyCollectionStatus.PUBLISHED);

        mm.addMyCollection(myCollection2);

        Optional<MyCollection> result = mm.getMyCollection(myCollection2.getId(), MyCollectionStatus.PUBLISHED);
        mm.deleteMyCollection(myCollection1.getId());
        mm.deleteMyCollection(myCollection2.getId());

        assertTrue(result.isPresent());

    }

    @Test
    public void testAddMyCollection1() throws Exception {

        MyCollection myCollection = new MyCollection("newCollection", "A very new collection",
                new IdUser("admin"), MyCollectionStatus.PRIVATE);
        myCollection.setPreface("Preface");

        mm.addMyCollection(myCollection);

        Optional<MyCollection> result = mm.getMyCollection(myCollection.getId(), MyCollectionStatus.PRIVATE);
        mm.deleteMyCollection(myCollection.getId());

        assertTrue(result.isPresent());

    }

    @Test(expectedExceptions = MomcaException.class)
    public void testAddMyCollection2() throws Exception {
        MyCollection myCollection = new MyCollection("newCollection", "A very new collection",
                new IdUser("admin"), MyCollectionStatus.PUBLISHED);
        mm.addMyCollection(myCollection);
    }

    @Test
    public void testDeleteMyCollection1() throws Exception {

        MyCollection myCollection1 = new MyCollection("newCollection", "A very new collection",
                new IdUser("admin"), MyCollectionStatus.PRIVATE);
        MyCollection myCollection2 = new MyCollection("newCollection", "A very new collection",
                new IdUser("admin"), MyCollectionStatus.PUBLISHED);
        mm.addMyCollection(myCollection1);
        mm.addMyCollection(myCollection2);

        mm.deleteMyCollection(myCollection1.getId());

        assertFalse(mm.getMyCollection(myCollection1.getId(), MyCollectionStatus.PRIVATE).isPresent());
        assertFalse(mm.getMyCollection(myCollection2.getId(), MyCollectionStatus.PUBLISHED).isPresent());

    }

    @Test(expectedExceptions = MomcaException.class)
    public void testDeleteMyCollection2() throws Exception {
        mm.deleteMyCollection(new IdMyCollection("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3"));
    }

    @Test
    public void testDeleteMyCollectionPublic1() throws Exception {

        MyCollection myCollectionPrivate = new MyCollection("newCollection", "A very new collection",
                new IdUser("admin"), MyCollectionStatus.PRIVATE);
        MyCollection myCollectionPublic = new MyCollection("newCollection", "A very new collection",
                new IdUser("admin"), MyCollectionStatus.PUBLISHED);
        mm.addMyCollection(myCollectionPrivate);
        mm.addMyCollection(myCollectionPublic);

        mm.deleteMyCollectionPublic(myCollectionPublic.getId());

        Optional<MyCollection> myCollectionResultPrivate = mm.getMyCollection(myCollectionPrivate.getId(), MyCollectionStatus.PRIVATE);
        Optional<MyCollection> myCollectionResultPublic = mm.getMyCollection(myCollectionPublic.getId(), MyCollectionStatus.PUBLISHED);
        mm.deleteMyCollection(myCollectionPrivate.getId());

        assertTrue(myCollectionResultPrivate.isPresent());
        assertFalse(myCollectionResultPublic.isPresent());

    }

    @Test(expectedExceptions = MomcaException.class)
    public void testDeleteMyCollectionPublic2() throws Exception {
        mm.deleteMyCollectionPublic(new IdMyCollection("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3"));
    }

    @Test
    public void testGetMyCollection() throws Exception {

        IdMyCollection id1 = new IdMyCollection("ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b");
        Optional<MyCollection> collection1 = mm.getMyCollection(id1, MyCollectionStatus.PRIVATE);
        assertTrue(collection1.isPresent());
        assertEquals(collection1.get().getStatus(), MyCollectionStatus.PRIVATE);
        assertEquals(collection1.get().getParentUri(), "/db/mom-data/xrx.user/admin/metadata.mycollection/ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b");

        collection1 = mm.getMyCollection(id1, MyCollectionStatus.PUBLISHED);
        assertFalse(collection1.isPresent());

        IdMyCollection id2 = new IdMyCollection("0d48f895-f296-485b-a6d9-e88b4523cc92");
        Optional<MyCollection> collection2 = mm.getMyCollection(id2, MyCollectionStatus.PRIVATE);
        assertTrue(collection2.isPresent());

        collection2 = mm.getMyCollection(id2, MyCollectionStatus.PUBLISHED);
        assertTrue(collection2.isPresent());

    }

    @Test
    public void testListPrivateMyCollections() throws Exception {

        IdUser idUser = new IdUser("admin");

        List<IdMyCollection> idMyCollections = mm.listPrivateMyCollections(idUser);
        assertEquals(idMyCollections.size(), 1);
        assertEquals(idMyCollections.get(0).getIdentifier(), "ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b");

    }

    @Test
    public void testListPublicMyCollections() throws Exception {
        List<IdMyCollection> idMyCollections = mm.listPublicMyCollections();
        assertEquals(idMyCollections.size(), 2);
        assertEquals(idMyCollections.get(0).getIdentifier(), "67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3");
    }

}