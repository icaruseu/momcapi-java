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

    private ExistMomcaConnection mc;

    @BeforeClass
    public void setUp() throws Exception {
        MomcaConnectionFactory factory = new MomcaConnectionFactory();
        mc = (ExistMomcaConnection) factory.getMomcaConnection();
        assertNotNull(mc, "ExistMomcaConnection connection not initialized.");
    }

    @AfterClass
    public void tearDown() throws Exception {
        mc.closeConnection();
    }

    @Test
    public void testAddCollection() throws Exception {

        String name = "te@m";
        String path = "/db";
        String uri = path + "/" + name;

        assertTrue(mc.createCollection(name, path));
        assertTrue(mc.readCollection(uri).isPresent());

    }

    @Test
    public void testDeleteCollection() throws Exception {

        String name = "te@m";
        String path = "/db";
        String uri = path + "/" + name;

        mc.createCollection(name, path);
        assertTrue(mc.deleteCollection(uri));
        assertFalse(mc.readCollection(uri).isPresent());

    }

    @Test
    public void testDeleteExistResource() throws Exception {

        ExistResource res = new ExistResource("delete@Test.xml", "/db");

        mc.writeExistResource(res);
        assertTrue(mc.deleteResource(res));
        assertFalse(mc.readExistResource(res.getUri()).isPresent());

    }

    @Test
    public void testIsCollectionExisting() throws Exception {

        assertTrue(mc.isCollectionExisting("/db/mom-data/xrx.user/user2.testuser@dev.monasterium.net/metadata.mycollection/0d48f895-f296-485b-a6d9-e88b4523cc92"));
        assertTrue(mc.isCollectionExisting("/db/mom-data/metadata.charter.public/CH-KAE/Urkunden"));
        assertFalse(mc.isCollectionExisting("/some/random/collection"));
    }

    @Test
    public void testIsResourceExisting() throws Exception {

        ExistResource resource1 = new ExistResource("AGNS_F.1_the_fascia_9_Sub_3499|1817.cei.xml", "/db/mom-data/metadata.charter.import/RS-IAGNS/Charters");
        assertTrue(mc.isResourceExisting(resource1.getUri()));

        ExistResource resource2 = new ExistResource("KAE_Urkunde_Nr_1.cei.xml", "/db/mom-data/metadata.charter.public/CH-KAE/Urkunden");
        assertTrue(mc.isResourceExisting(resource2.getUri()));

    }

    @Test
    public void testMakeSureCollectionPathExists() throws Exception {

        String path = "/db/path/to/collection";
        assertTrue(mc.createCollectionPath(path));
        mc.deleteCollection("/db/path");

        assertFalse(mc.createCollectionPath("not/absolute/path"));

    }

    @Test
    public void testReadExistResource() throws Exception {

        String uri = "/db/mom-data/xrx.user/user2.testuser@dev.monasterium.net/metadata.mycollection/0d48f895-f296-485b-a6d9-e88b4523cc92/0d48f895-f296-485b-a6d9-e88b4523cc92.mycollection.xml";
        Optional<ExistResource> resource = mc.readExistResource(uri);

        assertTrue(resource.isPresent());

    }

    @Test
    public void testStoreExistResource() throws Exception {
        ExistResource res = new ExistResource("write@Test.xml", "/db");
        assertTrue(mc.writeExistResource(res));
        assertTrue(mc.readExistResource(res.getUri()).isPresent());
        mc.deleteResource(res);
    }
}