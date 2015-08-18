package eu.icarus.momca.momcapi.xml.atom;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 21.07.2015.
 */
public class IdMyCollectionTest {

    @Test
    public void testConstructor() throws Exception {

        String identifier = "67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3";
        String idText = "tag:www.monasterium.net,2011:/mycollection/67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3";
        String atomIdXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/mycollection/67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3</atom:id>";

        IdMyCollection id = new IdMyCollection(identifier);
        assertEquals(id.getIdentifier(), identifier);
        assertEquals(id.getAtomId().toText(), idText);
        assertEquals(id.getAtomId().toXML(), atomIdXml);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithAtomIdText() throws Exception {
        String idText = "tag:www.monasterium.net,2011:/mycollection/67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3";
        new IdMyCollection(idText);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyId() throws Exception {
        String emptyId = "";
        new IdMyCollection(emptyId);
    }

    @Test
    public void testGetIdentifier() throws Exception {
        String myCollectionIdentifier = "67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3";
        IdMyCollection id = new IdMyCollection(myCollectionIdentifier);
        assertEquals(id.getIdentifier(), myCollectionIdentifier);
    }

}