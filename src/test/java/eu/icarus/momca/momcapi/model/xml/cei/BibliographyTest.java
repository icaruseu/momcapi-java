package eu.icarus.momca.momcapi.model.xml.cei;

import eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement.Bibl;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 22/09/2015.
 */
public class BibliographyTest {

    @Test
    public void test2() throws Exception {

        List<Bibl> bibl = new ArrayList<>(2);
        bibl.add(new Bibl("bibl1"));
        bibl.add(new Bibl("bibl2"));

        Bibliography bibliography = new Bibliography("sourceDescVolltext", bibl);

        assertEquals(bibliography.toXML(), "<cei:sourceDescVolltext xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:bibl>bibl1</cei:bibl><cei:bibl>bibl2</cei:bibl></cei:sourceDescVolltext>");
        assertEquals(bibliography.getEntries(), bibl);

    }
}