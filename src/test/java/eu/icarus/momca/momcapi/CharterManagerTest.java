package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.Date;
import eu.icarus.momca.momcapi.model.id.*;
import eu.icarus.momca.momcapi.model.resource.Charter;
import eu.icarus.momca.momcapi.model.resource.MyCollection;
import eu.icarus.momca.momcapi.model.resource.MyCollectionStatus;
import eu.icarus.momca.momcapi.model.resource.User;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Abstract;
import eu.icarus.momca.momcapi.model.xml.xrx.Saved;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
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
        cm.deleteCharter(id, CharterStatus.PUBLIC);

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
        cm.deleteCharter(id, CharterStatus.PUBLIC);

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
        cm.deleteCharter(id, CharterStatus.PRIVATE);

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
        cm.deleteCharter(id, CharterStatus.IMPORTED);

        assertTrue(addedCharter.isPresent());
        assertEquals(addedCharter.get().getDate(), date);

    }

    @Test
    public void testAddCharter5() throws Exception {

        IdCharter id = new IdCharter("CH-", "Urkunden", "Charter1");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");


        Charter charter = new Charter(id, CharterStatus.PUBLIC, admin, date);

        assertFalse(cm.addCharter(charter));


    }

    @Test
    public void testAddCharter6() throws Exception {

        Charter savedCharter = cm.getCharter(
                new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_3"),
                CharterStatus.SAVED).get();

        IdUser newIdUser = new IdUser("admin");
        CharterStatus newStatus = CharterStatus.PRIVATE;
        IdCharter newIdCharter = new IdCharter("ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b", savedCharter.getIdentifier());

        savedCharter.setCreator(newIdUser.getIdentifier());
        savedCharter.setCharterStatus(newStatus);
        savedCharter.setId(newIdCharter);

        assertTrue(cm.addCharter(savedCharter));

        Optional<Charter> newPrivateCharter = cm.getCharter(newIdCharter, newStatus);

        cm.deleteCharter(newIdCharter, newStatus);

        assertTrue(newPrivateCharter.isPresent());

    }

    @Test
    public void testAddCharter7() throws Exception {

        User newUser = new User("newUser", "admin");
        mc.getUserManager().addUser(newUser, "password");

        IdMyCollection idMyCollection = new IdMyCollection("532ffe0f-668b-4f02-a992-35bcc660e958");
        MyCollection myCollection = new MyCollection(idMyCollection.getIdentifier(),
                "Test MyCollection", newUser.getId(), MyCollectionStatus.PRIVATE);

        mc.getMyCollectionManager().addMyCollection(myCollection);

        IdCharter id = new IdCharter(idMyCollection.getIdentifier(), "f48cd631-9b3b-46b3-b819-f1f20026594b");
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");
        Charter charter = new Charter(id, CharterStatus.PRIVATE, newUser, date);

        assertTrue(cm.addCharter(charter));

        charter = cm.getCharter(charter.getId(), charter.getCharterStatus()).get();

        cm.deleteCharter(charter.getId(), charter.getCharterStatus());
        mc.getUserManager().deleteUser(newUser.getId());

        assertTrue(charter.toCei().toXML().contains("cei:dateRange"));

    }

    @Test
    public void testDeleteCharter() throws Exception {

        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "Charter1");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        Charter charter = new Charter(id, CharterStatus.IMPORTED, admin, date);

        cm.addCharter(charter);

        assertTrue(cm.getCharter(charter.getId(), CharterStatus.IMPORTED).isPresent());

        assertFalse(cm.deleteCharter(charter.getId(), CharterStatus.PUBLIC));

        assertTrue(cm.getCharter(charter.getId(), CharterStatus.IMPORTED).isPresent());

        assertTrue(cm.deleteCharter(charter.getId(), CharterStatus.IMPORTED));

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
    public void testPublishCharter() throws Exception {

        UserManager userManager = mc.getUserManager();
        User user = new User("newUser", "admin");

        IdCharter id = new IdCharter("AbteiEberbach", "Charter7");
        User admin = userManager.getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        Charter charter = new Charter(id, CharterStatus.PUBLIC, admin, date);

        cm.addCharter(charter);

        charter.setCharterStatus(CharterStatus.SAVED);
        charter.setAbstract(new Abstract("Abstract"));
        charter.setCreator(user.getIdentifier());
        cm.addCharter(charter);

        List<Saved> savedList = new ArrayList<>(1);
        Saved saved = new Saved(id, "2015-06-27T10:42:39.179+02:00", "no");
        savedList.add(saved);
        user.setSavedCharters(savedList);

        userManager.addUser(user, "password");

        cm.publishCharter(user.getId(), id);

        assertFalse(cm.getCharter(id, CharterStatus.SAVED).isPresent());

        Optional<Charter> published = cm.getCharter(id, CharterStatus.PUBLIC);
        user = userManager.getUser(user.getId()).get();

        userManager.deleteUser(user.getId());
        cm.deleteCharter(id, CharterStatus.PUBLIC);

        assertTrue(published.isPresent());
        assertEquals(published.get().getCreator().get(), user.getId());
        assertEquals(published.get().getAbstract().get().getContent(), "Abstract");
        assertTrue(user.getSavedCharters().isEmpty());

    }

    @Test
    public void testUpdateCharter() throws Exception {

        IdCharter originalId = new IdCharter("CH-KAE", "Urkunden", "Charter2");
        CharterStatus originalStatus = CharterStatus.SAVED;
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        Charter charter = new Charter(originalId, originalStatus, admin, date);

        cm.addCharter(charter);
        assertTrue(cm.getCharter(originalId, originalStatus).isPresent());

        charter.setId(new IdCharter("ea13e5f1-03b2-4bfa-9dd5-8fb770f98d7b", "charter"));
        charter.setCharterStatus(CharterStatus.PRIVATE);

        cm.updateCharter(charter, originalId, originalStatus);

        assertFalse(cm.getCharter(originalId, originalStatus).isPresent());

        Optional<Charter> updated = cm.getCharter(charter.getId(), charter.getCharterStatus());
        cm.deleteCharter(charter.getId(), charter.getCharterStatus());

        assertTrue(updated.isPresent());

    }

    @Test
    public void testUpdateCharter1() throws Exception {

        IdCharter originalId = new IdCharter("CH-KAE", "Urkunden", "Charter2");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        CharterStatus originalStatus = CharterStatus.SAVED;
        Charter charter = new Charter(originalId, originalStatus, admin, date);

        cm.addCharter(charter);
        assertTrue(cm.getCharter(originalId, originalStatus).isPresent());

        charter.setCharterStatus(CharterStatus.PUBLIC);
        charter.setIdentifier("charter3");
        charter.setAbstract(new Abstract("abstract"));
        cm.updateCharter(charter, originalId, originalStatus);

        assertFalse(cm.getCharter(originalId, originalStatus).isPresent());

        Optional<Charter> updated = cm.getCharter(charter.getId(), charter.getCharterStatus());
        cm.deleteCharter(charter.getId(), charter.getCharterStatus());

        assertTrue(updated.isPresent());
        assertEquals(updated.get().getAbstract().get().getContent(), "abstract");

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