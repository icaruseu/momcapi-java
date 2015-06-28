package eu.icarus.momca.momcapi.atomid;

import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Created by daniel on 27.06.2015.
 */
public class CharterAtomIdTest {

    private static final String archiveId = "ArchiveId";
    private static final String charterId = "CharterId";
    private static final String collectionId = "CollectionId";
    private static final String collectionCharterAtomId = String.format("tag:www.monasterium.net,2011:/charter/%s/%s", collectionId, charterId);
    private static final String fondId = "FondId";
    private static final String fondCharterAtomId = String.format("tag:www.monasterium.net,2011:/charter/%s/%s/%s", archiveId, fondId, charterId);
    private static final String basePath = String.format("%s/%s", archiveId, fondId);

    @Test
    public void testConstructorForAtomId() throws Exception {
        CharterAtomId id = new CharterAtomId(fondCharterAtomId);
        assertEquals(id.getAtomId(), fondCharterAtomId);
    }

    @Test
    public void testConstructorForCollectionCharter() throws Exception {
        CharterAtomId id = new CharterAtomId(collectionId, charterId);
        assertEquals(id.getAtomId(), collectionCharterAtomId);
    }

    @Test
    public void testConstructorForFondCharter() throws Exception {
        CharterAtomId id = new CharterAtomId(archiveId, fondId, charterId);
        assertEquals(id.getAtomId(), fondCharterAtomId);
    }

    @Test
    public void testGetArchiveId() throws Exception {
        CharterAtomId id = new CharterAtomId(fondCharterAtomId);
        assertEquals(id.getArchiveId(), Optional.of(archiveId));
    }

    @Test
    public void testGetBasePath() throws Exception {
        CharterAtomId id = new CharterAtomId(fondCharterAtomId);
        assertEquals(id.getBasePath(), basePath);
    }

    @Test
    public void testGetCharterId() throws Exception {
        CharterAtomId id = new CharterAtomId(collectionCharterAtomId);
        assertEquals(id.getCharterId(), charterId);
    }

    @Test
    public void testGetCollectionId() throws Exception {
        CharterAtomId id = new CharterAtomId(collectionCharterAtomId);
        assertEquals(id.getCollectionId(), Optional.of(collectionId));
    }

    @Test
    public void testGetFondId() throws Exception {
        CharterAtomId id = new CharterAtomId(fondCharterAtomId);
        assertEquals(id.getFondId(), Optional.of(fondId));
    }

    @Test
    public void testIsPartOfArchiveFond() throws Exception {
        CharterAtomId id = new CharterAtomId(fondCharterAtomId);
        assertTrue(id.isPartOfArchiveFond());
    }

    @Test
    public void testIsPartOfArchiveFondWithCollectionCharter() throws Exception {
        CharterAtomId id = new CharterAtomId(collectionCharterAtomId);
        assertFalse(id.isPartOfArchiveFond());
    }

    @Test
    public void testIsPartOfCollection() throws Exception {
        CharterAtomId id = new CharterAtomId(collectionCharterAtomId);
        assertTrue(id.isPartOfCollection());
    }

    @Test
    public void testIsPartOfCollectionWithFond() throws Exception {
        CharterAtomId id = new CharterAtomId(fondCharterAtomId);
        assertFalse(id.isPartOfCollection());
    }

}