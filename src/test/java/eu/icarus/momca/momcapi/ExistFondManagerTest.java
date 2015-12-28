package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.ImageAccess;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.id.IdFond;
import eu.icarus.momca.momcapi.model.resource.Fond;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Created by djell on 09/08/2015.
 */
public class ExistFondManagerTest {

    private FondManager fm;
    private ExistMomcaConnection mc;

    @BeforeClass
    public void setUp() throws Exception {
        mc = TestUtils.initMomcaConnection();
        fm = mc.getFondManager();
        assertNotNull(fm, "MOM-CA connection not initialized.");
    }

    @Test
    public void testAddFond() throws Exception {

        String identifier = "Urkunden";
        String archiveIdentifier = "DE-SAMuenchen";
        IdArchive idArchive = new IdArchive(archiveIdentifier);
        String name = "Alle Urkunden";

        Fond newFond = new Fond(identifier, idArchive, name);

        assertTrue(fm.add(newFond));

        Optional<Fond> fondFromTheDatabaseOptional = fm.get(new IdFond(archiveIdentifier, identifier));
        fm.delete(newFond.getId());

        newFond.setArchiveId(new IdArchive("NotExisting"));
        assertFalse(fm.add(newFond));

        assertTrue(fondFromTheDatabaseOptional.isPresent());
        assertEquals(fondFromTheDatabaseOptional.get().getIdentifier(), identifier);
        assertEquals(fondFromTheDatabaseOptional.get().getArchiveId(), idArchive);
        assertEquals(fondFromTheDatabaseOptional.get().getName(), name);

    }

    @Test
    public void testAddFondExisting() throws Exception {
        assertFalse(fm.add(new Fond("Urkunden", new IdArchive("CH-KAE"), "Klosterurkunden")));
    }

    @Test
    public void testDeleteFond() throws Exception {

        IdArchive idArchive = new IdArchive("DE-SAMuenchen");
        IdFond id = new IdFond(idArchive.getIdentifier(), "MUrkunden");

        fm.add(new Fond("MUrkunden", new IdArchive("DE-SAMuenchen"), "Mehrere Urkunden"));

        assertTrue(fm.delete(id));

        assertFalse(fm.get(id).isPresent());
        assertFalse(mc.readCollection("/db/mom-data/metadata.charter.public/DE-SAMuenchen/MUrkunden").isPresent());
        assertFalse(mc.readCollection("/db/mom-data/metadata.fond.public/DE-SAMuenchen/MUrkunden").isPresent());

    }

    @Test
    public void testDeleteFondWithExistingCharters() throws Exception {
        assertFalse(fm.delete(new IdFond("CH-KAE", "Urkunden")));
    }

    @Test
    public void testGetFond() throws Exception {

        IdFond idNotExisting = new IdFond("CH-KAE", "Not existing fond");
        assertFalse(fm.get(idNotExisting).isPresent());

        IdFond id1 = new IdFond("CH-KAE", "Urkunden");
        Optional<Fond> fondOptional1 = fm.get(id1);
        assertTrue(fondOptional1.isPresent());
        Fond fond1 = fondOptional1.get();
        assertEquals(fond1.getId().getContentAsElement().toXML(), id1.getContentAsElement().toXML());
        assertEquals(fond1.getName(), "Urkunden (0947-1483)");
        assertEquals(fond1.getImageAccess().get(), ImageAccess.FREE);
        assertFalse(fond1.getDummyImageUrl().isPresent());
        assertEquals(fond1.getImagesUrl().get().toExternalForm(), "http://www.klosterarchiv.ch/urkunden/urkunden-3000");

        IdFond id2 = new IdFond("CH-KASchwyz", "Urkunden");
        Optional<Fond> fondOptional2 = fm.get(id2);
        assertTrue(fondOptional2.isPresent());
        Fond fond2 = fondOptional2.get();
        assertEquals(fond2.getId().getContentAsElement().toXML(), id2.getContentAsElement().toXML());
        assertEquals(fond2.getName(), "Urkunden");
        assertEquals(fond2.getImageAccess().get(), ImageAccess.RESTRICTED);
        assertEquals(fond2.getDummyImageUrl().get().toExternalForm(), "http://example.com/dummy.png");
        assertFalse(fond2.getImagesUrl().isPresent());

    }

    @Test
    public void testGetFondWithoutPreferences() throws Exception {

        IdFond id = new IdFond("CH-KAE", "UrkundenWithoutPrefs");
        Fond fond = fm.get(id).get();
        assertFalse(fond.getImageAccess().isPresent());
        assertFalse(fond.getDummyImageUrl().isPresent());
        assertFalse(fond.getImagesUrl().isPresent());

    }

    @Test
    public void testIsFondExisting() throws Exception {
        assertTrue(fm.isExisting(new IdFond("CH-KAE", "Urkunden")));
        assertTrue(fm.isExisting(new IdFond("CH-KAE", "Urkunden")));
    }

    @Test
    public void testListFonds() throws Exception {

        IdArchive id1 = new IdArchive("CH-KAE");
        List<IdFond> resultList1 = fm.list(id1);
        assertEquals(resultList1.size(), 2);
        assertEquals(resultList1.get(0).getIdentifier(), "Urkunden");

        IdArchive id2 = new IdArchive("DE-SAMuenchen");
        List<IdFond> resultList2 = fm.list(id2);
        assertTrue(resultList2.isEmpty());

    }

}