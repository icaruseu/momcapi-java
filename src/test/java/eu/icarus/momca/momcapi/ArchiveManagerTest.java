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
public class ArchiveManagerTest {

    private ArchiveManager am;
    private MomcaConnection mc;

    @BeforeClass
    public void setUp() throws Exception {
        mc = TestUtils.initMomcaConnection();
        am = mc.getArchiveManager();
        assertNotNull(am, "MOM-CA connection not initialized.");
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

        Archive newArchive = am.addArchive(author, shortName, name, country, subdivision, address, contactInformation, logoUrl);
        am.deleteArchive(newArchive);

        assertEquals(newArchive.getId().getArchiveIdentifier(), shortName);

        assertEquals(newArchive.getIdentifier(), shortName);
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

        am.addArchive(author, shortName, name, country, subdivision, address, contactInformation, logoUrl);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddArchiveWithNotexistingAuthor() throws Exception {

        Country country = mc.getCountryManager().getCountry("DE").get();
        Subdivision subdivision = country.getSubdivisions().stream().filter(s -> s.getCode().equals("DE-BW")).findFirst().get();
        Address address = new Address("Stuttgart", "01234", "Somewhere");
        ContactInformation contactInformation =
                new ContactInformation("http://example.com", "01234557", "0123458952", "alpha@example.com");

        am.addArchive("notExisting", "DE-GLAK", "Landesarchiv Baden-Württemberg, Abt. Generallandesarchiv Karlsruhe",
                country, subdivision, address, contactInformation, "http://example.com/image.png");

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

        Archive newArchive = am.addArchive(author, shortName, name, country, subdivision, address, contactInformation, logoUrl);
        am.deleteArchive(newArchive);

        assertFalse(am.getArchive(newArchive.getId()).isPresent());

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDeleteArchiveWithExistingFonds() throws Exception {
        IdArchive id = new IdArchive("CH-KAE");
        Archive archive = am.getArchive(id).get();
        am.deleteArchive(archive);
    }

    @Test
    public void testGetArchive() throws Exception {

        IdArchive existingArchiveIdentifier = new IdArchive("CH-KAE");
        IdArchive nonExistingArchiveIdentifier = new IdArchive("CH-ABC");

        assertTrue(am.getArchive(existingArchiveIdentifier).isPresent());
        assertFalse(am.getArchive(nonExistingArchiveIdentifier).isPresent());

    }

    @Test
    public void testListArchives() throws Exception {
        assertEquals(am.listArchives().size(), 5);
    }

    @Test
    public void testListArchivesForCountry() throws Exception {
        assertTrue(am.listArchivesForCountry(new Country("AT", "Österreich", new ArrayList<>(0))).isEmpty());
        assertEquals(am.listArchivesForCountry(new Country("CH", "Schweiz", new ArrayList<>(0))).size(), 2);
    }

    @Test
    public void testListArchivesForSubdivision() throws Exception {
        assertTrue(am.listArchivesForSubdivision(new Subdivision("DE-BW", "Baden-Württemberg")).isEmpty());
        assertEquals(am.listArchivesForSubdivision(new Subdivision("DE-BY", "Bayern")).size(), 1);
    }

}