package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 12/09/2015.
 */

public class AbstractMixedContentElementTest {

    @Test
    public void testConstructor() throws Exception {

        String xmlString = "Ludwig der Fromme bestätigt der Kirche von Salzburg auf Bitten Erzbischof Arns laut der vorgelegten Urkunde seines Vaters, Kaiser Karls, Immunität mit Königsschutz und ihre Besitzungen. <issuer>Ludwig der Fromme</issuer>";

        AbstractMixedContentElement element1 = new Abstract(xmlString);
        AbstractMixedContentElement element2 = new Abstract("<abstract>" + xmlString + "</abstract>");

        String expected = "<cei:abstract xmlns:cei=\"http://www.monasterium.net/NS/cei\">Ludwig der Fromme bestätigt der Kirche von Salzburg auf Bitten Erzbischof Arns laut der vorgelegten Urkunde seines Vaters, Kaiser Karls, Immunität mit Königsschutz und ihre Besitzungen. <cei:issuer>Ludwig der Fromme</cei:issuer></cei:abstract>";
        assertEquals(element1.toXML(), expected);
        assertEquals(element2.toXML(), expected);

        String expected1 = "Ludwig der Fromme bestätigt der Kirche von Salzburg auf Bitten Erzbischof Arns laut der vorgelegten Urkunde seines Vaters, Kaiser Karls, Immunität mit Königsschutz und ihre Besitzungen. <cei:issuer>Ludwig der Fromme</cei:issuer>";
        assertEquals(element1.getContent(), expected1);
        assertEquals(element2.getContent(), expected1);

    }

}