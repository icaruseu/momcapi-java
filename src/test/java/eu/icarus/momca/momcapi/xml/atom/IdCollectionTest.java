package eu.icarus.momca.momcapi.xml.atom;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 21.07.2015.
 */
public class IdCollectionTest {

    @Test
    public void testConstructor() throws Exception {

        String collectionIdentifier = "MedDocBulgEmp";
        String collectionAtomId = "tag:www.monasterium.net,2011:/collection/MedDocBulgEmp";
        String correctXml = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                "tag:www.monasterium.net,2011:/collection/MedDocBulgEmp</atom:id>";

        IdCollection id1 = new IdCollection(collectionIdentifier);
        assertEquals(id1.toXML(), correctXml);

        IdCollection id2 = new IdCollection(collectionAtomId);
        assertEquals(id2.toXML(), correctXml);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongId() throws Exception {
        String archiveAtomId = "tag:www.monasterium.net,2011:/archive/CH-KAE";
        new IdCollection(archiveAtomId);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithFaultyId() throws Exception {
        String faultyAtomId = "tag:www.monasterium.net,2011:/collection/CH-KAE/Urkunden/Urkunde_1";
        new IdCollection(faultyAtomId);
    }

    @Test
    public void testGetCollectionIdentifier() throws Exception {

        String collectionIdentifier = "MedDocBulgEmp";
        String collectionAtomId = "tag:www.monasterium.net,2011:/collection/MedDocBulgEmp";

        IdCollection id1 = new IdCollection(collectionIdentifier);
        assertEquals(id1.getCollectionIdentifier(), collectionIdentifier);

        IdCollection id2 = new IdCollection(collectionAtomId);
        assertEquals(id2.getCollectionIdentifier(), collectionIdentifier);

    }

}