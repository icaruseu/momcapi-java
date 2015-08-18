package eu.icarus.momca.momcapi.xml.atom;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 20.07.2015.
 */
public class IdArchiveTest {

    @Test
    public void testConstructor() throws Exception {

        String identifier = "CH-KAE";
        String atomIdText = "tag:www.monasterium.net,2011:/archive/CH-KAE";
        String atomIdXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/archive/CH-KAE</atom:id>";

        IdArchive id1 = new IdArchive(identifier);
        assertEquals(id1.getAtomId().toXML(), atomIdXml);
        assertEquals(id1.getIdentifier(), identifier);
        assertEquals(id1.getAtomId().getText(), atomIdText);

        IdArchive id2 = new IdArchive(new AtomId(atomIdText));
        assertEquals(id2.getAtomId().toXML(), atomIdXml);
        assertEquals(id2.getIdentifier(), identifier);
        assertEquals(id2.getAtomId().getText(), atomIdText);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongAtomIdType() throws Exception {
        AtomId collectionAtomId = new AtomId("tag:www.monasterium.net,2011:/collection/MedDocBulgEmp");
        new IdArchive(collectionAtomId);
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