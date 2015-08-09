package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.resource.Archive;
import eu.icarus.momca.momcapi.resource.Fond;
import eu.icarus.momca.momcapi.resource.ImageAccess;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import eu.icarus.momca.momcapi.xml.atom.IdFond;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
    public void testGetFond() throws Exception {

        IdFond idNotExisting = new IdFond("CH-KAE", "Not existing fond");
        assertFalse(fm.getFond(idNotExisting).isPresent());

        IdFond id1 = new IdFond("CH-KAE", "Urkunden");
        Optional<Fond> fondOptional1 = fm.getFond(id1);
        assertTrue(fondOptional1.isPresent());
        Fond fond1 = fondOptional1.get();
        assertEquals(fond1.getId().toXML(), id1.toXML());
        assertEquals(fond1.getName(), "Urkunden (0947-1483)");
        assertEquals(fond1.getImageAccess(), ImageAccess.FREE);
        assertFalse(fond1.getDummyImageUrl().isPresent());
        assertEquals(fond1.getImagesUrl().get().toExternalForm(), "http://www.klosterarchiv.ch/urkunden/urkunden-3000");

        IdFond id2 = new IdFond("CH-KASchwyz", "Urkunden");
        Optional<Fond> fondOptional2 = fm.getFond(id2);
        assertTrue(fondOptional2.isPresent());
        Fond fond2 = fondOptional2.get();
        assertEquals(fond2.getId().toXML(), id2.toXML());
        assertEquals(fond2.getName(), "Urkunden");
        assertEquals(fond2.getImageAccess(), ImageAccess.RESTRICTED);
        assertEquals(fond2.getDummyImageUrl().get().toExternalForm(), "http://example.com/dummy.png");
        assertFalse(fond2.getImagesUrl().isPresent());

    }

    @Test
    public void testGetFondWithoutPreferences() throws Exception {

        IdFond id = new IdFond("CH-KAE", "UrkundenWithoutPrefs");
        Fond fond = fm.getFond(id).get();
        assertEquals(fond.getImageAccess(), ImageAccess.UNDEFINED);
        assertFalse(fond.getDummyImageUrl().isPresent());
        assertFalse(fond.getImagesUrl().isPresent());

    }

    @Test
    public void testListFondsForArchive() throws Exception {

        IdArchive id1 = new IdArchive("CH-KAE");
        Archive archive1 = mc.getArchiveManager().getArchive(id1).get();
        List<IdFond> resultList1 = fm.listFondsForArchive(archive1);
        assertEquals(resultList1.size(), 2);
        assertEquals(resultList1.get(0).getFondIdentifier(), "Urkunden");

        IdArchive id2 = new IdArchive("DE-SAMuenchen");
        Archive archive2 = mc.getArchiveManager().getArchive(id2).get();
        List<IdFond> resultList2 = fm.listFondsForArchive(archive2);
        assertTrue(resultList2.isEmpty());

    }

}