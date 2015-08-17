package eu.icarus.momca.momcapi.xml.atom;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 21.07.2015.
 */
public class IdMyCollectionTest {

    @Test
    public void testConstructor() throws Exception {

        String myCollectionIdentifier = "67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3";
        String myCollectionId = "tag:www.monasterium.net,2011:/mycollection/67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3";
        String correctXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/mycollection/67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3</atom:id>";

        IdMyCollection id1 = new IdMyCollection(myCollectionIdentifier);
        assertEquals(id1.toXML(), correctXml);

        IdMyCollection id2 = new IdMyCollection(myCollectionId);
        assertEquals(id2.toXML(), correctXml);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyId() throws Exception {
        String emptyId = "";
        new IdMyCollection(emptyId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithFaultyId() throws Exception {
        String faultyId = "tag:www.monasterium.net,2011:/mycollection/CH-KAE/Urkunden/Urkunde_1";
        new IdMyCollection(faultyId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongId() throws Exception {
        String archiveId = "tag:www.monasterium.net,2011:/collection/MedBulgEmp";
        new IdMyCollection(archiveId);
    }

    @Test
    public void testGetMyCollectionIdentifier() throws Exception {

        String myCollectionIdentifier = "67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3";
        String myCollectionId = "tag:www.monasterium.net,2011:/mycollection/67e2a744-6a32-4d71-abaa-7a5f7b0e9bf3";

        IdMyCollection id1 = new IdMyCollection(myCollectionIdentifier);
        assertEquals(id1.getMyCollectionIdentifier(), myCollectionIdentifier);

        IdMyCollection id2 = new IdMyCollection(myCollectionId);
        assertEquals(id2.getMyCollectionIdentifier(), myCollectionIdentifier);

    }

}