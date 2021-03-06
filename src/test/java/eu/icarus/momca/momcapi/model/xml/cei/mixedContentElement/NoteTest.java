package eu.icarus.momca.momcapi.model.xml.cei.mixedContentElement;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 13/09/2015.
 */
public class NoteTest {

    @Test
    public void test() throws Exception {

        String content = "<sup>2</sup>\nru auf Rasur; das dritte o korrigiert aus u<lb />";

        Note note = new Note(content);
        assertEquals(note.getContent(), "<cei:sup>2</cei:sup>\nru auf Rasur; das dritte o korrigiert aus u<cei:lb />");
        assertEquals(note.toXML(), "<cei:note xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:sup>2</cei:sup>\n" +
                "ru auf Rasur; das dritte o korrigiert aus u<cei:lb /></cei:note>");
        assertFalse(note.getPlace().isPresent());

        note = new Note(content, "Line 2");

        assertTrue(note.getPlace().isPresent());
        assertEquals(note.getPlace().get(), "Line 2");

    }

}