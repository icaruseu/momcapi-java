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

        IdCollection id = new IdCollection(identifier);
        assertEquals(id.getAtomId().toXML(), atomIdXml);
        assertEquals(id.getIdentifier(), identifier);
        assertEquals(id.getAtomId().getText(), atomIdText);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongIdType() throws Exception {
        AtomId archiveAtomId = new AtomId("tag:www.monasterium.net,2011:/archive/CH-KAE");
        new IdCollection(archiveAtomId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyId() throws Exception {
        String emptyId = "";
        new IdCollection(emptyId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongIdentifier() throws Exception {
        String archiveId = "tag:www.monasterium.net,2011:/archive/CH-KAE";
        new IdCollection(archiveId);
    }

    @Test
    public void testGetCollectionIdentifier() throws Exception {

        String collectionIdentifier = "MedDocBulgEmp";
        IdCollection id1 = new IdCollection(collectionIdentifier);
        assertEquals(id1.getIdentifier(), collectionIdentifier);

        AtomId atomId = new AtomId("tag:www.monasterium.net,2011:/collection/MedDocBulgEmp");
        IdCollection id2 = new IdCollection(atomId);
        assertEquals(id2.getIdentifier(), collectionIdentifier);

    }

}