package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.resource.Charter;
import eu.icarus.momca.momcapi.resource.CharterStatus;
import eu.icarus.momca.momcapi.resource.User;
import eu.icarus.momca.momcapi.xml.atom.IdCharter;
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
    public void testGetCharterInstances() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_2");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertEquals(charters.size(), 2);

        IdCharter encodedId = new IdCharter("RS-IAGNS", "Charters", "IAGNS_F-.150_6605|193232"); // The | will be encoded
        List<Charter> encodedIdCharters = charterManager.getCharterInstances(encodedId);
        assertEquals(encodedIdCharters.size(), 1);

    }

    @Test
    public void testGetCharterInstancesForImportedCharter() throws Exception {

        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "F1_fasc.16_sub_N_1513");
        List<Charter> charters = charterManager.getCharterInstances(id, CharterStatus.IMPORTED);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId().toXML(), id.toXML());

    }

    @Test
    public void testGetCharterInstancesForPrivateCharter() throws Exception {

        IdCharter id = new IdCharter("ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b", "46bc10f3-bc35-4fa8-ab82-25827dc243f6");
        List<Charter> charters = charterManager.getCharterInstances(id, CharterStatus.PRIVATE);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId().toXML(), id.toXML());

    }

    @Test
    public void testGetCharterInstancesForSavedCharter() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_2");
        List<Charter> charters = charterManager.getCharterInstances(id, CharterStatus.SAVED);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId().toXML(), id.toXML());

    }

    @Test
    public void testGetGetCharterInstancesCharterNotExisting() throws Exception {
        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "NotExisting");
        List<Charter> charters = charterManager.getCharterInstances(id);
        assertTrue(charters.isEmpty());
    }

    @Test
    public void testGetGetCharterInstancesForPublishedCharter() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_1");
        List<Charter> charters = charterManager.getCharterInstances(id, CharterStatus.PUBLIC);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getAtomId().toXML(), id.toXML());

    }

    @Test
    public void testlistErroneouslySavedCharters() throws Exception {

        UserManager um = momcaConnection.getUserManager();
        User user = um.getUser("admin").get();

        IdCharter erroneouslySavedCharter = new IdCharter("tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1");

        List<IdCharter> erroneouslySavedCharterIds = charterManager.listErroneouslySavedCharters(user);
        assertEquals(erroneouslySavedCharterIds.size(), 1);
        assertEquals(erroneouslySavedCharterIds.get(0).toXML(), erroneouslySavedCharter.toXML());

    }

}