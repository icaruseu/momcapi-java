package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.atomid.CharterAtomId;
import eu.icarus.momca.momcapi.resource.Charter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by daniel on 03.07.2015.
 */
public class CharterManagerTest {

    private CharterManager charterManager;

    @BeforeClass
    public void setUp() throws Exception {
        MomcaConnection momcaConnection = TestUtils.initMomcaConnection();
        charterManager = momcaConnection.getCharterManager();
        assertNotNull(charterManager, "MOM-CA connection not initialized.");
    }


    @Test
    public void testGetCharterInstancesForImportedCharter() throws Exception {

        CharterAtomId id = new CharterAtomId("RS-IAGNS", "Charters", "F1_fasc.16_sub_N_1513");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testGetCharterInstancesForPrivateCharter() throws Exception {

        CharterAtomId id = new CharterAtomId("ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b", "46bc10f3-bc35-4fa8-ab82-25827dc243f6");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testGetCharterInstancesForPublishedPrivateCharter() throws Exception {

        CharterAtomId id = new CharterAtomId("f84fc6a2-85c6-4618-ab52-d0acfbcf58eb", "b94c19ed-95b2-40c6-9f0e-3f97d6e913ac");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 2);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testGetCharterInstancesForSavedCharter() throws Exception {
        CharterAtomId id = new CharterAtomId("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_2");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 2);
        assertEquals(charters.get(0).getAtomId(), id);
    }

    @Test
    public void testGetCharterInstancesWithEncodeId() throws Exception {

        CharterAtomId id = new CharterAtomId("RS-IAGNS", "Charters", "IAGNS_F-.150_6605|193232"); // The | will be encoded
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testGetGetCharterInstancesCharterNotExisting() throws Exception {
        CharterAtomId id = new CharterAtomId("RS-IAGNS", "Charters", "NotExisting");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertTrue(charters.isEmpty());
    }

    @Test
    public void testGetGetCharterInstancesForPublishedCharter() throws Exception {

        CharterAtomId id = new CharterAtomId("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_1");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId(), id);

    }

    @Test
    public void testlistErroneouslySavedCharters() throws Exception {

        String userName = "admin";
        CharterAtomId erroneouslySavedCharter = new CharterAtomId("tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1");
        final List<CharterAtomId> erroneouslySavedCharterIds = charterManager.listErroneouslySavedCharters(userName);
        assertEquals(erroneouslySavedCharterIds.size(), 1);
        assertEquals(erroneouslySavedCharterIds.get(0), erroneouslySavedCharter);

    }

}