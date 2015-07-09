package eu.icarus.momca.momcapi.resource.atom;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 09.07.2015.
 */
public class AtomAuthorTest {

    private static final AtomAuthor ATOM_AUTHOR = new AtomAuthor("author@test.com");

    @Test
    public void testGetEmail() throws Exception {
        assertEquals(ATOM_AUTHOR.getEmail(), "author@test.com");
    }

    @Test
    public void testToXml() throws Exception {
        assertEquals(ATOM_AUTHOR.toXML(), "<atom:author xmlns:atom=\"http://www.w3.org/2005/Atom\"><atom:email>author@test.com</atom:email></atom:author>");
    }

}