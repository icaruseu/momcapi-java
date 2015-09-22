package eu.icarus.momca.momcapi.model.xml.cei;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 10/09/2015.
 */
public class SourceDescTest {

    @Test
    public void testConstructor() throws Exception {

        List<String> listAbstract = new ArrayList<>(1);
        listAbstract.add("abstractEntry 1");
        List<String> listTenor = new ArrayList<>(2);
        listTenor.add("tenorEntry 1");
        listTenor.add("tenorEntry 2");

        String expectedXml = "<cei:sourceDesc xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:sourceDescRegest><cei:bibl>abstractEntry 1</cei:bibl></cei:sourceDescRegest><cei:sourceDescVolltext><cei:bibl>tenorEntry 1</cei:bibl><cei:bibl>tenorEntry 2</cei:bibl></cei:sourceDescVolltext></cei:sourceDesc>";

        SourceDesc sourceDesc = new SourceDesc(listAbstract, listTenor);

        assertEquals(sourceDesc.toXML(), expectedXml);
        assertEquals(sourceDesc.getBibliographyAbstract().getEntries().size(), 1);
        assertEquals(sourceDesc.getBibliographyTenor().getEntries().size(), 2);

    }

    @Test
    public void testConstructorEmpty() throws Exception {
        SourceDesc sourceDesc = new SourceDesc();
        assertEquals(sourceDesc.toXML(), "<cei:sourceDesc xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:sourceDescRegest><cei:bibl /></cei:sourceDescRegest><cei:sourceDescVolltext><cei:bibl /></cei:sourceDescVolltext></cei:sourceDesc>");
    }

    @Test
    public void testConstructorPartial1() throws Exception {

        List<String> listTenor = new ArrayList<>(2);
        listTenor.add("tenorEntry 1");
        listTenor.add("tenorEntry 2");

        String expectedXml = "<cei:sourceDesc xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:sourceDescRegest><cei:bibl /></cei:sourceDescRegest><cei:sourceDescVolltext><cei:bibl>tenorEntry 1</cei:bibl><cei:bibl>tenorEntry 2</cei:bibl></cei:sourceDescVolltext></cei:sourceDesc>";

        SourceDesc sourceDesc = new SourceDesc(new ArrayList<>(0), listTenor);

        assertEquals(sourceDesc.getBibliographyAbstract().getEntries().size(), 1);
        assertEquals(sourceDesc.getBibliographyTenor().getEntries().size(), 2);

        assertEquals(sourceDesc.toXML(), expectedXml);

    }

    @Test
    public void testConstructorPartial2() throws Exception {

        List<String> listAbstract = new ArrayList<>(1);
        listAbstract.add("abstractEntry 1");
        listAbstract.add("abstractEntry 2");

        String expectedXml = "<cei:sourceDesc xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:sourceDescRegest><cei:bibl>abstractEntry 1</cei:bibl><cei:bibl>abstractEntry 2</cei:bibl></cei:sourceDescRegest><cei:sourceDescVolltext><cei:bibl /></cei:sourceDescVolltext></cei:sourceDesc>";

        SourceDesc sourceDesc = new SourceDesc(listAbstract, new ArrayList<>(0));

        assertEquals(sourceDesc.getBibliographyAbstract().getEntries().size(), 2);
        assertEquals(sourceDesc.getBibliographyTenor().getEntries().size(), 1);

        assertEquals(sourceDesc.toXML(), expectedXml);

    }

}