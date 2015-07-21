package eu.icarus.momca.momcapi.xml.atom;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 20.07.2015.
 */
public class IdArchiveTest {

    @Test
    public void testConstructor() throws Exception {

        String archiveIdentifier = "CH-KAE";
        String archiveAtomId = "tag:www.monasterium.net,2011:/archive/CH-KAE";
        String correctXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/archive/CH-KAE</atom:id>";

        IdArchive id1 = new IdArchive(archiveIdentifier);
        assertEquals(id1.toXML(), correctXml);

        IdArchive id2 = new IdArchive(archiveAtomId);
        assertEquals(id2.toXML(), correctXml);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongId() throws Exception {
        String charterAtomId = "tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/Urkunde_1";
        new IdArchive(charterAtomId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithFaultyId() throws Exception {
        String faultyAtomId = "tag:www.monasterium.net,2011:/archive/CH-KAE/Urkunden/";
        new IdArchive(faultyAtomId);
    }

    @Test
    public void testGetArchiveIdentifier() throws Exception {

        String archiveIdentifier = "CH-KAE";
        String archiveAtomId = "tag:www.monasterium.net,2011:/archive/CH-KAE";

        IdArchive id1 = new IdArchive(archiveIdentifier);
        assertEquals(id1.getArchiveIdentifier(), archiveIdentifier);


        IdArchive id2 = new IdArchive(archiveAtomId);
        assertEquals(id2.getArchiveIdentifier(), archiveIdentifier);

    }

}