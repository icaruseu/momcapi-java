package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.resource.Charter;
import eu.icarus.momca.momcapi.resource.User;
import eu.icarus.momca.momcapi.resource.atom.AtomIdCharter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by daniel on 03.07.2015.
 */
public class CharterManagerTest {

    private CharterManager charterManager;
    private MomcaConnection momcaConnection;

    @BeforeClass
    public void setUp() throws Exception {
        momcaConnection = TestUtils.initMomcaConnection();
        charterManager = momcaConnection.getCharterManager();
        assertNotNull(charterManager, "MOM-CA connection not initialized.");
    }


    @Test
    public void testGetCharterInstancesForImportedCharter() throws Exception {

        AtomIdCharter id = new AtomIdCharter("RS-IAGNS", "Charters", "F1_fasc.16_sub_N_1513");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId().toXML(), id.toXML());

    }

    @Test
    public void testGetCharterInstancesForPrivateCharter() throws Exception {

        AtomIdCharter id = new AtomIdCharter("ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b", "46bc10f3-bc35-4fa8-ab82-25827dc243f6");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId().toXML(), id.toXML());

    }

    @Test
    public void testGetCharterInstancesForPublishedPrivateCharter() throws Exception {

        AtomIdCharter id = new AtomIdCharter("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3", "425d3dba-714e-40c9-af41-7edeb12d1a25");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 2);
        assertEquals(charters.get(0).getAtomId().toXML(), id.toXML());

    }

    @Test
    public void testGetCharterInstancesForSavedCharter() throws Exception {

        AtomIdCharter id = new AtomIdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_2");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 2);
        assertEquals(charters.get(0).getAtomId().toXML(), id.toXML());

    }

    @Test
    public void testGetCharterInstancesWithEncodeId() throws Exception {

        AtomIdCharter id = new AtomIdCharter("RS-IAGNS", "Charters", "IAGNS_F-.150_6605|193232"); // The | will be encoded
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId().toXML(), id.toXML());

    }

    @Test
    public void testGetGetCharterInstancesCharterNotExisting() throws Exception {
        AtomIdCharter id = new AtomIdCharter("RS-IAGNS", "Charters", "NotExisting");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertTrue(charters.isEmpty());
    }

    @Test
    public void testGetGetCharterInstancesForPublishedCharter() throws Exception {

        AtomIdCharter id = new AtomIdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_1");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId().toXML(), id.toXML());

    }

    @Test
    public void testlistErroneouslySavedCharters() throws Exception {

        UserManager um = momcaConnection.getUserManager();
        User user = um.getUser("admin").get();

        AtomIdCharter erroneouslySavedCharter = new AtomIdCharter("tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1");

        final List<AtomIdCharter> erroneouslySavedCharterIds = charterManager.listErroneouslySavedCharters(user);
        assertEquals(erroneouslySavedCharterIds.size(), 1);
        assertEquals(erroneouslySavedCharterIds.get(0).toXML(), erroneouslySavedCharter.toXML());

    }

}