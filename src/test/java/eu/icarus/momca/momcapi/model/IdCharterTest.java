package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.xml.atom.AtomId;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 27.06.2015.
 */
public class IdCharterTest {

    @NotNull
    private static final String ATOM_ID_COLLECTION_CHARTER = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">tag:www.monasterium.net,2011:/charter/MedDoc%7CBulgEmp/1192-02-02_sic%21_Ioan_Kaliman</atom:id>";
    @NotNull
    private static final String ATOM_ID_FOND_CHARTER = "<atom:id xmlns:atom=\"http://www.w3.org/2005/Atom\">tag:www.monasterium.net,2011:/charter/RS%7CIAGNS/Char%7Cters/IAGNS_F-.150_6605%7C193232</atom:id>";
    @NotNull
    private static final String ATOM_ID_TEXT_COLLECTION_CHARTER = "tag:www.monasterium.net,2011:/charter/MedDoc|BulgEmp/1192-02-02_sic!_Ioan_Kaliman";
    @NotNull
    private static final String ATOM_ID_TEXT_FOND_CHARTER = "tag:www.monasterium.net,2011:/charter/RS%7CIAGNS/Char|ters/IAGNS_F-.150_6605|193232";
    @NotNull
    private static final String IDENTIFIER_ARCHIVE = "RS|IAGNS";
    @NotNull
    private static final String IDENTIFIER_COLLECTION = "MedDoc|BulgEmp";
    @NotNull
    private static final String IDENTIFIER_COLLECTION_CHARTER = "1192-02-02_sic!_Ioan_Kaliman";
    @NotNull
    private static final String IDENTIFIER_FOND = "Char|ters";
    @NotNull
    private static final String IDENTIFIER_FOND_CHARTER = "IAGNS_F-.150_6605|193232";

    @Test
    public void testConstructorForCollectionCharter() throws Exception {

        IdCharter id1 = new IdCharter(IDENTIFIER_COLLECTION, IDENTIFIER_COLLECTION_CHARTER);
        assertEquals(id1.getIdentifier(), IDENTIFIER_COLLECTION_CHARTER);
        assertFalse(id1.isInFond());
        assertFalse(id1.getIdFond().isPresent());
        assertEquals(id1.getIdCollection().get().getIdentifier(), IDENTIFIER_COLLECTION);
        assertEquals(id1.getContentXml().toXML(), ATOM_ID_COLLECTION_CHARTER);
        assertEquals(id1.getContentXml().getText(), Util.encode(ATOM_ID_TEXT_COLLECTION_CHARTER));

        IdCharter id2 = new IdCharter(new AtomId(ATOM_ID_TEXT_COLLECTION_CHARTER));
        assertEquals(id2.getIdentifier(), IDENTIFIER_COLLECTION_CHARTER);
        assertFalse(id2.isInFond());
        assertFalse(id2.getIdFond().isPresent());
        assertEquals(id2.getIdCollection().get().getIdentifier(), IDENTIFIER_COLLECTION);
        assertEquals(id2.getContentXml().toXML(), ATOM_ID_COLLECTION_CHARTER);
        assertEquals(id2.getContentXml().getText(), Util.encode(ATOM_ID_TEXT_COLLECTION_CHARTER));

    }

    @Test
    public void testConstructorForFondCharter() throws Exception {

        IdCharter id1 = new IdCharter(IDENTIFIER_ARCHIVE, IDENTIFIER_FOND, IDENTIFIER_FOND_CHARTER);
        assertEquals(id1.getIdentifier(), IDENTIFIER_FOND_CHARTER);
        assertTrue(id1.isInFond());
        assertFalse(id1.getIdCollection().isPresent());
        assertEquals(id1.getIdFond().get().getIdentifier(), IDENTIFIER_FOND);
        assertEquals(id1.getIdFond().get().getIdArchive().getIdentifier(), IDENTIFIER_ARCHIVE);
        assertEquals(id1.getContentXml().toXML(), ATOM_ID_FOND_CHARTER);
        assertEquals(id1.getContentXml().getText(), Util.encode(ATOM_ID_TEXT_FOND_CHARTER));

        IdCharter id2 = new IdCharter(new AtomId(ATOM_ID_TEXT_FOND_CHARTER));
        assertEquals(id2.getIdentifier(), IDENTIFIER_FOND_CHARTER);
        assertTrue(id2.isInFond());
        assertEquals(id2.getIdFond().get().getIdentifier(), IDENTIFIER_FOND);
        assertEquals(id2.getIdFond().get().getIdArchive().getIdentifier(), IDENTIFIER_ARCHIVE);
        assertEquals(id2.getContentXml().toXML(), ATOM_ID_FOND_CHARTER);
        assertEquals(id2.getContentXml().getText(), Util.encode(ATOM_ID_TEXT_FOND_CHARTER));

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier1() throws Exception {
        new IdCharter("", IDENTIFIER_COLLECTION_CHARTER);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier2() throws Exception {
        new IdCharter(IDENTIFIER_COLLECTION, "");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier3() throws Exception {
        new IdCharter("", IDENTIFIER_FOND, IDENTIFIER_FOND_CHARTER);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier4() throws Exception {
        new IdCharter(IDENTIFIER_ARCHIVE, "", IDENTIFIER_FOND_CHARTER);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithEmptyIdentifier5() throws Exception {
        new IdCharter(IDENTIFIER_ARCHIVE, IDENTIFIER_FOND, "");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithInvalidIdentifier1() throws Exception {
        new IdCharter("tag:www.monasterium.net,2011:/charter/MedDoc|BulgEmp/1192-02-02_sic!_Ioan_Kaliman", IDENTIFIER_COLLECTION_CHARTER);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithInvalidIdentifier2() throws Exception {
        new IdCharter(IDENTIFIER_COLLECTION, "tag:www.monasterium.net,2011:/charter/MedDoc|BulgEmp/1192-02-02_sic!_Ioan_Kaliman");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithInvalidIdentifier3() throws Exception {
        new IdCharter("tag:www.monasterium.net,2011:/charter/RS%7CIAGNS/Char|ters/IAGNS_F-.150_6605|193232", IDENTIFIER_FOND, IDENTIFIER_FOND_CHARTER);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithInvalidIdentifier4() throws Exception {
        new IdCharter(IDENTIFIER_ARCHIVE, "tag:www.monasterium.net,2011:/charter/RS%7CIAGNS/Char|ters/IAGNS_F-.150_6605|193232", IDENTIFIER_FOND_CHARTER);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithInvalidIdentifier5() throws Exception {
        new IdCharter(IDENTIFIER_ARCHIVE, IDENTIFIER_FOND, "tag:www.monasterium.net,2011:/charter/RS%7CIAGNS/Char|ters/IAGNS_F-.150_6605|193232");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorWithWrongAtomIdType() throws Exception {
        new IdCharter(new AtomId("tag:www.monasterium.net,2011:/collection/MedDoc|BulgEmp"));
    }

    @Test
    public void testGetIdCollection() throws Exception {

        IdCharter id1 = new IdCharter(IDENTIFIER_COLLECTION, IDENTIFIER_COLLECTION_CHARTER);
        assertEquals(id1.getIdCollection().get(), new IdCollection(IDENTIFIER_COLLECTION));

        IdCharter id2 = new IdCharter(IDENTIFIER_ARCHIVE, IDENTIFIER_FOND, IDENTIFIER_FOND_CHARTER);
        assertFalse(id2.getIdCollection().isPresent());

    }

    @Test
    public void testGetIdFond() throws Exception {

        IdCharter id1 = new IdCharter(IDENTIFIER_COLLECTION, IDENTIFIER_COLLECTION_CHARTER);
        assertFalse(id1.getIdFond().isPresent());

        IdCharter id2 = new IdCharter(IDENTIFIER_ARCHIVE, IDENTIFIER_FOND, IDENTIFIER_FOND_CHARTER);
        assertEquals(id2.getIdFond().get(), new IdFond(IDENTIFIER_ARCHIVE, IDENTIFIER_FOND));

    }

    @Test
    public void testIsInFond() throws Exception {

        IdCharter id1 = new IdCharter(IDENTIFIER_COLLECTION, IDENTIFIER_COLLECTION_CHARTER);
        assertFalse(id1.isInFond());

        IdCharter id2 = new IdCharter(IDENTIFIER_ARCHIVE, IDENTIFIER_FOND, IDENTIFIER_FOND_CHARTER);
        assertTrue(id2.isInFond());

    }


}