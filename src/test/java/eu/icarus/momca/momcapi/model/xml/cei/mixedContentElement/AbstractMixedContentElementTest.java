package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import org.testng.annotations.Test;

import java.util.List;

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

    @Test
    public void testGetElements() throws Exception {

        String xmlString = "<persName reg=\"Ludwig\">Ludwig der Fromme</persName> bestätigt der <geogName existent=\"yes\">Kirche</geogName> von <placeName>Salzburg</placeName> auf Bitten <persName>Erzbischof Arns</persName> laut der vorgelegten Urkunde seines Vaters, Kaiser Karls, <index lemma=\"Rechtsbegriff\">Immunität</index> mit Königsschutz und ihre Besitzungen.";
        AbstractMixedContentElement anAbstract = new Abstract(xmlString);

        List<PersName> persNames = anAbstract.getPersNames();
        assertEquals(persNames.size(), 2);
        assertEquals(persNames.get(0).getReg().get(), "Ludwig");

        List<PlaceName> placeNames = anAbstract.getPlaceNames();
        assertEquals(placeNames.size(), 1);

        List<GeogName> geogNames = anAbstract.getGeogNames();
        assertEquals(geogNames.size(), 1);
        assertEquals(geogNames.get(0).getExistent().get(), "yes");

        List<Index> indexes = anAbstract.getIndexes();
        assertEquals(indexes.size(), 1);
        assertEquals(indexes.get(0).getLemma().get(), "Rechtsbegriff");

    }

    @Test
    public void testGetText() throws Exception {

        String xmlString = "<persName>Ludwig der Fromme</persName> bestätigt der Kirche von <placeName>Salzburg</placeName> auf Bitten <persName>Erzbischof Arns</persName> laut der vorgelegten Urkunde seines Vaters, Kaiser Karls, Immunität mit Königsschutz und ihre Besitzungen.";

        AbstractMixedContentElement anAbstract = new Abstract(xmlString);

        assertEquals(anAbstract.getText(), "Ludwig der Fromme bestätigt der Kirche von Salzburg auf Bitten Erzbischof Arns laut der vorgelegten Urkunde seines Vaters, Kaiser Karls, Immunität mit Königsschutz und ihre Besitzungen.");

    }
}