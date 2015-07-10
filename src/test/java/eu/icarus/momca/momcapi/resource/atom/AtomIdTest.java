package eu.icarus.momca.momcapi.resource.atom;

import eu.icarus.momca.momcapi.resource.Namespace;
import eu.icarus.momca.momcapi.resource.ResourceType;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 27.06.2015.
 */
public class AtomIdTest {

    @NotNull
    private static final String ATOM_ID = "tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/AGNS_F.1_the_fascia_9_Sub_3499%7C1817";
    @NotNull
    private static final String PREFIX = "tag:www.monasterium.net,2011:";
    @NotNull
    private static final ResourceType TYPE = ResourceType.CHARTER;
    @NotNull
    private static final Element XML = new Element(String.format("%s:id", Namespace.ATOM.getPrefix()), Namespace.ATOM.getUri());

    @BeforeClass
    public void setUp() throws Exception {
        XML.appendChild(ATOM_ID);
    }

    @Test
    public void testConstructorWithAtomId() throws Exception {
        AtomId id = new AtomId(ATOM_ID);
        assertEquals(id.getAtomId(), ATOM_ID);
        assertEquals(id.toXML(), "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/AGNS_F.1_the_fascia_9_Sub_3499%7C1817</atom:id>");
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