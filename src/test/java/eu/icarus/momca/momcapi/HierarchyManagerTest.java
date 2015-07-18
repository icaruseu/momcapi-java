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
    public void testChangeCountryCode() throws Exception {

        String originalCode = "CH";
        Country country = hierarchyManager.getCountry(originalCode).get();

        String newCode = "Schw";
        Country updatedCountry = hierarchyManager.changeCountryCode(country, newCode);

        assertEquals(updatedCountry.getCode(), newCode);

        hierarchyManager.changeCountryCode(updatedCountry, originalCode);

    }

    @Test
    public void testChangeSubdivisionCode() throws Exception {

        String countryCode = "DE";
        Country country = hierarchyManager.getCountry(countryCode).get();

        String originalSubdivisionCode = "DE-BY";
        String newSubdivisionCode = "DE-BAY";
        Country updatedCountry = hierarchyManager.changeSubdivisionCode(country, originalSubdivisionCode, newSubdivisionCode);

        assertEquals(updatedCountry.getSubdivisions().stream()
                .filter(subdivision -> subdivision.getCode().equals(newSubdivisionCode)).count(), 1);

        hierarchyManager.changeSubdivisionCode(updatedCountry, newSubdivisionCode, originalSubdivisionCode);

    }

    @Test
    public void testGetCountry() throws Exception {

        Optional<Country> countryOptional = hierarchyManager.getCountry("DE");
        assertTrue(countryOptional.isPresent());

        Country country = countryOptional.get();
        assertEquals(country.getCode(), "DE");
        assertEquals(country.getNativeForm(), "Deutschland");
        assertEquals(country.toXML(), "<eap:country xmlns:eap=\"http://www.monasterium.net/NS/eap\">" +
                "<eap:code>DE</eap:code><eap:nativeform>Deutschland</eap:nativeform>" +
                "<eap:subdivisions><eap:subdivision><eap:code>DE-BW</eap:code><eap:nativeform>" +
                "Baden-Württemberg</eap:nativeform></eap:subdivision><eap:subdivision><eap:code>DE-BY</eap:code>" +
                "<eap:nativeform>Bayern</eap:nativeform></eap:subdivision></eap:subdivisions></eap:country>");

        List<Subdivision> subdivisions = country.getSubdivisions();
        assertEquals(subdivisions.size(), 2);

        assertEquals(subdivisions.get(0).getCode(), "DE-BW");
        assertEquals(subdivisions.get(0).getNativeForm(), "Baden-Württemberg");


        assertEquals(subdivisions.get(1).getCode(), "DE-BY");
        assertEquals(subdivisions.get(1).getNativeForm(), "Bayern");

    }

    @Test
    public void testListCountries() throws Exception {
        assertEquals(hierarchyManager.listCountries().toString(), "[CH, DE, RS]");
    }


}