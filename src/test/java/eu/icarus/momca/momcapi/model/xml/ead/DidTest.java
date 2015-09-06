package eu.icarus.momca.momcapi.model.xml.ead;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 06/09/2015.
 */
public class DidTest {

    private static final String CORRECT_XML = "<ead:did xmlns:ead=\"urn:isbn:1-931666-22-9\"><ead:unitid identifier=\"KUFreising\">KUFreising</ead:unitid><ead:unittitle>Urkunden des Klosters Freising</ead:unittitle></ead:did>";
    private String identifier = "KUFreising";
    private String name = "Urkunden des Klosters Freising";

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier() throws Exception {
        Did did = new Did("", name);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyName() throws Exception {
        Did did = new Did(identifier, "");
    }

    @Test
    public void testGetIdentifier() throws Exception {
        Did did = new Did(identifier, name);
        assertEquals(did.getIdentifier(), identifier);
    }

    @Test
    public void testGetName() throws Exception {
        Did did = new Did(identifier, name);
        assertEquals(did.getName(), name);
    }

    @Test
    public void testToXML() throws Exception {
        Did did = new Did(identifier, name);
        assertEquals(did.toXML(), CORRECT_XML);
    }

}