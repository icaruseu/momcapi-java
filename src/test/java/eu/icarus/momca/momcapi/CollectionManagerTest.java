package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.resource.Country;
import eu.icarus.momca.momcapi.resource.CountryCode;
import eu.icarus.momca.momcapi.resource.Region;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by djell on 11/08/2015.
 */
public class CollectionManagerTest {

    private CollectionManager cm;
    private MomcaConnection mc;

    @BeforeClass
    public void setUp() throws Exception {
        mc = TestUtils.initMomcaConnection();
        cm = mc.getCollectionManager();
        assertNotNull(cm, "MOM-CA connection not initialized.");
    }

    @Test
    public void testListCollections() throws Exception {
        assertEquals(cm.listCollections().size(), 2);
    }

    @Test
    public void testListCollectionsForCountry() throws Exception {
        Country country = mc.getCountryManager().getCountry(new CountryCode("BG")).get();
        assertEquals(cm.listCollections(country).size(), 1);
    }

    @Test
    public void testListCollectionsForRegion() throws Exception {
        Country country = mc.getCountryManager().getCountry(new CountryCode("DE")).get();
        Region region = country.getRegions().stream().filter(r -> r.getNativeName().equals("Bayern")).collect(Collectors.toList()).get(0);
        assertEquals(cm.listCollections(region).size(), 1);
    }

}