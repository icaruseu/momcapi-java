package eu.icarus.momca.momcapi.atomid;

import eu.icarus.momca.momcapi.Namespace;
import eu.icarus.momca.momcapi.resource.ResourceType;
import nu.xom.Element;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by daniel on 27.06.2015.
 */
public class AtomIdTest {

    private static final String ATOM_ID = "tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1";
    private static final String PREFIX = "tag:www.monasterium.net,2011:";
    private static final ResourceType TYPE = ResourceType.CHARTER;
    private static final Element XML = new Element(String.format("%s:id", Namespace.ATOM.getPrefix()), Namespace.ATOM.getUri());

    @BeforeClass
    public void setUp() throws Exception {
        XML.appendChild(ATOM_ID);
    }

    @Test
    public void testConstructorWithAtomId() throws Exception {
        AtomId id = new AtomId(ATOM_ID);
        assertEquals(id.getAtomId(), ATOM_ID);
    }

    @Test
    public void testConstructorWithIdParts() throws Exception {

        String expectedEasyAtomId = "tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/F1_fasc.16_sub_N_1513";
        String expectedDifficultAtomId = "tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232";
        AtomId easyId = new AtomId("charter", "RS-IAGNS", "Charters", "F1_fasc.16_sub_N_1513");
        AtomId difficultId = new AtomId("charter", "RS-IAGNS", "Charters", "IAGNS_F-.150_6605|193232"); // | should be replaced with %7C etc.

        assertEquals(easyId.getAtomId(), expectedEasyAtomId);
        assertEquals(difficultId.getAtomId(), expectedDifficultAtomId);

    }

    @Test
    public void testEquals() throws Exception {
        AtomId id1 = new AtomId(ATOM_ID);
        AtomId id2 = new AtomId(ATOM_ID);
        assertTrue(id1.equals(id2));
    }

    @Test
    public void testGetAtomId() throws Exception {
        AtomId id = new AtomId(ATOM_ID);
        assertEquals(id.getAtomId(), ATOM_ID);
    }

    @Test
    public void testGetPrefix() throws Exception {
        AtomId id = new AtomId(ATOM_ID);
        assertEquals(id.getPrefix(), PREFIX);
    }

    @Test
    public void testGetType() throws Exception {
        AtomId id = new AtomId(ATOM_ID);
        assertEquals(id.getType(), TYPE);
    }

    @Test
    public void testGetXml() throws Exception {
        AtomId id = new AtomId(ATOM_ID);
        assertEquals(id.getXml().toXML(), XML.toXML());
    }

}