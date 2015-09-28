package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.model.xml.Namespace;
import nu.xom.Element;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 03.07.2015.
 */
public class UtilTest {

    @Test
    public void testChangeNamespace() throws Exception {

        String xml = "<cei:archIdentifier xmlns:cei=\"http://www.monasterium.net/NS/cei\"><arch>Kincstári levéltárból (E)</arch><ref target=\"http://archives.hungaricana.hu/en/charters/164\" /></cei:archIdentifier>";
        Element element = Util.parseToElement(xml);
        Util.changeNamespace(element, Namespace.CEI);

    }

    @Test
    public void testDecode() throws Exception {

        String encodedString = "/db/mom-data/AZK%7CAmbroz";
        String decodedString = "/db/mom-data/AZK|Ambroz";
        assertEquals(Util.decode(encodedString), decodedString);

        String nonEncodedString = "User1@monasterium.net";
        assertEquals(Util.decode(nonEncodedString), nonEncodedString);

    }

    @Test
    public void testEncode() throws Exception {

        String nonEncodedString = "User1@monasterium.net";
        assertEquals(Util.encode(nonEncodedString), "User1%40monasterium.net");

        String alreadyEncodedString = "User1%40monasterium.net";
        assertEquals(Util.encode(alreadyEncodedString), "User1%40monasterium.net");

        String nonEncodedPath = "/db/mom-data/AZK|Ambroz";
        assertEquals(Util.encode(nonEncodedPath), "/db/mom-data/AZK%7CAmbroz");

        String alreadyEncodedPath = "/db/mom-data/AZK%7CAmbroz";
        assertEquals(Util.encode(alreadyEncodedPath), "/db/mom-data/AZK%7CAmbroz");

    }

    @Test
    public void testGetLastUriPart() throws Exception {
        String uri = "/db/mom-data/xrx.user/admin.xml";
        String resourceName = "admin.xml";
        assertEquals(Util.getLastUriPart(uri), resourceName);
    }

    @Test
    public void testGetParentUri() throws Exception {
        String uri = "/db/mom-data/xrx.user/admin.xml";
        String parentUri = "/db/mom-data/xrx.user";
        assertEquals(Util.getParentUri(uri), parentUri);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTestIfUri() throws Exception {
        Util.getParentUri("not_an_uri");
    }

}