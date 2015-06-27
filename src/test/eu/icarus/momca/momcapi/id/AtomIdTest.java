package eu.icarus.momca.momcapi.id;

import eu.icarus.momca.momcapi.Namespace;
import eu.icarus.momca.momcapi.resource.ResourceType;
import nu.xom.Element;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 27.06.2015.
 */
public class AtomIdTest {

    private static final String atomId = "tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1";
    private static final String prefix = "tag:www.monasterium.net,2011:";
    private static final ResourceType type = ResourceType.CHARTER;
    private static final Element xml = new Element(String.format("%s:id", Namespace.ATOM.getPrefix()), Namespace.ATOM.getUri());

    @BeforeClass
    public void setUp() throws Exception {
        xml.appendChild(atomId);
    }

    @Test
    public void testConstructor() throws Exception {
        AtomId id = new AtomId(atomId);
        assertEquals(id.getAtomId(), atomId);
    }

    @Test
    public void testGetAtomId() throws Exception {
        AtomId id = new AtomId(atomId);
        assertEquals(id.getAtomId(), atomId);
    }

    @Test
    public void testGetPrefix() throws Exception {
        AtomId id = new AtomId(atomId);
        assertEquals(id.getPrefix(), prefix);
    }

    @Test
    public void testGetType() throws Exception {
        AtomId id = new AtomId(atomId);
        assertEquals(id.getType(), type);
    }

    @Test
    public void testGetXml() throws Exception {
        AtomId id = new AtomId(atomId);
        assertEquals(id.getXml().toString(), xml.toString());
    }
}