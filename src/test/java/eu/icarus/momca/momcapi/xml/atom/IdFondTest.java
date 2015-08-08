package eu.icarus.momca.momcapi.xml.atom;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 21.07.2015.
 */
public class IdFondTest {

    @Test
    public void testConstructor() throws Exception {

        String fondId = "tag:www.monasterium.net,2011:/fond/CH-KAE/Urkunden";
        String archiveIdentifier = "CH-KAE";
        String fondIdentifier = "Urkunden";

        String correctXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/fond/CH-KAE/Urkunden</atom:id>";

        IdFond id1 = new IdFond(fondId);
        IdFond id2 = new IdFond(archiveIdentifier, fondIdentifier);

        assertEquals(id1.toXML(), correctXml);
        assertEquals(id2.toXML(), correctXml);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongId() throws Exception {
        String charterId = "tag:www.monasterium.net,2011:/archive/CH-KAE/Urkunden/Urkunde_1";
        new IdFond(charterId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithFaultyId() throws Exception {
        String faultyId = "tag:www.monasterium.net,2011:/fond/CH-KAE";
        new IdFond(faultyId);
    }

    @Test
    public void testGetArchiveIdentifier() throws Exception {

        String fondId = "tag:www.monasterium.net,2011:/fond/CH-KAE/Urkunden";
        String archiveIdentifier = "CH-KAE";
        String fondIdentifier = "Urkunden";

        IdFond id1 = new IdFond(fondId);
        IdFond id2 = new IdFond(archiveIdentifier, fondIdentifier);

        assertEquals(id1.getArchiveIdentifier(), archiveIdentifier);
        assertEquals(id2.getArchiveIdentifier(), archiveIdentifier);

    }

    @Test
    public void testGetFondIdentifier() throws Exception {

        String fondId = "tag:www.monasterium.net,2011:/fond/CH-KAE/Urkunden";
        String archiveIdentifier = "CH-KAE";
        String fondIdentifier = "Urkunden";

        IdFond id1 = new IdFond(fondId);
        IdFond id2 = new IdFond(archiveIdentifier, fondIdentifier);

        assertEquals(id1.getFondIdentifier(), fondIdentifier);
        assertEquals(id2.getFondIdentifier(), fondIdentifier);

    }

}