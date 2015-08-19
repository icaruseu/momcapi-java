package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.*;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by daniel on 03.07.2015.
 */
public class CharterManagerTest {

    private CharterManager cm;
    private MomcaConnection mc;

    @BeforeClass
    public void setUp() throws Exception {
        mc = TestUtils.initMomcaConnection();
        cm = mc.getCharterManager();
        assertNotNull(cm, "MOM-CA connection not initialized.");
    }

    @Test
    public void testGetCharterInstances() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_2");
        List<Charter> charters = cm.getCharterInstances(id);
        assertEquals(charters.size(), 2);

        IdCharter encodedId = new IdCharter("RS-IAGNS", "Charters", "IAGNS_F-.150_6605|193232"); // The | will be encoded
        List<Charter> encodedIdCharters = cm.getCharterInstances(encodedId);
        assertEquals(encodedIdCharters.size(), 1);

    }

    @Test
    public void testGetCharterInstancesForImportedCharter() throws Exception {

        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "F1_fasc.16_sub_N_1513");
        List<Charter> charters = cm.getCharterInstances(id, CharterStatus.IMPORTED);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getId().getContentXml().toXML(), id.getContentXml().toXML());

    }

    @Test
    public void testGetCharterInstancesForPrivateCharter() throws Exception {

        IdCharter id = new IdCharter("ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b", "46bc10f3-bc35-4fa8-ab82-25827dc243f6");
        List<Charter> charters = cm.getCharterInstances(id, CharterStatus.PRIVATE);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getId().getContentXml().toXML(), id.getContentXml().toXML());

    }

    @Test
    public void testGetCharterInstancesForSavedCharter() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_2");
        List<Charter> charters = cm.getCharterInstances(id, CharterStatus.SAVED);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getId().getContentXml().toXML(), id.getContentXml().toXML());

    }

    @Test
    public void testGetGetCharterInstancesCharterNotExisting() throws Exception {
        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "NotExisting");
        List<Charter> charters = cm.getCharterInstances(id);
        assertTrue(charters.isEmpty());
    }

    @Test
    public void testGetGetCharterInstancesForPublishedCharter() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_1");
        List<Charter> charters = cm.getCharterInstances(id, CharterStatus.PUBLIC);
        assertEquals(charters.size(), 1);
        assertEquals(charters.get(0).getId().getContentXml().toXML(), id.getContentXml().toXML());

    }

    @Test
    public void testListChartersImportForCollections() throws Exception {

        IdCollection id1 = new IdCollection("MedDocBulgEmp");
        List<IdCharter> charters1 = cm.listChartersImport(id1);
        assertEquals(charters1.size(), 37);

        IdCollection id2 = new IdCollection("AbteiEiberbach");
        List<IdCharter> charters2 = cm.listChartersImport(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersImportForFonds() throws Exception {

        IdFond id1 = new IdFond("RS-IAGNS", "Charters");
        List<IdCharter> charters1 = cm.listChartersImport(id1);
        assertEquals(charters1.size(), 8);

        IdFond id2 = new IdFond("CH-KASchwyz", "Urkunden");
        List<IdCharter> charters2 = cm.listChartersImport(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersPrivateForMyCollections() throws Exception {

        IdMyCollection id1 = new IdMyCollection("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3");
        List<IdCharter> charters1 = cm.listChartersPrivate(id1);
        assertEquals(charters1.size(), 2);

        IdMyCollection id2 = new IdMyCollection("0d48f895-f296-485b-a6d9-e88b4523cc92");
        List<IdCharter> charters2 = cm.listChartersPrivate(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersPrivateForUser() throws Exception {

        IdUser user1 = new IdUser("user1.testuser@dev.monasterium.net");
        List<IdCharter> charters1 = cm.listChartersPrivate(user1);
        assertEquals(charters1.size(), 2);

        IdUser user2 = new IdUser("user2.testuser@dev.monasterium.net");
        List<IdCharter> charters2 = cm.listChartersPrivate(user2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersPublicForCollections() throws Exception {

        IdCollection id1 = new IdCollection("AbteiEberbach");
        List<IdCharter> charters1 = cm.listChartersPublic(id1);
        assertEquals(charters1.size(), 364);

        IdCollection id2 = new IdCollection("emptycollection");
        List<IdCharter> charters2 = cm.listChartersPublic(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersPublicForFonds() throws Exception {

        IdFond id1 = new IdFond("CH-KAE", "Urkunden");
        List<IdCharter> charters1 = cm.listChartersPublic(id1);
        assertEquals(charters1.size(), 10);

        IdFond id2 = new IdFond("CH-KASchwyz", "Urkunden");
        List<IdCharter> charters2 = cm.listChartersPublic(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersPublicForMyCollections() throws Exception {

        IdMyCollection id1 = new IdMyCollection("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3");
        List<IdCharter> charters1 = cm.listChartersPublic(id1);
        assertEquals(charters1.size(), 1);

        IdMyCollection id2 = new IdMyCollection("0d48f895-f296-485b-a6d9-e88b4523cc92");
        List<IdCharter> charters2 = cm.listChartersPublic(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersSaved() throws Exception {
        assertEquals(cm.listChartersSaved().size(), 2);
    }

    @Test
    public void testlistErroneouslySavedCharters() throws Exception {

        IdUser id = new IdUser("admin");
        IdCharter erroneouslySavedCharter = new IdCharter(new AtomId("tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1"));

        List<IdCharter> erroneouslySavedCharterIds = cm.listErroneouslySavedCharters(id);
        assertEquals(erroneouslySavedCharterIds.size(), 1);
        assertEquals(erroneouslySavedCharterIds.get(0).getContentXml().toXML(), erroneouslySavedCharter.getContentXml().toXML());

    }

}