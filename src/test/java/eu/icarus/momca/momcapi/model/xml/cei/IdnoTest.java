package eu.icarus.momca.momcapi.model.xml.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 09.07.2015.
 */
public class IdnoTest {

    public static final Idno CEI_IDNO_WITHOUT_OLD = new Idno("id", "text");
    private static final Idno CEI_IDNO_WITH_OLD = new Idno("text", "facs", "id", "n", "old");

    @Test
    public void testGetId() throws Exception {
        assertEquals(CEI_IDNO_WITH_OLD.getId(), "id");
    }

    @Test
    public void testGetText() throws Exception {
        assertEquals(CEI_IDNO_WITH_OLD.getText(), "text");
    }

    @Test
    public void testToXML() throws Exception {
        assertEquals(CEI_IDNO_WITHOUT_OLD.toXML(), "<cei:idno xmlns:cei=\"http://www.monasterium.net/NS/cei\" id=\"id\">text</cei:idno>");
        assertEquals(CEI_IDNO_WITH_OLD.toXML(), "<cei:idno xmlns:cei=\"http://www.monasterium.net/NS/cei\" id=\"id\" old=\"old\" facs=\"facs\" n=\"n\">text</cei:idno>");
    }

    @Test
    public void testgetOld() throws Exception {
        assertTrue(CEI_IDNO_WITH_OLD.getOld().isPresent());
        assertEquals(CEI_IDNO_WITH_OLD.getOld().get(), "old");
        assertFalse(CEI_IDNO_WITHOUT_OLD.getOld().isPresent());
    }

}