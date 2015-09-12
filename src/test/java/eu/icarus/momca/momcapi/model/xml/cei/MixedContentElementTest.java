package eu.icarus.momca.momcapi.model.xml.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 12/09/2015.
 */
public class MixedContentElementTest {

    @Test
    public void testConstructor() throws Exception {

        String xmlString = "Ludwig der Fromme bestätigt der Kirche von Salzburg auf Bitten Erzbischof Arns laut der vorgelegten Urkunde seines Vaters, Kaiser Karls, Immunität mit Königsschutz und ihre Besitzungen. <issuer>Ludwig der Fromme</issuer>";

        MixedContentElement element1 = new Abstract(xmlString);
        MixedContentElement element2 = new Abstract("<abstract>" + xmlString + "</abstract>");
        MixedContentElement element3 = new Abstract(
                "<abstract xmlns=\"http://www.monasterium.net/NS/cei\">" + xmlString + "</abstract>");
        MixedContentElement element4 = new Abstract(
                "<cei:abstract xmlns:cei=\"http://www.monasterium.net/NS/cei\">" + xmlString + "</cei:abstract>");

        String correctXml = "<abstract xmlns=\"http://www.monasterium.net/NS/cei\">" + xmlString + "</abstract>";

        assertEquals(element1.toXML(), correctXml);
        assertEquals(element2.toXML(), correctXml);
        assertEquals(element3.toXML(), correctXml);
        assertEquals(element4.toXML(), correctXml);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyContent() throws Exception {
        new Abstract("");
    }
    
}