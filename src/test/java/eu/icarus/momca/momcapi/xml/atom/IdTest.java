package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.resource.ResourceType;
import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Element;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 27.06.2015.
 */
public class IdTest {

    @NotNull
    private static final String ID = "tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/AGNS_F.1_the_fascia_9_Sub_3499%7C1817";
    @NotNull
    private static final String PREFIX = "tag:www.monasterium.net,2011:";
    @NotNull
    private static final ResourceType TYPE = ResourceType.CHARTER;
    @NotNull
    private static final Element XML = new Element(String.format("%s:id", Namespace.ATOM.getPrefix()), Namespace.ATOM.getUri());

    @BeforeClass
    public void setUp() throws Exception {
        XML.appendChild(ID);
    }

    @Test
    public void testConstructorWithId() throws Exception {
        AtomId atomId = new AtomId(ID);
        assertEquals(atomId.getId(), ID);
        assertEquals(atomId.toXML(), "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/AGNS_F.1_the_fascia_9_Sub_3499%7C1817</atom:id>");
    }

    @Test
    public void testConstructorWithIdParts() throws Exception {

        String expectedEasyId = "tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/F1_fasc.16_sub_N_1513";
        String expectedDifficultId = "tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232"; // %7C is difficult
        AtomId easyAtomId = new AtomId("charter", "RS-IAGNS", "Charters", "F1_fasc.16_sub_N_1513");
        AtomId difficultAtomId = new AtomId("charter", "RS-IAGNS", "Charters", "IAGNS_F-.150_6605|193232"); // | should be replaced with %7C etc.

        assertEquals(easyAtomId.getId(), expectedEasyId);
        assertEquals(difficultAtomId.getId(), expectedDifficultId);

    }

    @Test
    public void testConstructorWithPrefix() throws Exception {
        AtomId atomId = new AtomId(PREFIX, "charter", "Archive", "Fond", "Charter");
        assertEquals(atomId.toXML(), "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">tag:www.monasterium.net,2011:/charter/Archive/Fond/Charter</atom:id>");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongId() throws Exception {
        new AtomId("Thisisnotanatomid");
    }

    @Test
    public void testConstructorWithoutPrefix() throws Exception {
        AtomId atomId = new AtomId("charter", "Archive", "Fond", "Charter");
        assertEquals(atomId.toXML(), "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">tag:www.monasterium.net,2011:/charter/Archive/Fond/Charter</atom:id>");
    }

    @Test
    public void testGetId() throws Exception {
        AtomId atomId = new AtomId(ID);
        assertEquals(atomId.getId(), ID);
    }

    @Test
    public void testGetType() throws Exception {
        AtomId atomId = new AtomId(ID);
        assertEquals(atomId.getType(), TYPE);
    }

    @Test
    public void testGetXml() throws Exception {
        AtomId atomId = new AtomId(ID);
        assertEquals(atomId.toXML(), XML.toXML());
    }

}