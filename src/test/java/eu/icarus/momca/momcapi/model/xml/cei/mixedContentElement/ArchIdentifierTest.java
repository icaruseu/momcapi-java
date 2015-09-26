package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import eu.icarus.momca.momcapi.model.xml.cei.Ref;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 25/09/2015.
 */
public class ArchIdentifierTest {

    public static final String XML = "<arch>Kincstári levéltárból (E)</arch><ref target=\"http://archives.hungaricana.hu/en/charters/164\" />";

    @Test
    public void testConstructor1() throws Exception {
        ArchIdentifier ai = new ArchIdentifier(XML);
        assertEquals(ai.getContent(), "<cei:arch>Kincstári levéltárból (E)</cei:arch><cei:ref target=\"http://archives.hungaricana.hu/en/charters/164\" />");
        assertEquals(ai.toXML(), "<cei:archIdentifier xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:arch>Kincstári levéltárból (E)</cei:arch><cei:ref target=\"http://archives.hungaricana.hu/en/charters/164\" /></cei:archIdentifier>");
    }

    @Test
    public void testConstructor2() throws Exception {

        ArchIdentifier ai = new ArchIdentifier(XML, "facs", "id", "lang", "n");

        assertEquals(ai.getContent(), "<cei:arch>Kincstári levéltárból (E)</cei:arch><cei:ref target=\"http://archives.hungaricana.hu/en/charters/164\" />");

        assertTrue(ai.getFacs().isPresent());
        assertTrue(ai.getId().isPresent());
        assertTrue(ai.getLang().isPresent());
        assertTrue(ai.getN().isPresent());

        assertEquals(ai.getFacs().get(), "facs");
        assertEquals(ai.getId().get(), "id");
        assertEquals(ai.getLang().get(), "lang");
        assertEquals(ai.getN().get(), "n");

    }

    @Test
    public void testGetRef() throws Exception {

        ArchIdentifier ai = new ArchIdentifier(XML);

        assertTrue(ai.getRef().isPresent());

        Ref ref = ai.getRef().get();
        assertEquals(ref.getTarget().get(), "http://archives.hungaricana.hu/en/charters/164");
        assertFalse(ref.getText().isPresent());

    }
}