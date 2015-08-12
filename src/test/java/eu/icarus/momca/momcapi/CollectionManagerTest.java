package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.resource.Collection;
import eu.icarus.momca.momcapi.resource.Country;
import eu.icarus.momca.momcapi.resource.CountryCode;
import eu.icarus.momca.momcapi.resource.Region;
import eu.icarus.momca.momcapi.xml.atom.IdCollection;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

/**
 * Created by djell on 11/08/2015.
 */
public class CollectionManagerTest {

    private CollectionManager cm;
    private MomcaConnection mc;

    @Test
    public void testGetCollection() throws Exception {

        Collection collection1 = cm.getCollection(new IdCollection("AbteiEberbach")).get();
        assertEquals(collection1.getCountryCode().get().getCode(), "DE");
        assertEquals(collection1.getRegionName().get(), "Bayern");
        assertEquals(collection1.getId().getId(), "tag:www.monasterium.net,2011:/collection/AbteiEberbach");
        assertEquals(collection1.getIdentifier(), "AbteiEberbach");
        assertEquals(collection1.getName(), "Urkundenbuch der Abtei Eberbach (Google data)");

        Collection collection2 = cm.getCollection(new IdCollection("MedDocBulgEmp")).get();
        assertEquals(collection2.getCountryCode().get().getCode(), "BG");
        assertFalse(collection2.getRegionName().isPresent());
        assertEquals(collection2.getId().getId(), "tag:www.monasterium.net,2011:/collection/MedDocBulgEmp");
        assertEquals(collection2.getIdentifier(), "MedDocBulgEmp");
        assertEquals(collection2.getName(), "Bulgarian Medieval Documents: The Second Bulgarian Empire");

    }

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