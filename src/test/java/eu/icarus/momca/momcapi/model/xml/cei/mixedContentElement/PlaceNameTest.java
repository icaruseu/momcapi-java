package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 13/09/2015.
 */
public class PlaceNameTest {

    @Test
    public void test() throws Exception {

        PlaceName placeName = new PlaceName("Aachen");

        assertEquals(placeName.getContent(), "Aachen");
        assertFalse(placeName.getCertainty().isPresent());
        assertFalse(placeName.getReg().isPresent());
        assertFalse(placeName.getType().isPresent());

        String correctXml = "<cei:placeName xmlns:cei=\"http://www.monasterium.net/NS/cei\">Aachen</cei:placeName>";
        assertEquals(placeName.toXML(), correctXml);

        placeName = new PlaceName("Iuuauensis", "", "Salzburg", "", "", "");

        assertEquals(placeName.getContent(), "Iuuauensis");
        assertTrue(placeName.getReg().isPresent());
        assertEquals(placeName.getReg().get(), "Salzburg");
        assertFalse(placeName.getCertainty().isPresent());
        assertFalse(placeName.getType().isPresent());

        correctXml = "<cei:placeName xmlns:cei=\"http://www.monasterium.net/NS/cei\" reg=\"Salzburg\">Iuuauensis</cei:placeName>";
        assertEquals(placeName.toXML(), correctXml);

    }

}