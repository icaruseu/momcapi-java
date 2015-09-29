package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.Date;
import eu.icarus.momca.momcapi.model.id.*;
import eu.icarus.momca.momcapi.model.resource.Charter;
import eu.icarus.momca.momcapi.model.resource.User;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    public void testAddCharter1() throws Exception {

        IdCharter id = new IdCharter("AbteiEberbach", "Charter1");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        Charter charter = new Charter(id, CharterStatus.PUBLIC, admin, date);

        cm.addCharter(charter);

        Optional<Charter> addedCharter = cm.getCharter(id, CharterStatus.PUBLIC);
        cm.delete(id, CharterStatus.PUBLIC);

        assertTrue(addedCharter.isPresent());
        assertEquals(addedCharter.get().getDate(), date);

    }

    @Test
    public void testAddCharter2() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "Charter1");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        Charter charter = new Charter(id, CharterStatus.PUBLIC, admin, date);

        cm.addCharter(charter);

        Optional<Charter> addedCharter = cm.getCharter(id, CharterStatus.PUBLIC);
        cm.delete(id, CharterStatus.PUBLIC);

        assertTrue(addedCharter.isPresent());
        assertEquals(addedCharter.get().getDate(), date);

    }

    @Test
    public void testAddCharter3() throws Exception {

        IdCharter id = new IdCharter("ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b", "Charter1");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        Charter charter = new Charter(id, CharterStatus.PRIVATE, admin, date);

        cm.addCharter(charter);

        Optional<Charter> addedCharter = cm.getCharter(id, CharterStatus.PRIVATE);
        cm.delete(id, CharterStatus.PRIVATE);

        assertTrue(addedCharter.isPresent());
        assertEquals(addedCharter.get().getDate(), date);

    }

    @Test
    public void testAddCharter4() throws Exception {

        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "Charter1");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        Charter charter = new Charter(id, CharterStatus.IMPORTED, admin, date);

        cm.addCharter(charter);

        Optional<Charter> addedCharter = cm.getCharter(id, CharterStatus.IMPORTED);
        cm.delete(id, CharterStatus.IMPORTED);

        assertTrue(addedCharter.isPresent());
        assertEquals(addedCharter.get().getDate(), date);

    }

    @Test(expectedExceptions = MomcaException.class)
    public void testAddCharter5() throws Exception {

        IdCharter id = new IdCharter("CH-", "Urkunden", "Charter1");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");


        Charter charter = new Charter(id, CharterStatus.PUBLIC, admin, date);

        cm.addCharter(charter);


    }

    @Test
    public void testDeleteCharter() throws Exception {

        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "Charter1");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        Charter charter = new Charter(id, CharterStatus.IMPORTED, admin, date);

        cm.addCharter(charter);

        assertTrue(cm.getCharter(charter.getId(), CharterStatus.IMPORTED).isPresent());

        cm.delete(charter.getId(), CharterStatus.PUBLIC);

        assertTrue(cm.getCharter(charter.getId(), CharterStatus.IMPORTED).isPresent());

        cm.delete(charter.getId(), CharterStatus.IMPORTED);

        assertFalse(cm.getCharter(charter.getId(), CharterStatus.IMPORTED).isPresent());

    }

    @Test
    public void testGetCharterForImportedCharter() throws Exception {

        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "F1_fasc.16_sub_N_1513");
        Optional<Charter> charter = cm.getCharter(id, CharterStatus.IMPORTED);
        assertTrue(charter.isPresent());

        charter.get().regenerateXmlContent();
        assertTrue(charter.get().isValidCei());
        assertEquals(charter.get().toCei().toXML(), "<cei:text xmlns:cei=\"http://www.monasterium.net/NS/cei\" type=\"charter\"><cei:front /><cei:body><cei:idno id=\"F1_fasc.16_sub_N_1513\">F1. fasc.16, sub. N 1513</cei:idno><cei:chDesc><cei:abstract>Circular of Consilium Regium Locumtenentiale Hungaricum prohibiting the collection of unprescribed duties for goods from Turkey imported or exported in Belgrade, as well as works of Paradise Jocko, requires that the cereals exported across the Danube to the Black Sea only with the permission sought to punish Vilhelm Finns who improperly 3000 mc exported grain to the army of the Kingdom of Sardinia, as well as to individual prisoners sentenced to less punishment may refer to the army.</cei:abstract><cei:issued><cei:dateRange from=\"17930101\" to=\"17931231\">1793</cei:dateRange></cei:issued><cei:witnessOrig><cei:archIdentifier><cei:arch>Historical Archive of Novi Sad</cei:arch></cei:archIdentifier><cei:figure n=\"RS-IAGNS_F1.-fasc.16,-sub.-N-1513_1\"><cei:graphic url=\"RS-IAGNS_F1.-fasc.16,-sub.-N-1513_1.jpg\" /></cei:figure><cei:figure n=\"RS-IAGNS_F1.-fasc.16,-sub.-N-1513_2\"><cei:graphic url=\"RS-IAGNS_F1.-fasc.16,-sub.-N-1513_2.jpg\" /></cei:figure><cei:figure n=\"RS-IAGNS_F1.-fasc.16,-sub.-N-1513_3\"><cei:graphic url=\"RS-IAGNS_F1.-fasc.16,-sub.-N-1513_3.jpg\" /></cei:figure></cei:witnessOrig></cei:chDesc></cei:body><cei:back /></cei:text>");

    }

    @Test
    public void testGetCharterForPrivateCharter() throws Exception {

        IdCharter id = new IdCharter("ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b", "46bc10f3-bc35-4fa8-ab82-25827dc243f6");
        Optional<Charter> charter = cm.getCharter(id, CharterStatus.PRIVATE);
        assertTrue(charter.isPresent());
        assertEquals(charter.get().getId().getContentXml().toXML(), id.getContentXml().toXML());

    }

    @Test
    public void testGetCharterForSavedCharter() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_2");
        Optional<Charter> charter = cm.getCharter(id, CharterStatus.SAVED);
        assertTrue(charter.isPresent());
        assertEquals(charter.get().getId().getContentXml().toXML(), id.getContentXml().toXML());
        assertTrue(charter.get().getAbstract().isPresent());
        assertEquals(charter.get().getAbstract().get().getContent(), "Herzog Hermann von Alamannien, Graf in Unter-Rätien, schenkt als Helfer des <cei:persName>Abtes Eberhard</cei:persName> dem Kloster Einsiedeln sein Eigentum in Gams.");

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
    public void testGetGetCharterForPublishedCharter() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_1");
        Optional<Charter> charters = cm.getCharter(id, CharterStatus.PUBLIC);
        assertTrue(charters.isPresent());
        assertEquals(charters.get().getId().getContentXml().toXML(), id.getContentXml().toXML());

    }

    @Test
    public void testGetGetCharterInstancesCharterNotExisting() throws Exception {
        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "NotExisting");
        List<Charter> charters = cm.getCharterInstances(id);
        assertTrue(charters.isEmpty());
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
        assertEquals(cm.listChartersSaved().size(), 3);
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