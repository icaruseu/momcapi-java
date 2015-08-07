package eu.icarus.momca.momcapi.query;

import eu.icarus.momca.momcapi.TestUtils;
import eu.icarus.momca.momcapi.resource.MomcaResource;
import nu.xom.Nodes;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    private MomcaResource resource;

    @BeforeClass
    public void setUp() throws Exception {
        String testXml = TestUtils.getXmlFromResource("XpathQueryTestXml.xml").toXML();
        resource = new MomcaResource(NAME, PARENT_URI, testXml);
    }

    @Test
    public void testQUERY_ATOM_EMAIL() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_ATOM_EMAIL);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "atomemail");
    }

    @Test
    public void testQUERY_ATOM_ID() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_ATOM_ID);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "atomid");
    }

    @Test
    public void testQUERY_CEI_BODY_IDNO_TEXT() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_CEI_BODY_IDNO_TEXT);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "idnotext");
    }

    @Test
    public void testQUERY_CEI_ISSUED() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_CEI_ISSUED);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "ceiissued");
    }

    @Test
    public void testQUERY_CEI_TEXT() throws Exception {
        Nodes result = queryContentAsNodes(resource, XpathQuery.QUERY_CEI_TEXT);
        assertEquals(result.size(), 1);
    }

    @Test
    public void testQUERY_CEI_WITNESS_ORIG_FIGURE() throws Exception {

        String expected1 = "<cei:figure n=\"nvalue1\">\n" +
                "                <cei:graphic url=\"urlvalue1\">textvalue1</cei:graphic>\n" +
                "            </cei:figure>";
        String expected2 = "<cei:figure>\n" +
                "                <cei:graphic url=\"urlvalue1\" />\n" +
                "            </cei:figure>";

        Nodes result = queryContentAsNodes(resource, XpathQuery.QUERY_CEI_WITNESS_ORIG_FIGURE);

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).toXML(), expected1);
        assertEquals(result.get(1).toXML(), expected2);

    }

    @Test
    public void testQUERY_CEI__BODY_IDNO_ID() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_CEI_BODY_IDNO_ID);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "idnoid");
    }

    @Test
    public void testQUERY_CONFIG_GROUP_NAME() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_CONFIG_GROUP_NAME);
        List<String> expectedResult = new ArrayList<>(2);
        expectedResult.add("atom");
        expectedResult.add("guest");
        assertEquals(result, expectedResult);
    }

    @Test
    public void testQUERY_CONFIG_NAME() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_CONFIG_NAME);
        String expectedResult = "configname";
        assertEquals(result.get(0), expectedResult);
    }

    @Test
    public void testQUERY_EAG_AUTFORM() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_EAG_AUTFORM);
        String expectedResult = "Klosterarchiv Einsiedeln";
        assertEquals(result.get(0), expectedResult);
    }

    @Test
    public void testQUERY_EAG_DESC() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_EAG_DESC);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "eagdesc");
    }

    @Test
    public void testQUERY_EAG_REPOSITORID() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_EAG_REPOSITORID);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "CH-KAE");
    }

    @Test
    public void testQUERY_NAME() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_NAME);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "name");
    }

    @Test
    public void testQUERY_XRX_BOOKMARK() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_XRX_BOOKMARK);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "xrxbookmark");
    }

    @Test
    public void testQUERY_XRX_EMAIL() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_XRX_EMAIL);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "xrxemail");
    }

    @Test
    public void testQUERY_XRX_MODERATOR() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_XRX_MODERATOR);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "xrxmoderator");
    }

    @Test
    public void testQUERY_XRX_NAME() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_XRX_NAME);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "xrxname");
    }

    @Test
    public void testQUERY_XRX_SAVED_ID() throws Exception {
        List<String> result = queryContentAsList(resource, XpathQuery.QUERY_XRX_SAVED_ID);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), "xrxsaved");
    }

    private List<String> queryContentAsList(@NotNull MomcaResource resource, @NotNull XpathQuery query)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method queryMethod = MomcaResource.class.
                getDeclaredMethod("queryContentAsList", XpathQuery.class);
        queryMethod.setAccessible(true);
        return (List<String>) queryMethod.invoke(resource, query);

    }

    private Nodes queryContentAsNodes(@NotNull MomcaResource resource, @NotNull XpathQuery query)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method queryMethod = MomcaResource.class.
                getDeclaredMethod("queryContentAsNodes", XpathQuery.class);
        queryMethod.setAccessible(true);
        return (Nodes) queryMethod.invoke(resource, query);

    }

}