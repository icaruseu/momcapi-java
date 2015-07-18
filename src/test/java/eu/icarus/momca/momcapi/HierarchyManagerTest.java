package eu.icarus.momca.momcapi;

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
public class HierarchyManagerTest {

    private HierarchyManager hierarchyManager;
    private MomcaConnection momcaConnection;

    @BeforeClass
    public void setUp() throws Exception {
        momcaConnection = TestUtils.initMomcaConnection();
        hierarchyManager = momcaConnection.getHierarchyManager();
        assertNotNull(hierarchyManager, "MOM-CA connection not initialized.");
    }

    @Test
    public void testAddCountry() throws Exception {

        String code = "AT";
        String nativeform = "Österreich";

        Country newCountry = hierarchyManager.addCountry(code, nativeform);

        assertEquals(newCountry.getCode(), code);
        assertEquals(newCountry.getNativeform(), nativeform);

        hierarchyManager.deleteCountry(code);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddCountryThatExists() throws Exception {
        String existingCode = "DE";
        String nativeform = "Österreich";
        hierarchyManager.addCountry(existingCode, nativeform);
    }

    @Test
    public void testChangeCountryCode() throws Exception {

        String originalCode = "CH";
        Country originalCountry = hierarchyManager.getCountry(originalCode).get();

        String newCode = "Schw";

        Country updatedCountry = hierarchyManager.changeCountryCode(originalCountry, newCode);
        hierarchyManager.changeCountryCode(updatedCountry, originalCode);

        assertEquals(updatedCountry.getCode(), newCode);

    }

    @Test
    public void testChangeCountryNativeform() throws Exception {

        String code = "CH";
        Country originalCountry = hierarchyManager.getCountry(code).get();

        String originalNativeform = "Schweiz";
        String newNativeform = "Svizzera";

        Country updatedCountry = hierarchyManager.changeCountryNativeform(originalCountry, newNativeform);
        hierarchyManager.changeCountryNativeform(updatedCountry, originalNativeform);

        assertEquals(updatedCountry.getNativeform(), newNativeform);

    }

    @Test
    public void testChangeSubdivisionCode() throws Exception {

        String countryCode = "DE";
        Country originalCountry = hierarchyManager.getCountry(countryCode).get();

        String originalSubdivisionCode = "DE-BY";
        String newSubdivisionCode = "DE-BAY";

        Country updatedCountry = hierarchyManager
                .changeSubdivisionCode(originalCountry, originalSubdivisionCode, newSubdivisionCode);
        hierarchyManager.changeSubdivisionCode(updatedCountry, newSubdivisionCode, originalSubdivisionCode);

        assertEquals(updatedCountry.getSubdivisions().stream()
                .filter(subdivision -> subdivision.getCode().equals(newSubdivisionCode)).count(), 1);


    }

    @Test
    public void testChangeSubdivisionNativeform() throws Exception {
        String code = "DE";
        Country originalCountry = hierarchyManager.getCountry(code).get();

        String originalNativeform = "Bayern";
        String newNativeform = "Bavaria";

        Country updatedCountry = hierarchyManager
                .changeSubdivisionNativeform(originalCountry, originalNativeform, newNativeform);
        hierarchyManager.changeSubdivisionNativeform(updatedCountry, newNativeform, originalNativeform);

        assertEquals(updatedCountry.getSubdivisions().stream()
                .filter(subdivision -> subdivision.getNativeform().equals(newNativeform)).count(), 1);


    }

    @Test
    public void testDeleteCountry() throws Exception {
        // TODO add code
        assertTrue(false);
    }

    @Test
    public void testGetCountry() throws Exception {

        Optional<Country> countryOptional = hierarchyManager.getCountry("DE");
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
    public void testListCountries() throws Exception {
        assertEquals(hierarchyManager.listCountries().toString(), "[CH, DE, RS]");
    }


}