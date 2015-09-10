package eu.icarus.momca.momcapi.model.xml.cei;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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

        assertEquals(sourceDesc.getBiblAbstract(), listAbstract);
        assertEquals(sourceDesc.getBiblTenor(), listTenor);
        assertEquals(sourceDesc.toXML(), expectedXml);

    }

    @Test
    public void testConstructorEmpty() throws Exception {

        SourceDesc sourceDesc = new SourceDesc();

        assertTrue(sourceDesc.getBiblAbstract().isEmpty());
        assertTrue(sourceDesc.getBiblTenor().isEmpty());
        assertEquals(sourceDesc.toXML(), "<cei:sourceDesc xmlns:cei=\"http://www.monasterium.net/NS/cei\" />");

    }

    @Test
    public void testConstructorPartial1() throws Exception {

        List<String> listTenor = new ArrayList<>(2);
        listTenor.add("tenorEntry 1");
        listTenor.add("tenorEntry 2");

        String expectedXml = "<cei:sourceDesc xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:sourceDescVolltext><cei:bibl>tenorEntry 1</cei:bibl><cei:bibl>tenorEntry 2</cei:bibl></cei:sourceDescVolltext></cei:sourceDesc>";

        SourceDesc sourceDesc = new SourceDesc(null, listTenor);

        assertTrue(sourceDesc.getBiblAbstract().isEmpty());
        assertEquals(sourceDesc.getBiblTenor(), listTenor);
        assertEquals(sourceDesc.toXML(), expectedXml);

    }

    @Test
    public void testConstructorPartial2() throws Exception {

        List<String> listAbstract = new ArrayList<>(1);
        listAbstract.add("abstractEntry 1");

        String expectedXml = "<cei:sourceDesc xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:sourceDescRegest><cei:bibl>abstractEntry 1</cei:bibl></cei:sourceDescRegest></cei:sourceDesc>";

        SourceDesc sourceDesc = new SourceDesc(listAbstract, new ArrayList<>(0));

        assertEquals(sourceDesc.getBiblAbstract(), listAbstract);
        assertTrue(sourceDesc.getBiblTenor().isEmpty());
        assertEquals(sourceDesc.toXML(), expectedXml);

    }

}