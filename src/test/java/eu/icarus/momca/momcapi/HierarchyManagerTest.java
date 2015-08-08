package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.resource.Address;
import eu.icarus.momca.momcapi.resource.Archive;
import eu.icarus.momca.momcapi.resource.ContactInformation;
import eu.icarus.momca.momcapi.xml.atom.IdArchive;
import eu.icarus.momca.momcapi.xml.eap.Country;
import eu.icarus.momca.momcapi.xml.eap.Subdivision;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.*;

/**
 * Created by daniel on 22.07.2015.
 */
public class HierarchyManagerTest {

    private HierarchyManager hm;
    private MomcaConnection mc;

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
        Address address = new Address("Stuttgart", "01234", "Somewhere");
        ContactInformation contactInformation =
                new ContactInformation("http://example.com", "01234557", "0123458952", "alpha@example.com");
        String logoUrl = "http://example.com/image.png";

        Archive newArchive = hm.addArchive(author, shortName, name, country, subdivision, address, contactInformation, logoUrl);

        assertEquals(newArchive.getId().getArchiveIdentifier(), shortName);

        assertEquals(newArchive.getShortName(), shortName);
        assertEquals(newArchive.getName(), name);

        assertEquals(newArchive.getCountryCode(), country.getCode());
        assertEquals(newArchive.getSubdivisionNativeForm(), subdivision.getNativeform());

        assertEquals(newArchive.getAddress(), address);
        assertEquals(newArchive.getContactInformation(), contactInformation);
        assertEquals(newArchive.getLogoUrl(), logoUrl);

        hm.deleteArchive(newArchive);

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
        assertEquals(hm.listArchives().size(), 3);
    }

    @Test
    public void testListArchivesForCountry() throws Exception {
        assertTrue(hm.listArchivesForCountry(new Country("AT", "Österreich", new ArrayList<>(0))).isEmpty());
        assertEquals(hm.listArchivesForCountry(new Country("CH", "Schweiz", new ArrayList<>(0))).size(), 1);
    }

    @Test
    public void testListArchivesForSubdivision() throws Exception {
        assertTrue(hm.listArchivesForSubdivision(new Subdivision("DE-BW", "Baden-Württemberg")).isEmpty());
        assertEquals(hm.listArchivesForSubdivision(new Subdivision("DE-BY", "Bayern")).size(), 1);
    }

}