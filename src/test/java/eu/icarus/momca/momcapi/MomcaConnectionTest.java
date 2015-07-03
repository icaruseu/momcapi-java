package eu.icarus.momca.momcapi;

import eu.icarus.momca.momcapi.exist.ExistQueryFactory;
import eu.icarus.momca.momcapi.resource.ExistResource;
import org.exist.xmldb.RemoteCollectionManagementService;
import org.exist.xmldb.XmldbURI;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.xmldb.api.base.Collection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Created by daniel on 25.06.2015.
 */
public class MomcaConnectionTest {

    @NotNull
    private static final ExistQueryFactory QUERY_FACTORY = new ExistQueryFactory();
    private MomcaConnection db;

    @BeforeClass
    public void setUp() throws Exception {
        db = InitMomcaConnection.init();
        assertNotNull(db, "MomcaConnection connection not initialized.");
    }

    @AfterClass
    public void tearDown() throws Exception {
        db.closeConnection();
    }

    @Test
    public void testAddCollection() throws Exception {

        String name = "te@m";
        String path = "/db";

        Class<?> cl = db.getClass();

        Method addCollection = cl.getDeclaredMethod("addCollection", String.class, String.class);
        addCollection.setAccessible(true);
        addCollection.invoke(db, name, path);

        Method removeCollection = cl.getDeclaredMethod("deleteCollection", String.class);
        removeCollection.setAccessible(true);
        removeCollection.invoke(db, path + "/" + name);

        Method getCollection = cl.getDeclaredMethod("getCollection", String.class);
        getCollection.setAccessible(true);
        //noinspection unchecked
        assertFalse(((Optional<Collection>) getCollection.invoke(db, path + "/" + name)).isPresent());

    }

    @Test
    public void testDeleteCollection() throws Exception {

        XmldbURI uri = XmldbURI.create("/db/t|est");

        Field f = db.getClass().getDeclaredField("rootCollection");
        f.setAccessible(true);
        Collection rootCollection = (Collection) f.get(db);

        RemoteCollectionManagementService service = (RemoteCollectionManagementService) rootCollection.getService("CollectionManagementService", "1.0");
        service.createCollection(uri);

        Class<?> cl = db.getClass();
        Method deleteCollectionMethod = cl.getDeclaredMethod("deleteCollection", String.class);
        deleteCollectionMethod.setAccessible(true);
        deleteCollectionMethod.invoke(db, uri.toASCIIString());

        Method getCollectionMethod = cl.getDeclaredMethod("getCollection", String.class);
        getCollectionMethod.setAccessible(true);

        //noinspection unchecked
        assertFalse(((Optional<Collection>) getCollectionMethod.invoke(db, uri.toASCIIString())).isPresent());

    }

    @Test
    public void testDeleteExistResource() throws Exception {

        ExistResource res = new ExistResource("deleteTest.xml", "/db", "<empty/>");
        db.storeExistResource(res);
        db.deleteExistResource(res);
        assertFalse(callGetExistResourceMethod(res.getResourceName(), res.getParentUri()).isPresent());

    }

    @Test
    public void testQueryDatabase() throws Exception {

        Class<?> cl = db.getClass();
        Method method = cl.getDeclaredMethod("queryDatabase", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> queryResults = (List<String>) method.invoke(db, QUERY_FACTORY.queryUserModerator("user1.testuser@dev.monasterium.net"));
        assertEquals(queryResults.get(0), "admin");

    }

    @Test
    public void testStoreExistResource() throws Exception {
        ExistResource res = new ExistResource("write@Test.xml", "/db", "<empty/>");
        db.storeExistResource(res);
        assertTrue(callGetExistResourceMethod(res.getResourceName(), res.getParentUri()).isPresent());
        db.deleteExistResource(res);
    }

    @NotNull
    private Optional<ExistResource> callGetExistResourceMethod(@NotNull String resourceName, @NotNull String parentCollection) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Class<?> cl = db.getClass();
        Method method = cl.getDeclaredMethod("getExistResource", String.class, String.class);
        method.setAccessible(true);
        //noinspection unchecked
        return (Optional<ExistResource>) method.invoke(db, resourceName, parentCollection);

    }

}