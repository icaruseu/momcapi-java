package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import eu.icarus.momca.momcapi.xml.eap.Country;
import eu.icarus.momca.momcapi.xml.eap.Subdivision;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.*;

/**
 * Created by daniel on 22.07.2015.
 * =======
 * import org.testng.annotations.BeforeClass;
 * <p>
 * import static org.testng.Assert.assertNotNull;
 * <p>
 * /**
 * Created by djell on 07/08/2015.
 * >>>>>>> MAPI-7
 */
public class HierarchyManagerTest {

    private HierarchyManager hm;

    @Test
    public void testListArchivesForSubdivision() throws Exception {
        assertTrue(hm.listArchivesForSubdivision(new Subdivision("DE-BW", "Baden-Württemberg")).isEmpty());
        assertEquals(hm.listArchivesForSubdivision(new Subdivision("DE-BY", "Bayern")).size(), 1);
    }

    @Test
    public void testListArchivesForCountry() throws Exception {
        assertTrue(hm.listArchivesForCountry(new Country("AT", "Österreich", new ArrayList<>(0))).isEmpty());
        assertEquals(hm.listArchivesForCountry(new Country("CH", "Schweiz", new ArrayList<>(0))).size(), 1);
    }

    @Test
    public void testListArchives() throws Exception {
        assertEquals(hm.listArchives().size(), 3);
    }

    @BeforeClass
    public void setUp() throws Exception {
        hm = TestUtils.initMomcaConnection().getHierarchyManager();
        assertNotNull(hm, "MOM-CA connection not initialized.");
    }

    @Test
    public void testGetArchive() throws Exception {

        IdArchive existingArchiveIdentifier = new IdArchive("CH-KAE");
        IdArchive nonExistingArchiveIdentifier = new IdArchive("CH-ABC");

        assertTrue(hm.getArchive(existingArchiveIdentifier).isPresent());
        assertFalse(hm.getArchive(nonExistingArchiveIdentifier).isPresent());

    }

}