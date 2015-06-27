package eu.icarus.momca.momcapi.resource;

import eu.icarus.momca.momcapi.Namespace;
import nu.xom.Builder;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 27.06.2015.
 */
public class ExistResourceTest {

    private static final String name = "admin.xml";
    private static final String parentUri = "/db/mom-data/xrx.user";
    private static final String queryResult = "Mustermann";
    private static final String uri = String.format("%s/%s", parentUri, name);
    private static final String xmlContentWithNamespace = "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"> <xrx:username /> <xrx:password /> <xrx:firstname>Max</xrx:firstname> <xrx:name>Mustermann</xrx:name> <xrx:email>admin</xrx:email> <xrx:moderator>admin</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:annotations /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>";
    private static final String xmlContentWithoutNamespace = "<user><name>Mustermann</name></user>";

    @Test
    public void testConstructorWithExistResource() throws Exception {
        ExistResource resOrig = new ExistResource(name, parentUri, xmlContentWithNamespace);
        ExistResource resNew = new ExistResource(resOrig);
        assertEquals(resNew.getUri(), resOrig.getUri());
    }

    @Test
    public void testConstructorWithNameCollectionAndContent() throws Exception {
        ExistResource res = new ExistResource(name, parentUri, xmlContentWithNamespace);
        assertEquals(res.getUri(), uri);
    }

    @Test
    public void testGetParentCollectionUri() throws Exception {
        ExistResource res = new ExistResource(name, parentUri, xmlContentWithNamespace);
        assertEquals(res.getParentUri(), parentUri);
    }

    @Test
    public void testGetResourceName() throws Exception {
        ExistResource res = new ExistResource(name, parentUri, xmlContentWithNamespace);
        assertEquals(res.getName(), name);
    }

    @Test
    public void testGetUri() throws Exception {
        ExistResource res = new ExistResource(name, parentUri, xmlContentWithNamespace);
        assertEquals(res.getUri(), uri);
    }

    @Test
    public void testGetXmlAsDocument() throws Exception {
        Builder parser = new Builder();
        String origXml = parser.build(xmlContentWithNamespace, null).toXML();
        ExistResource res = new ExistResource(name, parentUri, xmlContentWithNamespace);
        assertEquals(res.getXmlAsDocument().toXML(), origXml);
    }

    @Test
    public void testQueryContentXmlWithNamespace() throws Exception {
        ExistResource res = new ExistResource(name, parentUri, xmlContentWithNamespace);
        assertEquals(res.queryContentXml("//xrx:name", Namespace.XRX).get(0), queryResult);

    }

    @Test
    public void testQueryContentXmlWithoutNamespace() throws Exception {
        ExistResource res = new ExistResource(name, parentUri, xmlContentWithoutNamespace);
        assertEquals(res.queryContentXml("//name").get(0), queryResult);
    }

}