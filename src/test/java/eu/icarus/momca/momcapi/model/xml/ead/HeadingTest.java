package eu.icarus.momca.momcapi.model.xml.ead;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 06/09/2015.
 */
public class HeadingTest {

    @Test
    public void testGetText() throws Exception {

        String text = "Literatur:";
        Heading heading = new Heading(text);
        assertTrue(heading.getText().isPresent());
        assertEquals(heading.getText().get(), text);

        text = "";
        heading = new Heading(text);
        assertFalse(heading.getText().isPresent());

    }

    @Test
    public void testToXML() throws Exception {

        String text = "Literatur:";
        Heading heading = new Heading(text);
        assertEquals(heading.toXML(), "<ead:head xmlns:ead=\"urn:isbn:1-931666-22-9\">Literatur:</ead:head>");

        text = "";
        heading = new Heading(text);
        assertEquals(heading.toXML(), "<ead:head xmlns:ead=\"urn:isbn:1-931666-22-9\" />");

    }

}