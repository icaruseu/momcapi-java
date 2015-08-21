package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Optional;

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

        IdUser author = new IdUser("admin");
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = mc.getCountryManager().getRegions(country).stream().filter(s -> s.getCode().get().equals("DE-BW")).findFirst().get();
        String shortName = "DE-GLAK";
        String name = "Landesarchiv Baden-Württemberg, Abt. Generallandesarchiv Karlsruhe";
        Address address = new Address("Karlsruhe", "01234", "Somewhere");
        ContactInformation contactInformation =
                new ContactInformation("http://example.com", "01234557", "0123458952", "alpha@example.com");
        String logoUrl = "http://example.com/image.png";

        Archive newArchive = new Archive(shortName, name, country);
        newArchive.setCreator(author.getIdentifier());
        newArchive.setRegionName(region.getNativeName());
        newArchive.setAddress(address);
        newArchive.setContactInformation(contactInformation);
        newArchive.setLogoUrl(logoUrl);

        am.addArchive(newArchive);
        Optional<Archive> addedArchiveOptional = am.getArchive(newArchive.getId());
        am.deleteArchive(newArchive.getId());

        assertTrue(addedArchiveOptional.isPresent());

        Archive addedArchive = addedArchiveOptional.get();

        assertEquals(addedArchive.getId().getIdentifier(), shortName);

        assertEquals(addedArchive.getIdentifier(), shortName);
        assertEquals(addedArchive.getName(), name);

        assertEquals(addedArchive.getCountry().getCountryCode(), country.getCountryCode());
        assertEquals(addedArchive.getRegionName().get(), region.getNativeName());

        assertEquals(addedArchive.getAddress().get(), address);
        assertEquals(addedArchive.getContactInformation().get(), contactInformation);
        assertEquals(addedArchive.getLogoUrl().get(), logoUrl);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddArchiveAlreadyExisting() throws Exception {

        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        String shortName = "DE-BayHStA";
        String name = "München, Bayerisches Hauptstaatsarchiv";

        Archive newArchive = new Archive(shortName, name, country);

        am.addArchive(newArchive);

    }

    @Test
    public void testDeleteArchive() throws Exception {

        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        String shortName = "DE-HStASt";
        String name = "Landesarchiv Baden-Württemberg, Abt. Hauptstaatsarchiv Stuttgart";

        Archive newArchive = new Archive(shortName, name, country);
        am.addArchive(newArchive);
        am.deleteArchive(newArchive.getId());

        assertFalse(am.getArchive(newArchive.getId()).isPresent());
        assertFalse(mc.getCollection("/db/mom-data/metadata.fond.public/" + newArchive.getIdentifier()).isPresent());

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDeleteArchiveWithExistingFonds() throws Exception {
        IdArchive id = new IdArchive("CH-KAE");
        am.deleteArchive(id);
    }

    @Test
    public void testGetArchive() throws Exception {

        IdArchive existingArchiveIdentifier = new IdArchive("CH-KAE");
        IdArchive nonExistingArchiveIdentifier = new IdArchive("CH-ABC");

        assertTrue(am.getArchive(existingArchiveIdentifier).isPresent());

        Archive archive = am.getArchive(existingArchiveIdentifier).get();
        assertEquals(archive.getName(), "Klosterarchiv Einsiedeln");
        assertEquals(archive.getCountry().getCountryCode().getCode(), "CH");
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