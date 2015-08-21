package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by daniel on 21.07.2015.
 */
public class IdCollectionTest {

    @Test
    public void testConstructor() throws Exception {

        String identifier = "MedDoc|BulgEmp"; // includeds a "|" character
        String atomIdText = "tag:www.monasterium.net,2011:/collection/MedDoc|BulgEmp";
        String atomIdXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/collection/MedDoc%7CBulgEmp</atom:id>";

        IdCollection id1 = new IdCollection(identifier);
        assertEquals(id1.getContentXml().toXML(), atomIdXml);
        assertEquals(id1.getIdentifier(), identifier);
        assertEquals(id1.getContentXml().getText(), Util.encode(atomIdText));

        IdCollection id2 = new IdCollection(new AtomId(atomIdText));
        assertEquals(id2.getContentXml().toXML(), atomIdXml);
        assertEquals(id2.getIdentifier(), identifier);
        assertEquals(id2.getContentXml().getText(), Util.encode(atomIdText));

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithAtomId() throws Exception {
        new IdCollection("tag:www.monasterium.net,2011:/collection/MedDocBulgEmp");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier() throws Exception {
        String emptyId = "";
        new IdCollection(emptyId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongAtomIdType() throws Exception {
        AtomId archiveAtomId = new AtomId("tag:www.monasterium.net,2011:/archive/CH-KAE");
        new IdCollection(archiveAtomId);
    }

    @Test
    public void testEquals() throws Exception {

        String identifier = "MedDoc|BulgEmp"; // includeds a "|" character
        String atomIdText = "tag:www.monasterium.net,2011:/collection/MedDoc|BulgEmp";

        IdCollection id1 = new IdCollection(identifier);
        IdCollection id2 = new IdCollection(new AtomId(atomIdText));

        assertTrue(id1.equals(id2));

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