package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.Country;
import eu.icarus.momca.momcapi.model.CountryCode;
import eu.icarus.momca.momcapi.model.Region;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class CountryManagerTest {

    private CountryManager cm;

    @BeforeClass
    public void setUp() throws Exception {


        MomcaConnection momcaConnection = TestUtils.initMomcaConnection();
        cm = momcaConnection.getCountryManager();
        assertNotNull(cm, "MOM-CA connection not initialized.");

        cm.deleteCountryFromHierarchy(new CountryCode("AT"));
        cm.deleteCountryFromHierarchy(new CountryCode("SE"));
        cm.deleteRegionFromHierarchy(new Country(new CountryCode("CH"), "Schweiz"), "Sankt Gallen");
        cm.deleteRegionFromHierarchy(new Country(new CountryCode("RS"), "Serbia"), "Beograd");

    }

    @Test
    public void testAddNewCountryToHierarchy() throws Exception {

        CountryCode code = new CountryCode("AT");
        String nativeName = "Österreich";
        Country country = new Country(code, nativeName);

        assertTrue(cm.addNewCountryToHierarchy(country));

        Optional<Country> countryOptional = cm.getCountry(code);
        cm.deleteCountryFromHierarchy(code);

        assertTrue(countryOptional.isPresent());
        assertEquals(countryOptional.get(), country);

    }

    @Test
    public void testAddNewCountryToHierarchyThatExists() throws Exception {
        assertFalse(cm.addNewCountryToHierarchy(new Country(new CountryCode("DE"), "Deutschland")));
    }

    @Test
    public void testAddRegionToHierarchy() throws Exception {

        Country country = new Country(new CountryCode("RS"), "Serbia");
        Region region = new Region("RS-BG", "Beograd");

        assertTrue(cm.addRegionToHierarchy(country, region));

        List<Region> regions = cm.getRegions(country);
        cm.deleteRegionFromHierarchy(country, region.getNativeName());

        assertTrue(regions.contains(region));

    }

    @Test
    public void testAddRegionToHierarchyAlreadyExisting() throws Exception {
        Country country = new Country(new CountryCode("DE"), "Deutschland");
        assertFalse(cm.addRegionToHierarchy(country, new Region("DE-BW", "Baden-Württemberg")));
    }

    @Test
    public void testDeleteCountryFromHierarchy() throws Exception {

        CountryCode code = new CountryCode("SE");
        cm.addNewCountryToHierarchy(new Country(code, "Sverige"));

        assertTrue(cm.deleteCountryFromHierarchy(code));

        assertFalse(cm.getCountry(code).isPresent());

    }

    @Test
    public void testDeleteCountryFromHierarchyWithExistingArchives() throws Exception {
        assertFalse(cm.deleteCountryFromHierarchy(new CountryCode("CH")));
    }

    @Test
    public void testDeleteRegionFromHierarchy() throws Exception {

        Country country = cm.getCountry(new CountryCode("CH")).get();
        Region region = new Region("CH-SG", "Sankt Gallen");
        cm.addRegionToHierarchy(country, region);

        assertTrue(cm.deleteRegionFromHierarchy(country, region.getNativeName()));

        assertTrue(cm.getRegions(country).isEmpty());

    }

    @Test
    public void testDeleteRegionFromHierarchyWithExistingArchives() throws Exception {
        Country country = cm.getCountry(new CountryCode("DE")).get();
        assertFalse(cm.deleteRegionFromHierarchy(country, "Bayern"));
    }

    @Test
    public void testGetCountry() throws Exception {

        CountryCode countryCode = new CountryCode("DE");
        Country country = new Country(countryCode, "Deutschland");

        Optional<Country> countryOptional = cm.getCountry(countryCode);

        assertTrue(countryOptional.isPresent());
        assertEquals(countryOptional.get(), country);

    }

    @Test
    public void testGetCountryNotExisting() throws Exception {
        assertFalse(cm.getCountry(new CountryCode("RU")).isPresent());
    }

    @Test
    public void testGetRegions() throws Exception {

        Country country = cm.getCountry(new CountryCode("DE")).get();
        List<Region> regions = cm.getRegions(country);

        assertEquals(regions.size(), 3);

        Region region1 = new Region("DE-BY", "Bayern");
        Region region2 = new Region("DE-BW", "Baden-Württemberg");
        Region region3 = new Region("DE-NRW", "Nordrhein-Westfalen");

        assertTrue(regions.contains(region1));
        assertTrue(regions.contains(region2));
        assertTrue(regions.contains(region3));

    }

    @Test
    public void testListCountries() throws Exception {
        assertEquals(cm.listCountries().size(), 5);
    }

}