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
        cm.deletePublicCharter(id, CharterStatus.PUBLIC);

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
        cm.deletePublicCharter(id, CharterStatus.PUBLIC);

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
        cm.deletePublicCharter(id, CharterStatus.PRIVATE);

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
        cm.deletePublicCharter(id, CharterStatus.IMPORTED);

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

        assertTrue(cm.deletePrivateCharter(newIdCharter, newIdUser));

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

        cm.deletePublicCharter(charter.getId(), charter.getCharterStatus());
        mc.getUserManager().deleteUser(newUser.getId());

        assertTrue(charter.toCei().toXML().contains("cei:dateRange"));

    }

    @Test
    public void testDeletePrivateCharter() throws Exception {

        IdCharter id = new IdCharter("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3", "a7e2a744-6a32-4d71-abaa-7a5f7b0e9bf5");
        User user = mc.getUserManager().getUser(new IdUser("user1.testuser@dev.monasterium.net")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        assertFalse(cm.deletePrivateCharter(new IdCharter("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3", "not-existing"), user.getId()));

        Charter charter = new Charter(id, CharterStatus.PRIVATE, user, date);
        cm.addCharter(charter);

        assertTrue(cm.deletePrivateCharter(id, user.getId()));
        assertFalse(cm.getCharter(id, CharterStatus.PRIVATE).isPresent());
        assertFalse(cm.deletePrivateCharter(id, user.getId()));

    }

    @Test
    public void testDeletePublicCharter() throws Exception {

        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "Charter1");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        Charter charter = new Charter(id, CharterStatus.IMPORTED, admin, date);
        cm.addCharter(charter);

        assertFalse(cm.deletePublicCharter(charter.getId(), CharterStatus.PUBLIC));

        assertTrue(cm.getCharter(charter.getId(), CharterStatus.IMPORTED).isPresent());

        assertTrue(cm.deletePublicCharter(charter.getId(), CharterStatus.IMPORTED));

        assertFalse(cm.getCharter(charter.getId(), CharterStatus.IMPORTED).isPresent());

        assertFalse(cm.deletePublicCharter(
                new IdCharter("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3", "425d3dba-714e-40c9-af41-7edeb12d1a25"),
                CharterStatus.PRIVATE));

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
        assertEquals(charter.get().getId().getContentAsElement().toXML(), id.getContentAsElement().toXML());

    }

    @Test
    public void testGetCharterForPublishedCharter() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_1");
        Optional<Charter> charters = cm.getCharter(id, CharterStatus.PUBLIC);
        assertTrue(charters.isPresent());
        assertEquals(charters.get().getId().getContentAsElement().toXML(), id.getContentAsElement().toXML());

    }

    @Test
    public void testGetCharterForSavedCharter() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_2");
        Optional<Charter> charter = cm.getCharter(id, CharterStatus.SAVED);
        assertTrue(charter.isPresent());
        assertEquals(charter.get().getId().getContentAsElement().toXML(), id.getContentAsElement().toXML());
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
    public void testGetCharterInstancesCharterNotExisting() throws Exception {
        IdCharter id = new IdCharter("RS-IAGNS", "Charters", "NotExisting");
        List<Charter> charters = cm.getCharterInstances(id);
        assertTrue(charters.isEmpty());
    }

    @Test
    public void testListChartersImportForCollections() throws Exception {

        IdCollection id1 = new IdCollection("MedDocBulgEmp");
        List<IdCharter> charters1 = cm.listImportedCharters(id1);
        assertEquals(charters1.size(), 37);

        IdCollection id2 = new IdCollection("AbteiEiberbach");
        List<IdCharter> charters2 = cm.listImportedCharters(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersImportForFonds() throws Exception {

        IdFond id1 = new IdFond("RS-IAGNS", "Charters");
        List<IdCharter> charters1 = cm.listImportedCharters(id1);
        assertEquals(charters1.size(), 8);

        IdFond id2 = new IdFond("CH-KASchwyz", "Urkunden");
        List<IdCharter> charters2 = cm.listImportedCharters(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersInPrivateMyCollection() throws Exception {

        IdMyCollection id1 = new IdMyCollection("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3");
        List<IdCharter> charters1 = cm.listChartersInPrivateMyCollection(id1);
        assertEquals(charters1.size(), 2);

        IdMyCollection id2 = new IdMyCollection("0d48f895-f296-485b-a6d9-e88b4523cc92");
        List<IdCharter> charters2 = cm.listChartersInPrivateMyCollection(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersPublicForCollections() throws Exception {

        IdCollection id1 = new IdCollection("AbteiEberbach");
        List<IdCharter> charters1 = cm.listPublicCharters(id1);
        assertEquals(charters1.size(), 364);

        IdCollection id2 = new IdCollection("emptycollection");
        List<IdCharter> charters2 = cm.listPublicCharters(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersPublicForFonds() throws Exception {

        IdFond id1 = new IdFond("CH-KAE", "Urkunden");
        List<IdCharter> charters1 = cm.listPublicCharters(id1);
        assertEquals(charters1.size(), 10);

        IdFond id2 = new IdFond("CH-KASchwyz", "Urkunden");
        List<IdCharter> charters2 = cm.listPublicCharters(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListChartersPublicForMyCollections() throws Exception {

        IdMyCollection id1 = new IdMyCollection("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3");
        List<IdCharter> charters1 = cm.listPublicCharters(id1);
        assertEquals(charters1.size(), 1);

        IdMyCollection id2 = new IdMyCollection("0d48f895-f296-485b-a6d9-e88b4523cc92");
        List<IdCharter> charters2 = cm.listPublicCharters(id2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testListNotExistingSavedCharters() throws Exception {

        IdUser id = new IdUser("admin");
        IdCharter erroneouslySavedCharter = new IdCharter(new AtomId("tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1"));

        List<IdCharter> erroneouslySavedCharterIds = cm.listNotExistingSavedCharters(id);
        assertEquals(erroneouslySavedCharterIds.size(), 1);
        assertEquals(erroneouslySavedCharterIds.get(0).getContentAsElement().toXML(), erroneouslySavedCharter.getContentAsElement().toXML());

    }

    @Test
    public void testListSavedCharters() throws Exception {
        assertEquals(cm.listSavedCharters().size(), 3);
    }

    @Test
    public void testListUsersPrivateCharters() throws Exception {

        IdUser user1 = new IdUser("user1.testuser@dev.monasterium.net");
        List<IdCharter> charters1 = cm.listUsersPrivateCharters(user1);
        assertEquals(charters1.size(), 2);

        IdUser user2 = new IdUser("user2.testuser@dev.monasterium.net");
        List<IdCharter> charters2 = cm.listUsersPrivateCharters(user2);
        assertEquals(charters2.size(), 0);

    }

    @Test
    public void testPublishSavedCharter() throws Exception {

        UserManager userManager = mc.getUserManager();
        User user = new User("newUser", "admin");

        IdCharter id = new IdCharter("AbteiEberbach", "Charter7");
        User admin = userManager.getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        assertFalse(cm.publishSavedCharter(user, new IdCharter("CH-KAE", "Urkunden", "KAE_Urkunde_Nr_3")));
        assertFalse(cm.publishSavedCharter(user, id));

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

        assertTrue(cm.publishSavedCharter(user, id));

        assertFalse(cm.getCharter(id, CharterStatus.SAVED).isPresent());

        Optional<Charter> published = cm.getCharter(id, CharterStatus.PUBLIC);
        user = userManager.getUser(user.getId()).get();

        userManager.deleteUser(user.getId());
        cm.deletePublicCharter(id, CharterStatus.PUBLIC);

        assertTrue(published.isPresent());
        assertEquals(published.get().getCreator().get(), user.getId());
        assertEquals(published.get().getAbstract().get().getContent(), "Abstract");
        assertTrue(user.getSavedCharters().isEmpty());

    }

    @Test
    public void testUpdateCharterContent() throws Exception {

        IdCharter idCharter = new IdCharter("CH-KAE", "Urkunden", "Charter2");
        User admin = mc.getUserManager().getUser(new IdUser("admin")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");
        CharterStatus charterStatus = CharterStatus.PUBLIC;

        Charter charter = new Charter(idCharter, charterStatus, admin, date);

        cm.addCharter(charter);

        charter.setAbstract(new Abstract("New abstract"));

        assertTrue(cm.updateCharterContent(charter));

        Optional<Charter> updated = cm.getCharter(charter.getId(), charter.getCharterStatus());

        cm.deletePublicCharter(charter.getId(), charter.getCharterStatus());

        assertTrue(updated.isPresent());
        assertEquals(updated.get().getAbstract().get().getContent(), "New abstract");

        charter.setCharterStatus(CharterStatus.PRIVATE);

        assertFalse(cm.updateCharterContent(charter));

    }

    @Test
    public void testUpdateCharterId() throws Exception {

        IdCharter originalId = new IdCharter("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3", "originalId");
        CharterStatus status = CharterStatus.PRIVATE;

        User user = mc.getUserManager().getUser(new IdUser("user1.testuser@dev.monasterium.net")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");

        assertFalse(cm.updateCharterId(originalId, originalId, status, null));

        Charter charter = new Charter(originalId, status, user, date);

        cm.addCharter(charter);

        IdCharter newId = new IdCharter("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3", "newId");

        assertFalse(cm.updateCharterId(newId, originalId, status, null));

        IdUser notExistingUser = new IdUser("notExistingUser");
        assertFalse(cm.updateCharterId(newId, originalId, status, notExistingUser));

        IdCharter notExistingOriginalId = new IdCharter("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3", "NotExistingCharter");
        assertFalse(cm.updateCharterId(newId, notExistingOriginalId, status, null));

        IdUser idUser = user.getId();

        assertTrue(cm.updateCharterId(newId, originalId, status, idUser));

        Optional<Charter> updated = cm.getCharter(newId, status);

        if (updated.isPresent()) {
            cm.deletePrivateCharter(newId, idUser);
        } else {
            cm.deletePrivateCharter(originalId, idUser);
        }

        assertTrue(updated.isPresent());
        assertEquals(updated.get().getId(), newId);

    }

    @Test
    public void testUpdateCharterNewStatus() throws Exception {

        IdCharter idCharter = new IdCharter("67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3", "newCharter");
        CharterStatus originalStatus = CharterStatus.PRIVATE;

        User user = mc.getUserManager().getUser(new IdUser("user1.testuser@dev.monasterium.net")).get();
        Date date = new Date(LocalDate.of(1413, 2, 2), 0, "2nd Februrary, 1413");
        IdUser idUser = user.getId();

        assertFalse(cm.updateCharterStatus(originalStatus, originalStatus, idCharter, idUser));

        Charter charter = new Charter(idCharter, originalStatus, user, date);

        cm.addCharter(charter);

        CharterStatus newStatus = CharterStatus.PUBLIC;

        assertFalse(cm.updateCharterStatus(newStatus, originalStatus, idCharter, null));

        IdUser notExistingUser = new IdUser("notExistingUser");
        assertFalse(cm.updateCharterStatus(newStatus, originalStatus, idCharter, notExistingUser));

        CharterStatus statusForCharterWithNotExistingHierarchy = CharterStatus.IMPORTED;
        assertFalse(cm.updateCharterStatus(statusForCharterWithNotExistingHierarchy, originalStatus, idCharter, idUser));

        assertTrue(cm.updateCharterStatus(newStatus, originalStatus, idCharter, idUser));

        Optional<Charter> updated = cm.getCharter(idCharter, newStatus);

        if (updated.isPresent()) {
            cm.deletePublicCharter(idCharter, newStatus);
        } else {
            cm.deletePrivateCharter(idCharter, idUser);
        }

        assertTrue(updated.isPresent());
        assertEquals(updated.get().getCharterStatus(), newStatus);

    }

    @Test
    public void testisExisting() throws Exception {

        assertTrue(cm.isExisting(new IdCharter("RS-IAGNS", "Charters", "F1_fasc.16_sub_N_1513"), CharterStatus.IMPORTED));

    }

}