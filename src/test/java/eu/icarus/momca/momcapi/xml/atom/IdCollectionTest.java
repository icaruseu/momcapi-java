package eu.icarus.momca.momcapi.xml.atom;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 21.07.2015.
 */
public class IdCollectionTest {

    @Test
    public void testConstructor() throws Exception {

        String identifier = "MedDocBulgEmp";
        String atomIdText = "tag:www.monasterium.net,2011:/collection/MedDocBulgEmp";
        String atomIdXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/collection/MedDocBulgEmp</atom:id>";

        IdCollection id1 = new IdCollection(identifier);
        assertEquals(id1.getAtomId().toXML(), atomIdXml);
        assertEquals(id1.getIdentifier(), identifier);
        assertEquals(id1.getAtomId().getText(), atomIdText);

        IdCollection id2 = new IdCollection(new AtomId(atomIdText));
        assertEquals(id2.getAtomId().toXML(), atomIdXml);
        assertEquals(id2.getIdentifier(), identifier);
        assertEquals(id2.getAtomId().getText(), atomIdText);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongAtomIdType() throws Exception {
        AtomId archiveAtomId = new AtomId("tag:www.monasterium.net,2011:/archive/CH-KAE");
        new IdCollection(archiveAtomId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier() throws Exception {
        String emptyId = "";
        new IdCollection(emptyId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithAtomId() throws Exception {
        new IdCollection("tag:www.monasterium.net,2011:/collection/MedDocBulgEmp");
    }

    @Test
    public void testGetIdentifier() throws Exception {

        String identifier = "MedDocBulgEmp";
        IdCollection id1 = new IdCollection(identifier);
        assertEquals(id1.getIdentifier(), identifier);

        AtomId atomId = new AtomId("tag:www.monasterium.net,2011:/collection/MedDocBulgEmp");
        IdCollection id2 = new IdCollection(atomId);
        assertEquals(id2.getIdentifier(), identifier);

    }

}