package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by daniel on 21.07.2015.
 */
public class IdMyCollectionTest {

    @Test
    public void testConstructor() throws Exception {

        String identifier = "|67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3"; // has "|" at the beginning, this has to be encoded
        String atomIdText = "tag:www.monasterium.net,2011:/mycollection/|67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3"; // includeds the "|" char
        String atomIdXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/mycollection/%7C67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3</atom:id>";

        IdMyCollection id1 = new IdMyCollection(identifier);
        assertEquals(id1.getContentXml().toXML(), atomIdXml);
        assertEquals(id1.getIdentifier(), identifier);
        assertEquals(id1.getContentXml().getText(), Util.encode(atomIdText));

        IdMyCollection id2 = new IdMyCollection(new AtomId(atomIdText));
        assertEquals(id2.getContentXml().toXML(), atomIdXml);
        assertEquals(id2.getIdentifier(), identifier);
        assertEquals(id2.getContentXml().getText(), Util.encode(atomIdText));

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithAtomId() throws Exception {
        new IdCollection("tag:www.monasterium.net,2011:/mycollection/67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier() throws Exception {
        String emptyId = "";
        new IdMyCollection(emptyId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongAtomIdType() throws Exception {
        AtomId archiveAtomId = new AtomId("tag:www.monasterium.net,2011:/archive/CH-KAE");
        new IdMyCollection(archiveAtomId);
    }

    @Test
    public void testEquals() throws Exception {

        String identifier = "|67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3"; // has "|" at the beginning, this has to be encoded
        String atomIdText = "tag:www.monasterium.net,2011:/mycollection/|67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3"; // includeds the "|" char

        IdMyCollection id1 = new IdMyCollection(identifier);
        IdMyCollection id2 = new IdMyCollection(new AtomId(atomIdText));

        assertTrue(id1.equals(id2));

    }

    @Test
    public void testGetIdentifier() throws Exception {

        String identifier = "67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3";
        IdMyCollection id1 = new IdMyCollection(identifier);
        assertEquals(id1.getIdentifier(), identifier);

        AtomId atomId = new AtomId("tag:www.monasterium.net,2011:/mycollection/67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3");
        IdMyCollection id2 = new IdMyCollection(atomId);
        assertEquals(id2.getIdentifier(), identifier);

    }
}