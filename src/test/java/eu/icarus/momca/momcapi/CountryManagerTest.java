package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.resource.Country;
import eu.icarus.momca.momcapi.resource.CountryCode;
import eu.icarus.momca.momcapi.resource.Region;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

/**
 * @author daniel
 *         Created on 17.07.2015.
 */
public class CountryManagerTest {

    private CountryManager cm;
    private MomcaConnection momcaConnection;

    @BeforeClass
    public void setUp() throws Exception {


        momcaConnection = TestUtils.initMomcaConnection();
        cm = momcaConnection.getCountryManager();
        assertNotNull(cm, "MOM-CA connection not initialized.");

        cm.deleteCountryFromHierarchy(new CountryCode("AT"));
        cm.deleteCountryFromHierarchy(new CountryCode("SE"));
        cm.deleteRegionFromHierarchy(new Country(new CountryCode("CH"), "Schweiz", new ArrayList<>(0)), "CH-SG");
        cm.deleteRegionFromHierarchy(new Country(new CountryCode("RS"), "Serbia", new ArrayList<>(0)), "RS-BG");

    }

    @Test
    public void testAddNewCountryToHierarchy() throws Exception {

        CountryCode code = new CountryCode("AT");
        String nativeName = "Österreich";

        Country newCountry = cm.addNewCountryToHierarchy(code, nativeName);

        assertEquals(newCountry.getCountryCode(), code);
        assertEquals(newCountry.getNativeName(), nativeName);

        cm.deleteCountryFromHierarchy(code);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddNewCountryToHierarchyThatExists() throws Exception {
        CountryCode existingCode = new CountryCode("DE");
        String nativeform = "Österreich";
        cm.addNewCountryToHierarchy(existingCode, nativeform);
    }

    @Test
    public void testAddRegionToHierarchy() throws Exception {

        Country country = cm.getCountry(new CountryCode("RS")).get();
        String regionCode = "RS-BG";

        country = cm.addRegionToHierarchy(country, regionCode, "Beograd");
        cm.deleteRegionFromHierarchy(country, regionCode);

        assertFalse(country.getRegions().isEmpty());

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddRegionToHierarchyAlreadyExisting() throws Exception {
        Country country = cm.getCountry(new CountryCode("DE")).get();
        cm.addRegionToHierarchy(country, "DE-BW", "Baden-Württemberg");
    }

    @Test
    public void testDeleteCountryFromHierarchy() throws Exception {

        CountryCode code = new CountryCode("SE");
        cm.addNewCountryToHierarchy(code, "Sverige");
        cm.deleteCountryFromHierarchy(code);
        assertFalse(cm.getCountry(code).isPresent());

    }

    @Test(expectedExceptions = MomcaException.class)
    public void testDeleteCountryFromHierarchyWithExistingArchives() throws Exception {
        cm.deleteCountryFromHierarchy(new CountryCode("CH"));
    }

    @Test
    public void testDeleteRegionFromHierarchy() throws Exception {

        Country country = cm.getCountry(new CountryCode("CH")).get();
        String regionCode = "CH-SG";
        country = cm.addRegionToHierarchy(country, regionCode, "Sankt Gallen");
        country = cm.deleteRegionFromHierarchy(country, regionCode);

        assertTrue(country.getRegions().isEmpty());

    }

    @Test(expectedExceptions = MomcaException.class)
    public void testDeleteRegionFromHierarchyWithExistingArchives() throws Exception {
        Country country = cm.getCountry(new CountryCode("DE")).get();
        String regionCode = "DE-BY";
        cm.deleteRegionFromHierarchy(country, regionCode);
    }

    @Test
    public void testGetCountry() throws Exception {

        Optional<Country> countryOptional = cm.getCountry(new CountryCode("DE"));
        assertTrue(countryOptional.isPresent());

        Country country = countryOptional.get();
        assertEquals(country.getCountryCode().getCode(), "DE");
        assertEquals(country.getNativeName(), "Deutschland");
        assertEquals(country.getHierarchyXml().toXML(), "<eap:country xmlns:eap=\"http://www.monasterium.net/NS/eap\">" +
                "<eap:code>DE</eap:code><eap:nativeform>Deutschland</eap:nativeform>" +
                "<eap:subdivisions><eap:subdivision><eap:code>DE-BW</eap:code><eap:nativeform>" +
                "Baden-Württemberg</eap:nativeform></eap:subdivision><eap:subdivision><eap:code>DE-BY</eap:code>" +
                "<eap:nativeform>Bayern</eap:nativeform></eap:subdivision></eap:subdivisions></eap:country>");

        List<Region> regions = country.getRegions();
        assertEquals(regions.size(), 2);

        assertEquals(regions.get(0).getCode().get(), "DE-BW");
        assertEquals(regions.get(0).getNativeName(), "Baden-Württemberg");


        assertEquals(regions.get(1).getCode().get(), "DE-BY");
        assertEquals(regions.get(1).getNativeName(), "Bayern");

    }

    @Test
    public void testGetCountryNotExisting() throws Exception {
        assertFalse(cm.getCountry(new CountryCode("RU")).isPresent());
    }

    @Test
    public void testListCountries() throws Exception {
        assertEquals(cm.listCountries().size(), 4);
    }

}