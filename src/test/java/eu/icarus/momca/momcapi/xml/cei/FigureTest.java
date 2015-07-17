package eu.icarus.momca.momcapi.xml.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 09.07.2015.
 */
public class FigureTest {

    private static final Figure CEI_FIGURE = new Figure("urlvalue", "nvalue", "textvalue");

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyUrl() throws Exception {
        new Figure("");
    }

    @Test
    public void testConstructorFull() throws Exception {
        assertEquals(CEI_FIGURE.toXML(), "<cei:figure xmlns:cei=\"http://www.monasterium.net/NS/cei\" n=\"nvalue\"><cei:graphic url=\"urlvalue\">textvalue</cei:graphic></cei:figure>");
    }

    @Test
    public void testConstructorWithOnlyUrl() throws Exception {
        assertEquals(new Figure("onlyUrl").toXML(), "<cei:figure xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:graphic url=\"onlyUrl\" /></cei:figure>");
    }

    @Test
    public void testGetN() throws Exception {
        assertEquals(CEI_FIGURE.getN(), "nvalue");
    }

    @Test
    public void testGetText() throws Exception {
        assertEquals(CEI_FIGURE.getText(), "textvalue");
    }

    @Test
    public void testGetUrl() throws Exception {
        assertEquals(CEI_FIGURE.getUrl(), "urlvalue");
    }

    @Test
    public void testHasAbsoluteUrl() throws Exception {
        assertFalse(CEI_FIGURE.hasAbsoluteUrl());
        assertTrue(new Figure("http://images.monasterium.net/img/AT-DASP/Urkunden/Urk1_r.jpg").hasAbsoluteUrl());
    }

}