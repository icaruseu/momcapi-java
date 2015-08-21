package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by daniel on 20.07.2015.
 */
public class IdArchiveTest {

    @Test
    public void testConstructor() throws Exception {

        String identifier = "CH|KAE";
        String atomIdText = "tag:www.monasterium.net,2011:/archive/CH|KAE";
        String atomIdXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/archive/CH%7CKAE</atom:id>";

        IdArchive id1 = new IdArchive(identifier);
        assertEquals(id1.getContentXml().toXML(), atomIdXml);
        assertEquals(id1.getIdentifier(), identifier);
        assertEquals(id1.getContentXml().getText(), Util.encode(atomIdText));

        IdArchive id2 = new IdArchive(new AtomId(atomIdText));
        assertEquals(id2.getContentXml().toXML(), atomIdXml);
        assertEquals(id2.getIdentifier(), identifier);
        assertEquals(id2.getContentXml().getText(), Util.encode(atomIdText));

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithAtomId() throws Exception {
        new IdArchive("tag:www.monasterium.net,2011:/archive/CH-KAE");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier() throws Exception {
        String emptyId = "";
        new IdArchive(emptyId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongAtomIdType() throws Exception {
        AtomId collectionAtomId = new AtomId("tag:www.monasterium.net,2011:/collection/MedDocBulgEmp");
        new IdArchive(collectionAtomId);
    }

    @Test
    public void testEquals() throws Exception {

        String identifier = "CH|KAE";
        String atomIdText = "tag:www.monasterium.net,2011:/archive/CH|KAE";

        IdArchive id1 = new IdArchive(identifier);
        IdArchive id2 = new IdArchive(new AtomId(atomIdText));

        assertTrue(id1.equals(id2));

    }

    @Test
    public void testGetIdentifier() throws Exception {

        String identifier = "CH-KAE";
        IdArchive id1 = new IdArchive(identifier);
        assertEquals(id1.getIdentifier(), identifier);

        AtomId atomId = new AtomId("tag:www.monasterium.net,2011:/archive/CH-KAE");
        IdArchive id2 = new IdArchive(atomId);
        assertEquals(id2.getIdentifier(), identifier);

    }

}