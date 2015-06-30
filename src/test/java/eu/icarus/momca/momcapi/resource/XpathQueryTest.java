package eu.icarus.momca.momcapi.resource;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 28.06.2015.
 */
public class XpathQueryTest {

    @NotNull
    private static final String NAME = "testfile.xml";
    @NotNull
    private static final String PARENT_URI = "/db/mom-data/";
    @NotNull
    private static final String XML_CONTENT = "<testxml> <atom:atom xmlns:atom=\"http://www.w3.org/2005/Atom\"> <atom:id>atomid</atom:id> </atom:atom> <name>name</name> <xrx:xrx xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"> <xrx:bookmark>xrxbookmark</xrx:bookmark> <xrx:email>xrxemail</xrx:email> <xrx:name>xrxname</xrx:name> <xrx:saved><xrx:id>xrxsaved</xrx:id></xrx:saved> </xrx:xrx> </testxml>";
    private ExistResource resource;

    @BeforeClass
    public void setUp() throws Exception {
        resource = new ExistResource(NAME, PARENT_URI, XML_CONTENT);
    }

    @Test
    public void testQUERY_ATOM_ID() throws Exception {
        List<String> result = resource.queryContentXml(XpathQuery.QUERY_ATOM_ID);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "atomid");
    }

    @Test
    public void testQUERY_NAME() throws Exception {
        List<String> result = resource.queryContentXml(XpathQuery.QUERY_NAME);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "name");
    }

    @Test
    public void testQUERY_XRX_BOOKMARK() throws Exception {
        List<String> result = resource.queryContentXml(XpathQuery.QUERY_XRX_BOOKMARK);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "xrxbookmark");
    }

    @Test
    public void testQUERY_XRX_EMAIL() throws Exception {
        List<String> result = resource.queryContentXml(XpathQuery.QUERY_XRX_EMAIL);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "xrxemail");
    }

    @Test
    public void testQUERY_XRX_NAME() throws Exception {
        List<String> result = resource.queryContentXml(XpathQuery.QUERY_XRX_NAME);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "xrxname");
    }

    @Test
    public void testQUERY_XRX_SAVED() throws Exception {
        List<String> result = resource.queryContentXml(XpathQuery.QUERY_XRX_SAVED);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "xrxsaved");
    }

}