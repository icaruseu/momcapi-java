package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.query.XpathQuery;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 27.06.2015.
 */
public class MomcaResourceTest {

    @NotNull
    private static final String NAME = "admin.xml";
    @NotNull
    private static final String PARENT_URI = "/db/mom-data/xrx.user";
    @NotNull
    private static final String QUERY_RESULT = "Mustermann";
    @NotNull
    private static final String URI = String.format("%s/%s", PARENT_URI, NAME);
    @NotNull
    private static final String XML_CONTENT_WITHOUT_NAMESPACE = "<user><name>Mustermann</name></user>";
    @NotNull
    private static final String XML_CONTENT_WITH_NAMESPACE = "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"> <xrx:username /> <xrx:password /> <xrx:firstname>Max</xrx:firstname> <xrx:name>Mustermann</xrx:name> <xrx:email>admin</xrx:email> <xrx:moderator>admin</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:annotations /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>";

    @Test
    public void testConstructorWithExistResource() throws Exception {
        MomcaResource resOrig = new MomcaResource(NAME, PARENT_URI, XML_CONTENT_WITH_NAMESPACE);
        MomcaResource resNew = new MomcaResource(resOrig);
        assertEquals(resNew.getUri(), resOrig.getUri());
    }

    @Test
    public void testConstructorWithNameCollectionAndContent() throws Exception {
        MomcaResource res = new MomcaResource(NAME, PARENT_URI, XML_CONTENT_WITH_NAMESPACE);
        assertEquals(res.getUri(), URI);
    }

    @Test
    public void testGetParentCollectionUri() throws Exception {
        MomcaResource res = new MomcaResource(NAME, PARENT_URI, XML_CONTENT_WITH_NAMESPACE);
        assertEquals(res.getParentUri(), PARENT_URI);
    }

    @Test
    public void testGetResourceName() throws Exception {
        MomcaResource res = new MomcaResource(NAME, PARENT_URI, XML_CONTENT_WITH_NAMESPACE);
        assertEquals(res.getResourceName(), NAME);
    }

    @Test
    public void testGetUri() throws Exception {
        MomcaResource res = new MomcaResource(NAME, PARENT_URI, XML_CONTENT_WITH_NAMESPACE);
        assertEquals(res.getUri(), URI);
    }

    @Test
    public void testGetXmlAsDocument() throws Exception {
        String origXml = Util.parseToDocument(XML_CONTENT_WITH_NAMESPACE).getDocument().toXML();
        MomcaResource res = new MomcaResource(NAME, PARENT_URI, XML_CONTENT_WITH_NAMESPACE);
        assertEquals(res.getXmlAsDocument().toXML(), origXml);
    }

    @Test
    public void testQueryContentXmlWithNamespace() throws Exception {
        MomcaResource res = new MomcaResource(NAME, PARENT_URI, XML_CONTENT_WITH_NAMESPACE);
        assertEquals(res.queryContentAsList(XpathQuery.QUERY_XRX_NAME).get(0), QUERY_RESULT);

    }

    @Test
    public void testQueryContentXmlWithoutNamespace() throws Exception {
        MomcaResource res = new MomcaResource(NAME, PARENT_URI, XML_CONTENT_WITHOUT_NAMESPACE);
        assertEquals(res.queryContentAsList(XpathQuery.QUERY_NAME).get(0), QUERY_RESULT);
    }

}