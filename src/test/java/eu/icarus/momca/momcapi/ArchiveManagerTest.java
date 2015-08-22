package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.*;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.resource.Archive;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

        String identifier = "DE-GLAK";
        String name = "Landesarchiv Baden-Württemberg, Abt. Generallandesarchiv Karlsruhe";
        Country country = new Country(new CountryCode("DE"), "Deutschland");

        Archive archiveToAdd = new Archive(identifier, name, country);
        am.addArchive(archiveToAdd);

        Optional<Archive> archiveFromDbOptional = am.getArchive(archiveToAdd.getId());
        am.deleteArchive(archiveToAdd.getId());
        assertTrue(archiveFromDbOptional.isPresent());
        Archive archiveFromDb = archiveFromDbOptional.get();

        assertEquals(archiveFromDb.getId(), new IdArchive(identifier));
        assertEquals(archiveFromDb.getIdentifier(), identifier);
        assertEquals(archiveFromDb.getCountry(), country);
        assertFalse(archiveFromDb.getRegionName().isPresent());
        assertFalse(archiveFromDb.getCreator().isPresent());
        assertFalse(archiveFromDb.getAddress().isPresent());
        assertFalse(archiveFromDb.getContactInformation().isPresent());
        assertFalse(archiveFromDb.getLogoUrl().isPresent());

        String newIdentifier = "CH-BLAK";
        String newName = "New archive name";
        IdUser creator = new IdUser("admin");
        Country newCountry = new Country(new CountryCode("IT"), "Italia");
        Region region = new Region("IT-CAM", "Campania");
        Address address = new Address("Napoli", "80138", "Piazzetta del Grande Archivio, 5");
        ContactInformation contactInformation = new ContactInformation(
                "http://dev.monasterium.net/mom/IT-ASNA/www.archiviodistatonapoli.it", "0039/ 81 56 38 - 300",
                "0039/ 81 56 38 - 111", "as-na@beniculturali.it");
        String logoUrl = "http://example.com/image.png";

        archiveFromDb.setIdentifier(newIdentifier);
        archiveFromDb.setName(newName);
        archiveFromDb.setCreator(creator.getIdentifier());
        archiveFromDb.setCountry(newCountry);
        archiveFromDb.setRegionName(region.getNativeName());
        archiveFromDb.setAddress(address);
        archiveFromDb.setContactInformation(contactInformation);
        archiveFromDb.setLogoUrl(logoUrl);

        am.addArchive(archiveFromDb);
        Optional<Archive> changedArchiveOptional = am.getArchive(archiveFromDb.getId());
        am.deleteArchive(archiveFromDb.getId());
        assertTrue(changedArchiveOptional.isPresent());
        Archive changedArchive = changedArchiveOptional.get();

        assertEquals(changedArchive.getId(), new IdArchive(newIdentifier));
        assertEquals(changedArchive.getIdentifier(), newIdentifier);
        assertEquals(changedArchive.getName(), newName);
        assertEquals(changedArchive.getCreator().get(), creator);
        assertEquals(changedArchive.getCountry(), newCountry);
        assertEquals(changedArchive.getRegionName().get(), region.getNativeName());
        assertEquals(changedArchive.getAddress().get(), address);
        assertEquals(changedArchive.getContactInformation().get(), contactInformation);
        assertEquals(changedArchive.getLogoUrl().get(), logoUrl);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddArchiveAlreadyExisting() throws Exception {
        am.addArchive(
                new Archive("DE-BayHStA", "München, Bayerisches Hauptstaatsarchiv",
                        new Country(new CountryCode("DE"), "Deutschland")));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddArchiveWithEmptyIdentifier() throws Exception {
        am.addArchive(new Archive("", "Some Archive", new Country(new CountryCode("DE"), "Deutschland")));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddArchiveWithEmptyName() throws Exception {
        am.addArchive(new Archive("sarchive", "", new Country(new CountryCode("DE"), "Deutschland")));
    }

    @Test
    public void testDeleteArchive() throws Exception {

        Country country = new Country(new CountryCode("DE"), "Deutschland");
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
        assertTrue(am.listArchives(new Country(new CountryCode("AT"), "Österreich")).isEmpty());
        assertEquals(am.listArchives(new Country(new CountryCode("CH"), "Schweiz")).size(), 2);
    }

    @Test
    public void testListArchivesForRegion() throws Exception {

        Region region1 = new Region("DE-BW", "Baden-Württemberg");
        Region region2 = new Region("DE-BY","Bayern");

        assertTrue(am.listArchives(region1).isEmpty());
        assertEquals(am.listArchives(region2).size(), 1);

    }

}