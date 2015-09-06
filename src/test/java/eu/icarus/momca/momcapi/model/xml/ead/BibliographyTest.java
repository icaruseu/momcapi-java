package eu.icarus.momca.momcapi.model.xml.ead;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 06/09/2015.
 */
public class BibliographyTest {

    private static final String CORRECT_XML = "<ead:bibliography xmlns:ead=\"urn:isbn:1-931666-22-9\"><ead:head>Literatur:</ead:head><ead:bibref>Adam Doppler, Die ältesten Original-Urkunden des f. e. Consistorial-Archives zu Salzburg, 2 Bde. (1200-1400 u. 1401-1440) (= Sonderdruck aus MGSL 1870, 1871 u. 1872)</ead:bibref><ead:bibref>Ders., Auszüge aus den Original-Urkunden des fürsterzbischöflichen Consistorial-Archives zu Salzburg, 4 Bde. (1314-1400, 1441-1460, 1461-1480, 1481-1500) (Salzburg 1870).</ead:bibref><ead:bibref>Salzburger Urkundenbuch, 4 Bde., bearb. v. P. Willibald Hauthaler u. Franz Martin (Salzburg 1910-1933).</ead:bibref></ead:bibliography>";

    private String[] entries = new String[3];
    private String heading;

    @BeforeMethod
    public void setUp() throws Exception {

        entries[0] = "Adam Doppler, Die ältesten Original-Urkunden des f. e. Consistorial-Archives zu Salzburg, 2 Bde. (1200-1400 u. 1401-1440) (= Sonderdruck aus MGSL 1870, 1871 u. 1872)";
        entries[1] = "Ders., Auszüge aus den Original-Urkunden des fürsterzbischöflichen Consistorial-Archives zu Salzburg, 4 Bde. (1314-1400, 1441-1460, 1461-1480, 1481-1500) (Salzburg 1870).";
        entries[2] = "Salzburger Urkundenbuch, 4 Bde., bearb. v. P. Willibald Hauthaler u. Franz Martin (Salzburg 1910-1933).";

        heading = "Literatur:";

    }

    @Test
    public void testGetEntries() throws Exception {
        Bibliography bibliography = new Bibliography(this.heading, this.entries);
        assertEquals(bibliography.getEntries().get(0), entries[0]);
        assertEquals(bibliography.getEntries().get(1), entries[1]);
        assertEquals(bibliography.getEntries().get(2), entries[2]);
    }

    @Test
    public void testGetEntriesEmpty() throws Exception {
        Bibliography bibliography = new Bibliography(this.heading);
        assertTrue(bibliography.getEntries().isEmpty());
    }

    @Test
    public void testGetHeading() throws Exception {

        Bibliography bibliography = new Bibliography(this.heading, this.entries);

        assertTrue(bibliography.getHeading().isPresent());
        assertTrue(bibliography.getHeading().get().getText().isPresent());
        assertEquals(bibliography.getHeading().get().getText().get(), new Heading(heading).getText().get());

    }

    @Test
    public void testGetHeadingEmpty() throws Exception {
        Bibliography bibliography = new Bibliography(null, this.entries);
        assertFalse(bibliography.getHeading().isPresent());
    }

    @Test
    public void testToXML() throws Exception {
        Bibliography bibliography = new Bibliography(this.heading, this.entries);
        assertEquals(bibliography.toXML(), CORRECT_XML);
    }
}