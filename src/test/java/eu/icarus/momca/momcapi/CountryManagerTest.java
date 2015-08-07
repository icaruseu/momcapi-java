package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.query.ExistQueryFactory;
import eu.icarus.momca.momcapi.xml.eap.Country;
import eu.icarus.momca.momcapi.xml.eap.Subdivision;
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

    private CountryManager countryManager;
    private MomcaConnection momcaConnection;

    @BeforeClass
    public void setUp() throws Exception {
        momcaConnection = TestUtils.initMomcaConnection();
        countryManager = momcaConnection.getCountryManager();
        assertNotNull(countryManager, "MOM-CA connection not initialized.");
    }

    @Test
    public void testAddCountry() throws Exception {

        String code = "AT";
        String nativeform = "Österreich";

        Country newCountry = countryManager.addCountry(code, nativeform);

        assertEquals(newCountry.getCode(), code);
        assertEquals(newCountry.getNativeform(), nativeform);

        countryManager.deleteCountry(code);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCountryThatExists() throws Exception {
        String existingCode = "DE";
        String nativeform = "Österreich";
        countryManager.addCountry(existingCode, nativeform);
    }

    @Test
    public void testAddSubdivision() throws Exception {

        Country country = countryManager.getCountry("RS").get();
        String subdivision = "RS-BG";

        country = countryManager.addSubdivision(country, subdivision, "Beograd");

        assertFalse(country.getSubdivisions().isEmpty());

        countryManager.deleteSubdivision(country, subdivision);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddSubdivisionAlreadyExisting() throws Exception {
        Country country = countryManager.getCountry("DE").get();
        countryManager.addSubdivision(country, "DE-BW", "Baden-Württemberg");
    }

    @Test
    public void testChangeCountryCode() throws Exception {

        String originalCode = "CH";
        Country originalCountry = countryManager.getCountry(originalCode).get();

        String newCode = "Schw";

        Country updatedCountry = countryManager.changeCountryCode(originalCountry, newCode);
        countryManager.changeCountryCode(updatedCountry, originalCode);

        assertEquals(updatedCountry.getCode(), newCode);

    }

    @Test
    public void testChangeCountryNativeform() throws Exception {

        String code = "CH";
        Country originalCountry = countryManager.getCountry(code).get();

        String originalNativeform = "Schweiz";
        String newNativeform = "Svizzera";

        Country updatedCountry = countryManager.changeCountryNativeform(originalCountry, newNativeform);
        countryManager.changeCountryNativeform(updatedCountry, originalNativeform);

        assertEquals(updatedCountry.getNativeform(), newNativeform);

    }

    @Test
    public void testChangeSubdivisionCode() throws Exception {

        String countryCode = "DE";
        Country originalCountry = countryManager.getCountry(countryCode).get();

        String originalSubdivisionCode = "DE-BY";
        String newSubdivisionCode = "DE-BAY";

        Country updatedCountry = countryManager
                .changeSubdivisionCode(originalCountry, originalSubdivisionCode, newSubdivisionCode);
        countryManager.changeSubdivisionCode(updatedCountry, newSubdivisionCode, originalSubdivisionCode);

        assertEquals(updatedCountry.getSubdivisions().stream()
                .filter(subdivision -> subdivision.getCode().equals(newSubdivisionCode)).count(), 1);

    }

    @Test
    public void testChangeSubdivisionNativeform() throws Exception {

        String code = "DE";
        Country originalCountry = countryManager.getCountry(code).get();

        String originalNativeform = "Bayern";
        String newNativeform = "Bavaria";

        Country updatedCountry = countryManager
                .changeSubdivisionNativeform(originalCountry, originalNativeform, newNativeform);
        countryManager.changeSubdivisionNativeform(updatedCountry, newNativeform, originalNativeform);

        assertEquals(updatedCountry.getSubdivisions().stream()
                .filter(subdivision -> subdivision.getNativeform().equals(newNativeform)).count(), 1);

    }

    @Test
    public void testDeleteCountry() throws Exception {

        String code = "SE";
        countryManager.addCountry(code, "Sverige");
        countryManager.deleteCountry(code);
        assertFalse(countryManager.getCountry(code).isPresent());

    }

    @Test(expectedExceptions = MomcaException.class)
    public void testDeleteCountryWithExistingArchives() throws Exception {
        countryManager.deleteCountry("CH");
    }

    @Test
    public void testDeleteSubdivision() throws Exception {

        Country country = countryManager.getCountry("CH").get();
        String subdivision = "CH-SG";
        country = countryManager.addSubdivision(country, subdivision, "Sankt Gallen");
        country = countryManager.deleteSubdivision(country, subdivision);

        assertTrue(country.getSubdivisions().isEmpty());

    }

    @Test(expectedExceptions = MomcaException.class)
    public void testDeleteSubdivisionWithExistingArchives() throws Exception {
        Country country = countryManager.getCountry("DE").get();
        String subdivision = "DE-BY";
        countryManager.deleteSubdivision(country, subdivision);
    }

    @Test
    public void testGetCountry() throws Exception {

        Optional<Country> countryOptional = countryManager.getCountry("DE");
        assertTrue(countryOptional.isPresent());

        Country country = countryOptional.get();
        assertEquals(country.getCode(), "DE");
        assertEquals(country.getNativeform(), "Deutschland");
        assertEquals(country.toXML(), "<eap:country xmlns:eap=\"http://www.monasterium.net/NS/eap\">" +
                "<eap:code>DE</eap:code><eap:nativeform>Deutschland</eap:nativeform>" +
                "<eap:subdivisions><eap:subdivision><eap:code>DE-BW</eap:code><eap:nativeform>" +
                "Baden-Württemberg</eap:nativeform></eap:subdivision><eap:subdivision><eap:code>DE-BY</eap:code>" +
                "<eap:nativeform>Bayern</eap:nativeform></eap:subdivision></eap:subdivisions></eap:country>");

        List<Subdivision> subdivisions = country.getSubdivisions();
        assertEquals(subdivisions.size(), 2);

        assertEquals(subdivisions.get(0).getCode(), "DE-BW");
        assertEquals(subdivisions.get(0).getNativeform(), "Baden-Württemberg");


        assertEquals(subdivisions.get(1).getCode(), "DE-BY");
        assertEquals(subdivisions.get(1).getNativeform(), "Bayern");

    }

    @Test
    public void testGetCountryNotExisting() throws Exception {
        assertFalse(countryManager.getCountry("notExisting").isPresent());
    }

    @Test
    public void testListCountries() throws Exception {
        assertEquals(countryManager.listCountries().toString(), "[CH, DE, RS]");
    }


}