package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Created by djell on 09/08/2015.
 */
public class FondManagerTest {

    private FondManager fm;
    private MomcaConnection mc;

    @BeforeClass
    public void setUp() throws Exception {
        mc = TestUtils.initMomcaConnection();
        fm = mc.getFondManager();
        assertNotNull(fm, "MOM-CA connection not initialized.");
    }

    @Test
    public void testAddFond() throws Exception {

        Archive archive = mc.getArchiveManager().getArchive(new IdArchive("DE-SAMuenchen")).get();

        Fond fond1 = fm.addFond(new IdUser("admin"), archive, "Urkunden", "Alle Urkunden", null,
                new URL("http://ex.com/img"), null);
        assertTrue(mc.getExistResource("Urkunden.preferences.xml", "/db/mom-data/metadata.fond.public/DE-SAMuenchen/Urkunden").isPresent());
        fm.deleteFond(fond1.getId());

        assertEquals(fond1.getId().getContentXml().getText(), "tag:www.monasterium.net,2011:/fond/DE-SAMuenchen/Urkunden");
        assertEquals(fond1.getIdentifier(), "Urkunden");
        assertEquals(fond1.getArchiveId().getContentXml().getText(), "tag:www.monasterium.net,2011:/archive/DE-SAMuenchen");
        assertEquals(fond1.getName(), "Alle Urkunden");
        assertEquals(fond1.getImageAccess().get(), ImageAccess.FREE);
        assertEquals(fond1.getImagesUrl().get().toExternalForm(), "http://ex.com/img");
        assertFalse(fond1.getDummyImageUrl().isPresent());

        Fond fond2 = fm.addFond(new IdUser("admin"), archive, "Urkunden1", "Andere Urkunden", ImageAccess.RESTRICTED,
                null, new URL("http://ex.com/dummy.png"));
        fm.deleteFond(fond2.getId());

        assertEquals(fond2.getImageAccess().get(), ImageAccess.RESTRICTED);
        assertFalse(fond2.getImagesUrl().isPresent());
        assertEquals(fond2.getDummyImageUrl().get().toExternalForm(), "http://ex.com/dummy.png");

        Fond fond3 = fm.addFond(new IdUser("admin"), archive, "Urkunden2", "Noch andere Urkunden", null,
                null, null);
        assertFalse(mc.getExistResource("Urkunden2.preferences.xml", "/db/mom-data/metadata.fond.public/DE-SAMuenchen/Urkunden2").isPresent());
        fm.deleteFond(fond3.getId());

        assertFalse(fond3.getImageAccess().isPresent());
        assertFalse(fond3.getImagesUrl().isPresent());
        assertFalse(fond3.getDummyImageUrl().isPresent());

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFondExisting() throws Exception {
        Archive archive = mc.getArchiveManager().getArchive(new IdArchive("CH-KAE")).get();
        fm.addFond(new IdUser("admin"), archive, "Urkunden", "Urkunden (0947-1483)", ImageAccess.FREE,
                new URL("http://www.klosterarchiv.ch/urkunden/urkunden-3000"), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFondNoIdentifier() throws Exception {
        Archive archive = mc.getArchiveManager().getArchive(new IdArchive("CH-KAE")).get();
        fm.addFond(new IdUser("admin"), archive, "", "Weitere Urkunden", ImageAccess.FREE,
                new URL("http://www.klosterarchiv.ch/urkunden/urkunden-3000"), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFondNoName() throws Exception {
        Archive archive = mc.getArchiveManager().getArchive(new IdArchive("CH-KAE")).get();
        fm.addFond(new IdUser("admin"), archive, "Urkunden2", "", ImageAccess.FREE,
                new URL("http://www.klosterarchiv.ch/urkunden/urkunden-3000"), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddFondNonexistentAuthor() throws Exception {
        Archive archive = mc.getArchiveManager().getArchive(new IdArchive("CH-KAE")).get();
        fm.addFond(new IdUser("someAuthor"), archive, "Urkunden2", "Weitere Urkunden", ImageAccess.FREE,
                new URL("http://www.klosterarchiv.ch/urkunden/urkunden-3000"), null);
    }

    @Test
    public void testDeleteFond() throws Exception {

        Archive archive = mc.getArchiveManager().getArchive(new IdArchive("DE-SAMuenchen")).get();

        Fond fond = fm.addFond(new IdUser("admin"), archive, "MUrkunden", "Mehrere Urkunden", null,
                new URL("http://ex.com/img"), null);
        fm.deleteFond(fond.getId());

        assertFalse(fm.getFond(fond.getId()).isPresent());
        assertFalse(mc.getCollection("/db/mom-data/metadata.charter.public/DE-SAMuenchen/MUrkunden").isPresent());
        assertFalse(mc.getCollection("/db/mom-data/metadata.fond.public/DE-SAMuenchen/MUrkunden").isPresent());

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDeleteFondWithExistingCharters() throws Exception {
        fm.deleteFond(new IdFond("CH-KAE", "Urkunden"));
    }

    @Test
    public void testGetFond() throws Exception {

        IdFond idNotExisting = new IdFond("CH-KAE", "Not existing fond");
        assertFalse(fm.getFond(idNotExisting).isPresent());

        IdFond id1 = new IdFond("CH-KAE", "Urkunden");
        Optional<Fond> fondOptional1 = fm.getFond(id1);
        assertTrue(fondOptional1.isPresent());
        Fond fond1 = fondOptional1.get();
        assertEquals(fond1.getId().getContentXml().toXML(), id1.getContentXml().toXML());
        assertEquals(fond1.getName(), "Urkunden (0947-1483)");
        assertEquals(fond1.getImageAccess().get(), ImageAccess.FREE);
        assertFalse(fond1.getDummyImageUrl().isPresent());
        assertEquals(fond1.getImagesUrl().get().toExternalForm(), "http://www.klosterarchiv.ch/urkunden/urkunden-3000");

        IdFond id2 = new IdFond("CH-KASchwyz", "Urkunden");
        Optional<Fond> fondOptional2 = fm.getFond(id2);
        assertTrue(fondOptional2.isPresent());
        Fond fond2 = fondOptional2.get();
        assertEquals(fond2.getId().getContentXml().toXML(), id2.getContentXml().toXML());
        assertEquals(fond2.getName(), "Urkunden");
        assertEquals(fond2.getImageAccess().get(), ImageAccess.RESTRICTED);
        assertEquals(fond2.getDummyImageUrl().get().toExternalForm(), "http://example.com/dummy.png");
        assertFalse(fond2.getImagesUrl().isPresent());

    }

    @Test
    public void testGetFondWithoutPreferences() throws Exception {

        IdFond id = new IdFond("CH-KAE", "UrkundenWithoutPrefs");
        Fond fond = fm.getFond(id).get();
        assertFalse(fond.getImageAccess().isPresent());
        assertFalse(fond.getDummyImageUrl().isPresent());
        assertFalse(fond.getImagesUrl().isPresent());

    }

    @Test
    public void testListFonds() throws Exception {

        IdArchive id1 = new IdArchive("CH-KAE");
        Archive archive1 = mc.getArchiveManager().getArchive(id1).get();
        List<IdFond> resultList1 = fm.listFonds(archive1.getId());
        assertEquals(resultList1.size(), 2);
        assertEquals(resultList1.get(0).getIdentifier(), "Urkunden");

        IdArchive id2 = new IdArchive("DE-SAMuenchen");
        Archive archive2 = mc.getArchiveManager().getArchive(id2).get();
        List<IdFond> resultList2 = fm.listFonds(archive2.getId());
        assertTrue(resultList2.isEmpty());

    }

}