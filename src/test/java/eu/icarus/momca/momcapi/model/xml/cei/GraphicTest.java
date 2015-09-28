package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.Util;
import nu.xom.Element;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by djell on 28/09/2015.
 */
public class GraphicTest {

    @Test
    public void testConstructor1() throws Exception {

        Graphic graphic = new Graphic("facs", "id", "n", "url");

        assertTrue(graphic.getFacs().isPresent());
        assertEquals(graphic.getFacs().get(), "facs");
        assertTrue(graphic.getId().isPresent());
        assertEquals(graphic.getId().get(), "id");
        assertTrue(graphic.getN().isPresent());
        assertEquals(graphic.getN().get(), "n");

        assertEquals(graphic.getUrl(), "url");

        assertEquals(graphic.toXML(), "<cei:graphic xmlns:cei=\"http://www.monasterium.net/NS/cei\" facs=\"facs\" id=\"id\" n=\"n\" url=\"url\" />");

    }

    @Test
    public void testConstructor2() throws Exception {

        Element graphicElement = Util.parseToElement("<cei:graphic xmlns:cei=\"http://www.monasterium.net/NS/cei\" facs=\"facs\" id=\"id\" n=\"n\" url=\"url\" />");
        Graphic graphic = new Graphic(graphicElement);

        assertTrue(graphic.getFacs().isPresent());
        assertEquals(graphic.getFacs().get(), "facs");
        assertTrue(graphic.getId().isPresent());
        assertEquals(graphic.getId().get(), "id");
        assertTrue(graphic.getN().isPresent());
        assertEquals(graphic.getN().get(), "n");

        assertEquals(graphic.getUrl(), "url");

        assertEquals(graphic.toXML(), "<cei:graphic xmlns:cei=\"http://www.monasterium.net/NS/cei\" facs=\"facs\" id=\"id\" n=\"n\" url=\"url\" />");

    }
}