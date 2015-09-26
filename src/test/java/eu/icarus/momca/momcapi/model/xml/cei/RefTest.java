package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.Util;
import nu.xom.Element;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 25/09/2015.
 */
public class RefTest {

    public static final String FACS = "facs";
    public static final String ID = "id";
    public static final String KEY = "key";
    public static final String LANG = "lang";
    public static final String N = "n";
    public static final String RESP = "resp";
    public static final String TARGET = "target";
    public static final String TEXT = "text";
    public static final String TYPE = "type";
    public static final String XML_FULL = "<cei:ref xmlns:cei=\"http://www.monasterium.net/NS/cei\" facs=\"facs\" id=\"id\" key=\"key\" lang=\"lang\" n=\"n\" resp=\"resp\" target=\"target\" type=\"type\">text</cei:ref>";
    public static final String XML_SIMPLE = "<cei:ref xmlns:cei=\"http://www.monasterium.net/NS/cei\" target=\"target\" />";

    @Test
    public void testConstructor1() throws Exception {

        Ref ref = new Ref("target");

        assertFalse(ref.getText().isPresent());
        assertFalse(ref.getFacs().isPresent());
        assertFalse(ref.getId().isPresent());
        assertFalse(ref.getKey().isPresent());
        assertFalse(ref.getLang().isPresent());
        assertFalse(ref.getN().isPresent());
        assertFalse(ref.getResp().isPresent());
        assertTrue(ref.getTarget().isPresent());
        assertFalse(ref.getText().isPresent());
        assertFalse(ref.getType().isPresent());

        assertEquals(ref.getTarget().get(), TARGET);

        assertEquals(ref.toXML(), XML_SIMPLE);

    }

    @Test
    public void testConstructor2() throws Exception {

        Ref ref = new Ref(TEXT, FACS, ID, KEY, LANG, N, RESP, TARGET, TYPE);

        assertTrue(ref.getText().isPresent());
        assertTrue(ref.getFacs().isPresent());
        assertTrue(ref.getId().isPresent());
        assertTrue(ref.getKey().isPresent());
        assertTrue(ref.getLang().isPresent());
        assertTrue(ref.getN().isPresent());
        assertTrue(ref.getResp().isPresent());
        assertTrue(ref.getTarget().isPresent());
        assertTrue(ref.getText().isPresent());
        assertTrue(ref.getType().isPresent());

        assertEquals(ref.getText().get(), TEXT);
        assertEquals(ref.getFacs().get(), FACS);
        assertEquals(ref.getId().get(), ID);
        assertEquals(ref.getKey().get(), KEY);
        assertEquals(ref.getLang().get(), LANG);
        assertEquals(ref.getN().get(), N);
        assertEquals(ref.getResp().get(), RESP);
        assertEquals(ref.getTarget().get(), TARGET);
        assertEquals(ref.getText().get(), TEXT);
        assertEquals(ref.getType().get(), TYPE);

        assertEquals(ref.toXML(), XML_FULL);

    }

    @Test
    public void testConstructor3() throws Exception {

        Element refElement = Util.parseToElement(XML_FULL);

        Ref ref = new Ref(refElement);

        assertTrue(ref.getText().isPresent());
        assertTrue(ref.getFacs().isPresent());
        assertTrue(ref.getId().isPresent());
        assertTrue(ref.getKey().isPresent());
        assertTrue(ref.getLang().isPresent());
        assertTrue(ref.getN().isPresent());
        assertTrue(ref.getResp().isPresent());
        assertTrue(ref.getTarget().isPresent());
        assertTrue(ref.getText().isPresent());
        assertTrue(ref.getType().isPresent());

        assertEquals(ref.getText().get(), TEXT);
        assertEquals(ref.getFacs().get(), FACS);
        assertEquals(ref.getId().get(), ID);
        assertEquals(ref.getKey().get(), KEY);
        assertEquals(ref.getLang().get(), LANG);
        assertEquals(ref.getN().get(), N);
        assertEquals(ref.getResp().get(), RESP);
        assertEquals(ref.getTarget().get(), TARGET);
        assertEquals(ref.getText().get(), TEXT);
        assertEquals(ref.getType().get(), TYPE);

        assertEquals(ref.toXML(), XML_FULL);

    }

}