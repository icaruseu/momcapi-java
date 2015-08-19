package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.*;
import eu.icarus.momca.momcapi.model.IdArchive;
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
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = country.getRegions().stream().filter(s -> s.getCode().get().equals("DE-BW")).findFirst().get();
        String shortName = "DE-GLAK";
        String name = "Landesarchiv Baden-Württemberg, Abt. Generallandesarchiv Karlsruhe";
        Address address = new Address("Karlsruhe", "01234", "Somewhere");
        ContactInformation contactInformation =
                new ContactInformation("http://example.com", "01234557", "0123458952", "alpha@example.com");
        String logoUrl = "http://example.com/image.png";

        Archive newArchive = am.addArchive(author, shortName, name, country, region, address, contactInformation, logoUrl);
        am.deleteArchive(newArchive);

        assertEquals(newArchive.getId().getIdentifier(), shortName);

        assertEquals(newArchive.getIdentifier(), shortName);
        assertEquals(newArchive.getName(), name);

        assertEquals(newArchive.getCountryCode(), country.getCountryCode());
        assertEquals(newArchive.getRegionName().get(), region.getNativeName());

        assertEquals(newArchive.getAddress(), address);
        assertEquals(newArchive.getContactInformation(), contactInformation);
        assertEquals(newArchive.getLogoUrl(), logoUrl);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddArchiveAlreadyExisting() throws Exception {

        String author = "admin";
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = country.getRegions().stream().filter(r -> r.getCode().get().equals("DE-BY")).findFirst().get();
        String shortName = "DE-BayHStA";
        String name = "München, Bayerisches Hauptstaatsarchiv";
        Address address = new Address("", "", "");
        ContactInformation contactInformation = new ContactInformation("", "", "", "");
        String logoUrl = "http://example.com/image.png";

        am.addArchive(author, shortName, name, country, region, address, contactInformation, logoUrl);

    }

    @Test
    public void testDeleteArchive() throws Exception {

        String author = "admin";
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = country.getRegions().stream().filter(r -> r.getCode().get().equals("DE-BW")).findFirst().get();
        String shortName = "DE-HStASt";
        String name = "Landesarchiv Baden-Württemberg, Abt. Hauptstaatsarchiv Stuttgart";
        Address address = new Address("Stuttgart", "0123334", "Somewhere else");
        ContactInformation contactInformation =
                new ContactInformation("http://example.com", "01234557", "0123458952", "alpha@example.com");
        String logoUrl = "http://example.com/image.png";

        Archive newArchive = am.addArchive(author, shortName, name, country, region, address, contactInformation, logoUrl);
        am.deleteArchive(newArchive);

        assertFalse(am.getArchive(newArchive.getId()).isPresent());
        assertFalse(mc.getCollection("/db/mom-data/metadata.fond.public/" + newArchive.getIdentifier()).isPresent());

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

        Archive archive = am.getArchive(existingArchiveIdentifier).get();
        assertEquals(archive.getName(), "Klosterarchiv Einsiedeln");
        assertEquals(archive.getCountryCode().getCode(), "CH");
        assertFalse(archive.getRegionName().isPresent());

        assertFalse(am.getArchive(nonExistingArchiveIdentifier).isPresent());

    }

    @Test
    public void testListArchives() throws Exception {
        assertEquals(am.listArchives().size(), 5);
    }

    @Test
    public void testListArchivesForCountry() throws Exception {
        assertTrue(am.listArchives(new Country(new CountryCode("AT"), "Österreich", new ArrayList<>(0))).isEmpty());
        assertEquals(am.listArchives(new Country(new CountryCode("CH"), "Schweiz", new ArrayList<>(0))).size(), 2);
    }

    @Test
    public void testListArchivesForSubdivision() throws Exception {
        assertTrue(am.listArchives(new Region("DE-BW", "Baden-Württemberg")).isEmpty());
        assertEquals(am.listArchives(new Region("DE-BY", "Bayern")).size(), 1);
    }

}