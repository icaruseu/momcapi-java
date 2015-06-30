package eu.icarus.momca.momcapi.atomid;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Created by daniel on 27.06.2015.
 */
public class CharterAtomIdTest {

    @NotNull
    private static final String ARCHIVE_ID = "RS-IAGNS";
    @NotNull
    private static final String COLLECTION_CHARTER_ATOM_ID = "tag:www.monasterium.net,2011:/charter/MedDocBulgEmp/1192-02-02_sic%21_Ioan_Kaliman";
    @NotNull
    private static final String COLLECTION_CHARTER_ID = "1192-02-02_sic!_Ioan_Kaliman";
    @NotNull
    private static final String COLLECTION_ID = "MedDocBulgEmp";
    @NotNull
    private static final String FOND_CHARTER_ATOM_ID = "tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/IAGNS_F-.150_6605%7C193232";
    @NotNull
    private static final String FOND_CHARTER_ID = "IAGNS_F-.150_6605|193232";
    @NotNull
    private static final String FOND_ID = "Charters";
    @NotNull
    private static final String BASE_PATH = String.format("%s/%s", ARCHIVE_ID, FOND_ID);

    @Test
    public void testConstructorForAtomId() throws Exception {
        CharterAtomId id = new CharterAtomId(FOND_CHARTER_ATOM_ID);
        assertEquals(id.getAtomId(), FOND_CHARTER_ATOM_ID);
    }

    @Test
    public void testConstructorForCollectionCharter() throws Exception {
        CharterAtomId id = new CharterAtomId(COLLECTION_ID, COLLECTION_CHARTER_ID);
        assertEquals(id.getAtomId(), COLLECTION_CHARTER_ATOM_ID);
    }

    @Test
    public void testConstructorForFondCharter() throws Exception {
        CharterAtomId id = new CharterAtomId(ARCHIVE_ID, FOND_ID, FOND_CHARTER_ID);
        assertEquals(id.getAtomId(), FOND_CHARTER_ATOM_ID);
    }

    @Test
    public void testGetArchiveId() throws Exception {
        CharterAtomId id = new CharterAtomId(FOND_CHARTER_ATOM_ID);
        assertEquals(id.getArchiveId(), Optional.of(ARCHIVE_ID));
    }

    @Test
    public void testGetBasePath() throws Exception {
        CharterAtomId id = new CharterAtomId(FOND_CHARTER_ATOM_ID);
        assertEquals(id.getBasePath(), BASE_PATH);
    }

    @Test
    public void testGetCharterId() throws Exception {
        CharterAtomId id = new CharterAtomId(COLLECTION_CHARTER_ATOM_ID);
        assertEquals(id.getCharterId(), COLLECTION_CHARTER_ID);
    }

    @Test
    public void testGetCollectionId() throws Exception {
        CharterAtomId id = new CharterAtomId(COLLECTION_CHARTER_ATOM_ID);
        assertEquals(id.getCollectionId(), Optional.of(COLLECTION_ID));
    }

    @Test
    public void testGetFondId() throws Exception {
        CharterAtomId id = new CharterAtomId(FOND_CHARTER_ATOM_ID);
        assertEquals(id.getFondId(), Optional.of(FOND_ID));
    }

    @Test
    public void testIsPartOfArchiveFond() throws Exception {
        CharterAtomId id = new CharterAtomId(FOND_CHARTER_ATOM_ID);
        assertTrue(id.isPartOfArchiveFond());
    }

    @Test
    public void testIsPartOfArchiveFondWithCollectionCharter() throws Exception {
        CharterAtomId id = new CharterAtomId(COLLECTION_CHARTER_ATOM_ID);
        assertFalse(id.isPartOfArchiveFond());
    }

    @Test
    public void testIsPartOfCollection() throws Exception {
        CharterAtomId id = new CharterAtomId(COLLECTION_CHARTER_ATOM_ID);
        assertTrue(id.isPartOfCollection());
    }

    @Test
    public void testIsPartOfCollectionWithFond() throws Exception {
        CharterAtomId id = new CharterAtomId(FOND_CHARTER_ATOM_ID);
        assertFalse(id.isPartOfCollection());
    }

}