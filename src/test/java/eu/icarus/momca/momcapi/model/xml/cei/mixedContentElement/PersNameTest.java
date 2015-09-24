package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 13/09/2015.
 */
public class PersNameTest {

    @Test
    public void test() throws Exception {

        PersName persName = new PersName("Josef");

        assertEquals(persName.getContent(), "Josef");
        assertFalse(persName.getCertainty().isPresent());
        assertFalse(persName.getReg().isPresent());
        assertFalse(persName.getType().isPresent());

        String correctXml = "<cei:persName xmlns:cei=\"http://www.monasterium.net/NS/cei\">Josef</cei:persName>";
        assertEquals(persName.toXML(), correctXml);

        persName = new PersName("Carolus", "100%", "Karl", "König", "");

        assertEquals(persName.getContent(), "Carolus");
        assertTrue(persName.getCertainty().isPresent());
        assertEquals(persName.getCertainty().get(), "100%");
        assertTrue(persName.getReg().isPresent());
        assertEquals(persName.getReg().get(), "Karl");
        assertTrue(persName.getType().isPresent());
        assertEquals(persName.getType().get(), "König");


        correctXml = "<cei:persName xmlns:cei=\"http://www.monasterium.net/NS/cei\" certainty=\"100%\" reg=\"Karl\" type=\"König\">Carolus</cei:persName>";
        assertEquals(persName.toXML(), correctXml);

    }

}