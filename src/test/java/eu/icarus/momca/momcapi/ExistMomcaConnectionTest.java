package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.resource.ExistResource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Created by daniel on 25.06.2015.
 */
public class ExistMomcaConnectionTest {

    private ExistMomcaConnection momcaConnection;

    @BeforeClass
    public void setUp() throws Exception {
        momcaConnection = TestUtils.initMomcaConnection();
        assertNotNull(momcaConnection, "ExistMomcaConnection connection not initialized.");
    }

    @AfterClass
    public void tearDown() throws Exception {
        momcaConnection.closeConnection();
    }

    @Test
    public void testAddCollection() throws Exception {

        String name = "te@m";
        String path = "/db";
        String uri = path + "/" + name;

        assertTrue(momcaConnection.createCollection(name, path));
        assertTrue(momcaConnection.readCollection(uri).isPresent());

    }

    @Test
    public void testDeleteCollection() throws Exception {

        String name = "te@m";
        String path = "/db";
        String uri = path + "/" + name;

        momcaConnection.createCollection(name, path);
        assertTrue(momcaConnection.deleteCollection(uri));
        assertFalse(momcaConnection.readCollection(uri).isPresent());

    }

    @Test
    public void testDeleteExistResource() throws Exception {

        ExistResource res = new ExistResource("delete@Test.xml", "/db");

        momcaConnection.writeExistResource(res);
        assertTrue(momcaConnection.deleteExistResource(res));
        assertFalse(momcaConnection.readExistResource(res.getUri()).isPresent());

    }

    @Test
    public void testIsCollectionExisting() throws Exception {

        assertTrue(momcaConnection.isCollectionExisting("/db/mom-data/xrx.user/user2.testuser@dev.monasterium.net/metadata.mycollection/0d48f895-f296-485b-a6d9-e88b4523cc92"));
        assertTrue(momcaConnection.isCollectionExisting("/db/mom-data/metadata.charter.public/CH-KAE/Urkunden"));
        assertFalse(momcaConnection.isCollectionExisting("/some/random/collection"));
    }

    @Test
    public void testIsResourceExisting() throws Exception {

        ExistResource resource1 = new ExistResource("AGNS_F.1_the_fascia_9_Sub_3499|1817.cei.xml", "/db/mom-data/metadata.charter.import/RS-IAGNS/Charters");
        assertTrue(momcaConnection.isResourceExisting(resource1.getUri()));

        ExistResource resource2 = new ExistResource("KAE_Urkunde_Nr_1.cei.xml", "/db/mom-data/metadata.charter.public/CH-KAE/Urkunden");
        assertTrue(momcaConnection.isResourceExisting(resource2.getUri()));

    }

    @Test
    public void testMakeSureCollectionPathExists() throws Exception {

        String path = "/db/path/to/collection";
        assertTrue(momcaConnection.makeSureCollectionPathExists(path));
        momcaConnection.deleteCollection("/db/path");

        assertFalse(momcaConnection.makeSureCollectionPathExists("not/absolute/path"));

    }

    @Test
    public void testReadExistResource() throws Exception {

        String uri = "/db/mom-data/xrx.user/user2.testuser@dev.monasterium.net/metadata.mycollection/0d48f895-f296-485b-a6d9-e88b4523cc92/0d48f895-f296-485b-a6d9-e88b4523cc92.mycollection.xml";
        Optional<ExistResource> resource = momcaConnection.readExistResource(uri);

        assertTrue(resource.isPresent());

    }

    @Test
    public void testStoreExistResource() throws Exception {
        ExistResource res = new ExistResource("write@Test.xml", "/db");
        assertTrue(momcaConnection.writeExistResource(res));
        assertTrue(momcaConnection.readExistResource(res.getUri()).isPresent());
        momcaConnection.deleteExistResource(res);
    }
}