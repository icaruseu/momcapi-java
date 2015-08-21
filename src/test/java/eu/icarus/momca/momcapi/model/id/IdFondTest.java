package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by daniel on 21.07.2015.
 */
public class IdFondTest {

    @Test
    public void testConstructor() throws Exception {

        String atomIdText = "tag:www.monasterium.net,2011:/fond/CH|KAE/Ur|kunden"; // includeds the "|" character
        AtomId atomId = new AtomId(atomIdText);
        String archiveIdentifier = "CH|KAE";
        String fondIdentifier = "Ur|kunden";
        String correctXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/fond/CH%7CKAE/Ur%7Ckunden</atom:id>";

        IdFond id1 = new IdFond(atomId);
        assertEquals(id1.getIdentifier(), fondIdentifier);
        assertEquals(id1.getIdArchive().getIdentifier(), archiveIdentifier);
        assertEquals(id1.getContentXml().toXML(), correctXml);
        assertEquals(id1.getContentXml().getText(), Util.encode(atomIdText));

        IdFond id2 = new IdFond(archiveIdentifier, fondIdentifier);
        assertEquals(id2.getIdentifier(), fondIdentifier);
        assertEquals(id2.getIdArchive().getIdentifier(), archiveIdentifier);
        assertEquals(id2.getContentXml().toXML(), correctXml);
        assertEquals(id2.getContentXml().getText(), Util.encode(atomIdText));

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithAtomIdAsArchive() throws Exception {
        new IdFond("tag:www.monasterium.net,2011:/archive/CH-KAE", "Urkunden");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithAtomIdAsFond() throws Exception {
        new IdFond("CH-KAE", "tag:www.monasterium.net,2011:/fond/CH-KAE/Urkunden");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyArchive() throws Exception {
        new IdFond("", "Urkunden");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyFond() throws Exception {
        new IdFond("CH-KAE", "");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyId() throws Exception {
        AtomId emptyId = new AtomId("");
        new IdFond(emptyId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithFaultyId() throws Exception {
        AtomId faultyId = new AtomId("tag:www.monasterium.net,2011:/fond/CH-KAE");
        new IdFond(faultyId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongIdType() throws Exception {
        AtomId archiveAtomId = new AtomId("tag:www.monasterium.net,2011:/archive/CH-KAE");
        new IdFond(archiveAtomId);
    }

    @Test
    public void testEquals() throws Exception {

        String atomIdText = "tag:www.monasterium.net,2011:/fond/CH|KAE/Ur|kunden"; // includeds the "|" character
        AtomId atomId = new AtomId(atomIdText);
        String archiveIdentifier = "CH|KAE";
        String fondIdentifier = "Ur|kunden";

        IdFond id1 = new IdFond(atomId);
        IdFond id2 = new IdFond(archiveIdentifier, fondIdentifier);

        assertTrue(id1.equals(id2));

    }

    @Test
    public void testGetIdArchive() throws Exception {

        String archiveIdentifier = "CH-KAE";
        String fondIdentifier = "Urkunden";
        AtomId fondAtomId = new AtomId("tag:www.monasterium.net,2011:/fond/CH-KAE/Urkunden");

        IdArchive idArchive = new IdArchive(archiveIdentifier);

        IdFond id1 = new IdFond(fondAtomId);
        IdFond id2 = new IdFond(archiveIdentifier, fondIdentifier);

        assertEquals(id1.getIdArchive(), idArchive);
        assertEquals(id2.getIdArchive(), idArchive);

    }

    @Test
    public void testGetIdentifier() throws Exception {

        AtomId fondAtomId = new AtomId("tag:www.monasterium.net,2011:/fond/CH-KAE/Urkunden");
        String archiveIdentifier = "CH-KAE";
        String fondIdentifier = "Urkunden";

        IdFond id1 = new IdFond(fondAtomId);
        assertEquals(id1.getIdentifier(), fondIdentifier);

        IdFond id2 = new IdFond(archiveIdentifier, fondIdentifier);
        assertEquals(id2.getIdentifier(), fondIdentifier);

    }

}