package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.xml.atom.AtomAuthor;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by djell on 19/08/2015.
 */
public class IdUserTest {

    @Test
    public void testConstructor() throws Exception {

        String userMail = "user1@example.com";
        String atomAuthorText = "<atom:author xmlns:atom=\"http://www.w3.org/2005/Atom\"><atom:email>user1@example.com</atom:email></atom:author>";
        AtomAuthor atomAuthor = new AtomAuthor(userMail);

        IdUser id1 = new IdUser(userMail);
        assertEquals(id1.getIdentifier(), userMail);
        assertEquals(id1.getContentXml().toXML(), atomAuthorText);

        IdUser id2 = new IdUser(atomAuthor);
        assertEquals(id2.getIdentifier(), userMail);
        assertEquals(id2.getContentXml().toXML(), atomAuthorText);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier() throws Exception {
        IdUser id = new IdUser("");
        assertEquals(id.getIdentifier(), "");
        assertEquals(id.getContentXml().toXML(), "");
    }

    @Test
    public void testEquals() throws Exception {

        String userMail = "user1@example.com";
        AtomAuthor atomAuthor = new AtomAuthor(userMail);

        IdUser id1 = new IdUser(userMail);
        IdUser id2 = new IdUser(atomAuthor);

        assertTrue(id1.equals(id2));

    }

    @Test
    public void testGetContentXml() throws Exception {

        String userMail = "user1@example.com";
        String atomAuthorText = "<atom:author xmlns:atom=\"http://www.w3.org/2005/Atom\"><atom:email>user1@example.com</atom:email></atom:author>";
        AtomAuthor atomAuthor = new AtomAuthor(userMail);

        IdUser id1 = new IdUser(userMail);
        assertEquals(id1.getContentXml().toXML(), atomAuthorText);

        IdUser id2 = new IdUser(atomAuthor);
        assertEquals(id2.getContentXml().toXML(), atomAuthorText);

    }

}