package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.FigDesc;
import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Zone;
import nu.xom.Element;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by djell on 28/09/2015.
 */
public class FigureTest {

    @Test
    public void testConstructor1() throws Exception {

        Figure figure = new Figure("url");

        assertFalse(figure.getFigDesc().isPresent());
        assertTrue(figure.getGraphic().isPresent());
        assertTrue(figure.getZones().isEmpty());

        assertEquals(figure.getUrl(), "url");

        assertEquals(figure.toXML(), "<cei:figure xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:graphic url=\"url\" /></cei:figure>");

    }

    @Test
    public void testConstructor2() throws Exception {

        FigDesc figDesc = new FigDesc("figDesc", "facs", "id", "n");
        Graphic graphic = new Graphic("facs", "id", "n", "url");
        Zone zone = new Zone("zone", "facs", "id", "n");
        List<Zone> zones = new ArrayList<>(1);
        zones.add(zone);

        Figure figure = new Figure(figDesc, graphic, zones, "facs", "id", "n");

        assertTrue(figure.getFigDesc().isPresent());
        assertTrue(figure.getGraphic().isPresent());
        assertFalse(figure.getZones().isEmpty());

        assertEquals(figure.getUrl(), "url");

        assertEquals(figure.toXML(), "<cei:figure xmlns:cei=\"http://www.monasterium.net/NS/cei\" facs=\"facs\" id=\"id\" n=\"n\"><cei:figDesc facs=\"facs\" id=\"id\" n=\"n\">figDesc</cei:figDesc><cei:graphic facs=\"facs\" id=\"id\" n=\"n\" url=\"url\" /><cei:zone facs=\"facs\" id=\"id\" n=\"n\">zone</cei:zone></cei:figure>");

    }

    @Test
    public void testConstructor3() throws Exception {

        Element figureElement = Util.parseToElement("<cei:figure xmlns:cei=\"http://www.monasterium.net/NS/cei\" facs=\"facs\" id=\"id\" n=\"n\"><cei:figDesc facs=\"facs\" id=\"id\" n=\"n\">figDesc</cei:figDesc><cei:graphic facs=\"facs\" id=\"id\" n=\"n\" url=\"url\" /><cei:zone facs=\"facs\" id=\"id\" n=\"n\">zone</cei:zone></cei:figure>");
        Figure figure = new Figure(figureElement);

        assertTrue(figure.getFigDesc().isPresent());
        assertTrue(figure.getGraphic().isPresent());
        assertFalse(figure.getZones().isEmpty());

        assertEquals(figure.getUrl(), "url");

        assertEquals(figure.toXML(), "<cei:figure xmlns:cei=\"http://www.monasterium.net/NS/cei\" facs=\"facs\" id=\"id\" n=\"n\"><cei:figDesc facs=\"facs\" id=\"id\" n=\"n\">figDesc</cei:figDesc><cei:graphic facs=\"facs\" id=\"id\" n=\"n\" url=\"url\" /><cei:zone facs=\"facs\" id=\"id\" n=\"n\">zone</cei:zone></cei:figure>");

    }

}