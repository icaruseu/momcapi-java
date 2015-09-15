package eu.icarus.momca.momcapi.model.xml.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 13/09/2015.
 */
public class IndexTest {

    @Test
    public void test() throws Exception {

        Index index = new Index("Urkunde");

        assertEquals(index.getContent(), "Urkunde");
        assertFalse(index.getIndexName().isPresent());
        assertFalse(index.getLemma().isPresent());
        assertFalse(index.getSublemma().isPresent());

        String correctXml = "<cei:index xmlns:cei=\"http://www.monasterium.net/NS/cei\">Urkunde</cei:index>";
        assertEquals(index.toXML(), correctXml);

        index = new Index("Waldi", "Tiere", "Hund", "Dackel");

        assertEquals(index.getContent(), "Waldi");
        assertTrue(index.getIndexName().isPresent());
        assertEquals(index.getIndexName().get(), "Tiere");
        assertTrue(index.getLemma().isPresent());
        assertEquals(index.getLemma().get(), "Hund");
        assertTrue(index.getSublemma().isPresent());
        assertEquals(index.getSublemma().get(), "Dackel");


        correctXml = "<cei:index xmlns:cei=\"http://www.monasterium.net/NS/cei\" indexName=\"Tiere\" lemma=\"Hund\" sublemma=\"Dackel\">Waldi</cei:index>";
        assertEquals(index.toXML(), correctXml);

    }

}