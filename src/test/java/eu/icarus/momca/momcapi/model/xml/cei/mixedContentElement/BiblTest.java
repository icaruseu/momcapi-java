package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 22/09/2015.
 */
public class BiblTest {

    @Test
    public void test2() throws Exception {

        Bibl bibl = new Bibl("bibl");
        assertEquals(bibl.toXML(), "<cei:bibl xmlns:cei=\"http://www.monasterium.net/NS/cei\">bibl</cei:bibl>");
        assertEquals(bibl.getContent(), "bibl");

    }

}