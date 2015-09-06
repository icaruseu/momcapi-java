package eu.icarus.momca.momcapi.model.xml.ead;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Created by djell on 06/09/2015.
 */
public class DescriptiveElementTest {


    @Test
    public void testWithContent() throws Exception {

        String heading = "Heading";
        String paragraph1 = "Paragraph 1";
        String paragraph2 = "Paragraph 2";
        DescriptiveElement element = new Odd(heading, paragraph1, paragraph2);

        assertEquals(element.getHeading().getText().get(), heading);

        assertEquals(element.getParagraphs().size(), 2);
        assertEquals(element.getParagraphs().get(0).getContent().get(), paragraph1);
        assertEquals(element.getParagraphs().get(1).getContent().get(), paragraph2);

        String xml = "<ead:odd xmlns:ead=\"urn:isbn:1-931666-22-9\"><ead:head>Heading</ead:head><ead:p>Paragraph 1</ead:p><ead:p>Paragraph 2</ead:p></ead:odd>";
        assertEquals(element.toXML(), xml);

    }

    @Test
    public void testWithoutContent() throws Exception {

        DescriptiveElement element = new BiogHist(null);

        assertFalse(element.getHeading().getText().isPresent());

        assertEquals(element.getParagraphs().size(), 1);
        assertFalse(element.getParagraphs().get(0).getContent().isPresent());

        String xml = "<ead:bioghist xmlns:ead=\"urn:isbn:1-931666-22-9\"><ead:head /><ead:p /></ead:bioghist>";
        assertEquals(element.toXML(), xml);

    }

}