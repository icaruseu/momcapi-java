package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.model.ResourceType;
import eu.icarus.momca.momcapi.xml.Namespace;
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
    public static final String ARCHIVE_TEXT = "tag:www.monasterium.net,2011:/archive/RS-IAGNS";
    @NotNull
    public static final String ARCHIVE_XML_STRING = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
            "tag:www.monasterium.net,2011:/archive/RS-IAGNS</atom:id>";
    @NotNull
    public static final String CHARTER_XML_STRING = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
            "tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/AGNS_F.1_the_fascia_9_Sub_3499%7C1817</atom:id>";
    @NotNull
    private static final String CHARTER_TEXT_DECODED = "tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/AGNS_F.1_the_fascia_9_Sub_3499|1817";
    @NotNull
    private static final String CHARTER_TEXT_ENCODED = "tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/AGNS_F.1_the_fascia_9_Sub_3499%7C1817";
    @NotNull
    private static final ResourceType TYPE = ResourceType.CHARTER;
    @NotNull
    private static final Element XML = new Element(String.format("%s:id", Namespace.ATOM.getPrefix()), Namespace.ATOM.getUri());

    @BeforeClass
    public void setUp() throws Exception {
        XML.appendChild(CHARTER_TEXT_ENCODED);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithFaultyId1() throws Exception {
        new AtomId("tag:www.monasterium.net,2011:/archive/RS-IAGNS/Charters");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithFaultyId2() throws Exception {
        new AtomId("tag:www.monasterium.net,2011:/charter/RS-IAGNS");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithFaultyId3() throws Exception {
        new AtomId("tag:www.monasterium.net,2011:/random/RS-IAGNS/Charters");
    }

    @Test
    public void testConstructorWithId() throws Exception {

        AtomId atomId1 = new AtomId(CHARTER_TEXT_DECODED);
        assertEquals(atomId1.getText(), CHARTER_TEXT_ENCODED);
        assertEquals(atomId1.toXML(), CHARTER_XML_STRING);

        AtomId atomId2 = new AtomId(ARCHIVE_TEXT);
        assertEquals(atomId2.getText(), ARCHIVE_TEXT);
        assertEquals(atomId2.toXML(), ARCHIVE_XML_STRING);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongId1() throws Exception {
        new AtomId("tag:www.monasterium.net,2011:/Thisisnotanatomid");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongId2() throws Exception {
        new AtomId("tag:www.monasterium.net,2011:/This/is/not/an/atomid");
    }

    @Test
    public void testConstructorWithoutPrefix() throws Exception {
        AtomId atomId = new AtomId("tag:www.monasterium.net,2011:/charter/Archive/Fond/Charter");
        assertEquals(atomId.toXML(), "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">tag:www.monasterium.net,2011:/charter/Archive/Fond/Charter</atom:id>");
    }

    @Test
    public void testGetText() throws Exception {
        AtomId atomId = new AtomId(CHARTER_TEXT_ENCODED);
        assertEquals(atomId.getText(), CHARTER_TEXT_ENCODED);
    }

    @Test
    public void testGetType() throws Exception {
        AtomId atomId = new AtomId(CHARTER_TEXT_ENCODED);
        assertEquals(atomId.getType(), TYPE);
    }

    @Test
    public void testToXml() throws Exception {
        AtomId atomId = new AtomId(CHARTER_TEXT_ENCODED);
        assertEquals(atomId.toXML(), XML.toXML());
    }

}