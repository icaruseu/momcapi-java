package eu.icarus.momca.momcapi.model.xml.cei;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

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

        assertTrue(sourceDesc.getBibliographyAbstract().isPresent());
        assertEquals(sourceDesc.getBibliographyAbstract().get().getEntries().size(), 1);

        assertTrue(sourceDesc.getBibliographyTenor().isPresent());
        assertEquals(sourceDesc.getBibliographyTenor().get().getEntries().size(), 2);

    }

    @Test
    public void testConstructorEmpty() throws Exception {

        SourceDesc sourceDesc = new SourceDesc();

        assertFalse(sourceDesc.getBibliographyAbstract().isPresent());
        assertFalse(sourceDesc.getBibliographyTenor().isPresent());
        assertEquals(sourceDesc.toXML(), "<cei:sourceDesc xmlns:cei=\"http://www.monasterium.net/NS/cei\" />");

    }

    @Test
    public void testConstructorPartial1() throws Exception {

        List<String> listTenor = new ArrayList<>(2);
        listTenor.add("tenorEntry 1");
        listTenor.add("tenorEntry 2");

        String expectedXml = "<cei:sourceDesc xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:sourceDescVolltext><cei:bibl>tenorEntry 1</cei:bibl><cei:bibl>tenorEntry 2</cei:bibl></cei:sourceDescVolltext></cei:sourceDesc>";

        SourceDesc sourceDesc = new SourceDesc(null, listTenor);

        assertFalse(sourceDesc.getBibliographyAbstract().isPresent());

        assertTrue(sourceDesc.getBibliographyTenor().isPresent());
        assertEquals(sourceDesc.getBibliographyTenor().get().getEntries().size(), 2);

        assertEquals(sourceDesc.toXML(), expectedXml);

    }

    @Test
    public void testConstructorPartial2() throws Exception {

        List<String> listAbstract = new ArrayList<>(1);
        listAbstract.add("abstractEntry 1");

        String expectedXml = "<cei:sourceDesc xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:sourceDescRegest><cei:bibl>abstractEntry 1</cei:bibl></cei:sourceDescRegest></cei:sourceDesc>";

        SourceDesc sourceDesc = new SourceDesc(listAbstract, new ArrayList<>(0));

        assertTrue(sourceDesc.getBibliographyAbstract().isPresent());
        assertEquals(sourceDesc.getBibliographyAbstract().get().getEntries().size(), 1);

        assertFalse(sourceDesc.getBibliographyTenor().isPresent());

        assertEquals(sourceDesc.toXML(), expectedXml);

    }

}