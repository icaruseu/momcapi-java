package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.resource.*;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import eu.icarus.momca.momcapi.xml.atom.IdFond;
import eu.icarus.momca.momcapi.xml.eap.Country;
import eu.icarus.momca.momcapi.xml.eap.Subdivision;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Created by daniel on 22.07.2015.
 */
public class HierarchyManagerTest {

    private HierarchyManager hm;
    private MomcaConnection mc;

    @Test
    public void testGetFond() throws Exception {

        IdFond idNotExisting = new IdFond("CH-KAE", "Not existing fond");
        assertFalse(hm.getFond(idNotExisting).isPresent());

        IdFond id1 = new IdFond("CH-KAE", "Urkunden");
        Optional<Fond> fondOptional1 = hm.getFond(id1);
        assertTrue(fondOptional1.isPresent());
        Fond fond1 = fondOptional1.get();
        assertEquals(fond1.getId().toXML(), id1.toXML());
        assertEquals(fond1.getName(), "Urkunden (0947-1483)");
        assertEquals(fond1.getImageAccess(), ImageAccess.FREE);
        assertFalse(fond1.getDummyImageUrl().isPresent());
        assertEquals(fond1.getImagesUrl().get().toExternalForm(), "http://www.klosterarchiv.ch/urkunden/urkunden-3000");

        IdFond id2 = new IdFond("CH-KASchwyz", "Urkunden");
        Optional<Fond> fondOptional2 = hm.getFond(id2);
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
        Fond fond = hm.getFond(id).get();
        assertEquals(fond.getImageAccess(), ImageAccess.UNDEFINED);
        assertFalse(fond.getDummyImageUrl().isPresent());
        assertFalse(fond.getImagesUrl().isPresent());

    }

    @Test
    public void testDeleteArchive() throws Exception {

        String author = "admin";
        Country country = mc.getCountryManager().getCountry("DE").get();
        Subdivision subdivision = country.getSubdivisions().stream().filter(s -> s.getCode().equals("DE-BW")).findFirst().get();
        String shortName = "DE-HStASt";
        String name = "Landesarchiv Baden-Württemberg, Abt. Hauptstaatsarchiv Stuttgart";
        Address address = new Address("Stuttgart", "0123334", "Somewhere else");
        ContactInformation contactInformation =
                new ContactInformation("http://example.com", "01234557", "0123458952", "alpha@example.com");
        String logoUrl = "http://example.com/image.png";

        Archive newArchive = hm.addArchive(author, shortName, name, country, subdivision, address, contactInformation, logoUrl);
        hm.deleteArchive(newArchive);

        assertFalse(hm.getArchive(newArchive.getId()).isPresent());

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDeleteArchiveWithExistingFonds() throws Exception {
        IdArchive id = new IdArchive("CH-KAE");
        Archive archive = hm.getArchive(id).get();
        hm.deleteArchive(archive);
    }

    @Test
    public void testListFondsForArchive() throws Exception {

        IdArchive id1 = new IdArchive("CH-KAE");
        Archive archive1 = hm.getArchive(id1).get();
        List<IdFond> resultList1 = hm.listFondsForArchive(archive1);
        assertEquals(resultList1.size(), 2);
        assertEquals(resultList1.get(0).getFondIdentifier(), "Urkunden");

        IdArchive id2 = new IdArchive("DE-SAMuenchen");
        Archive archive2 = hm.getArchive(id2).get();
        List<IdFond> resultList2 = hm.listFondsForArchive(archive2);
        assertTrue(resultList2.isEmpty());

    }

    @BeforeClass
    public void setUp() throws Exception {
        mc = TestUtils.initMomcaConnection();
        hm = mc.getHierarchyManager();
        assertNotNull(hm, "MOM-CA connection not initialized.");
    }

    @Test
    public void testAddArchive() throws Exception {

        String author = "admin";
        Country country = mc.getCountryManager().getCountry("DE").get();
        Subdivision subdivision = country.getSubdivisions().stream().filter(s -> s.getCode().equals("DE-BW")).findFirst().get();
        String shortName = "DE-GLAK";
        String name = "Landesarchiv Baden-Württemberg, Abt. Generallandesarchiv Karlsruhe";
        Address address = new Address("Karlsruhe", "01234", "Somewhere");
        ContactInformation contactInformation =
                new ContactInformation("http://example.com", "01234557", "0123458952", "alpha@example.com");
        String logoUrl = "http://example.com/image.png";

        Archive newArchive = hm.addArchive(author, shortName, name, country, subdivision, address, contactInformation, logoUrl);
        hm.deleteArchive(newArchive);

        assertEquals(newArchive.getId().getArchiveIdentifier(), shortName);

        assertEquals(newArchive.getShortName(), shortName);
        assertEquals(newArchive.getName(), name);

        assertEquals(newArchive.getCountryCode(), country.getCode());
        assertEquals(newArchive.getSubdivisionNativeForm(), subdivision.getNativeform());

        assertEquals(newArchive.getAddress(), address);
        assertEquals(newArchive.getContactInformation(), contactInformation);
        assertEquals(newArchive.getLogoUrl(), logoUrl);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddArchiveAlreadyExisting() throws Exception {

        String author = "admin";
        Country country = mc.getCountryManager().getCountry("DE").get();
        Subdivision subdivision = country.getSubdivisions().stream().filter(s -> s.getCode().equals("DE-BY")).findFirst().get();
        String shortName = "DE-BayHStA";
        String name = "München, Bayerisches Hauptstaatsarchiv";
        Address address = new Address("", "", "");
        ContactInformation contactInformation = new ContactInformation("", "", "", "");
        String logoUrl = "http://example.com/image.png";

        hm.addArchive(author, shortName, name, country, subdivision, address, contactInformation, logoUrl);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddArchiveWithNotexistingAuthor() throws Exception {

        Country country = mc.getCountryManager().getCountry("DE").get();
        Subdivision subdivision = country.getSubdivisions().stream().filter(s -> s.getCode().equals("DE-BW")).findFirst().get();
        Address address = new Address("Stuttgart", "01234", "Somewhere");
        ContactInformation contactInformation =
                new ContactInformation("http://example.com", "01234557", "0123458952", "alpha@example.com");

        hm.addArchive("notExisting", "DE-GLAK", "Landesarchiv Baden-Württemberg, Abt. Generallandesarchiv Karlsruhe",
                country, subdivision, address, contactInformation, "http://example.com/image.png");

    }

    @Test
    public void testGetArchive() throws Exception {

        IdArchive existingArchiveIdentifier = new IdArchive("CH-KAE");
        IdArchive nonExistingArchiveIdentifier = new IdArchive("CH-ABC");

        assertTrue(hm.getArchive(existingArchiveIdentifier).isPresent());
        assertFalse(hm.getArchive(nonExistingArchiveIdentifier).isPresent());

    }

    @Test
    public void testListArchives() throws Exception {
        assertEquals(hm.listArchives().size(), 5);
    }

    @Test
    public void testListArchivesForCountry() throws Exception {
        assertTrue(hm.listArchivesForCountry(new Country("AT", "Österreich", new ArrayList<>(0))).isEmpty());
        assertEquals(hm.listArchivesForCountry(new Country("CH", "Schweiz", new ArrayList<>(0))).size(), 2);
    }

    @Test
    public void testListArchivesForSubdivision() throws Exception {
        assertTrue(hm.listArchivesForSubdivision(new Subdivision("DE-BW", "Baden-Württemberg")).isEmpty());
        assertEquals(hm.listArchivesForSubdivision(new Subdivision("DE-BY", "Bayern")).size(), 1);
    }

}