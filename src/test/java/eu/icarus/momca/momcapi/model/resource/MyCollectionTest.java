package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.id.IdMyCollection;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.xml.xrx.Sharing;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 29/09/2015.
 */
public class MyCollectionTest {

    private MyCollection myCollection = new MyCollection(
            "newMyCollection", "A new my collection", new IdUser("admin"), MyCollectionStatus.PRIVATE);

    @Test
    public void testConstructor1() throws Exception {

        MyCollection myCollection = new MyCollection(
                "newMyCollection", "A new my collection", new IdUser("admin"), MyCollectionStatus.PRIVATE);

        assertEquals(myCollection.getId(), new IdMyCollection("newMyCollection"));
        assertTrue(myCollection.getCreator().isPresent());
        assertEquals(myCollection.getCreator().get(), new IdUser("admin"));
        assertEquals(myCollection.getIdentifier(), "newMyCollection");
        assertEquals(myCollection.getName(), "A new my collection");
        assertEquals(myCollection.getResourceName(), "newMyCollection.mycollection.xml");
        assertEquals(myCollection.getParentUri(), "/db/mom-data/xrx.user/admin/metadata.mycollection/newMyCollection");
        assertEquals(myCollection.getSharing().getContent(), "private");
        assertFalse(myCollection.getPreface().isPresent());
        assertEquals(myCollection.getContent().toXML(), "<cei:cei xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:teiHeader><cei:fileDesc><cei:titleStmt><cei:title>A new my collection</cei:title></cei:titleStmt><cei:publicationStmt /></cei:fileDesc></cei:teiHeader><cei:text type=\"collection\"><cei:front><cei:div type=\"preface\" /></cei:front><cei:group><cei:text sameAs=\"\" type=\"collection\" /><cei:text sameAs=\"\" type=\"charter\" /></cei:group><cei:back /></cei:text></cei:cei>");

    }

    @Test
    public void testConstructor2() throws Exception {

        ExistResource resource = new ExistResource("newMyCollection.mycollection.xml", "/db/mom-data/xrx.user/admin/metadata.mycollection/newMyCollection", "<atom:entry xmlns:atom='http://www.w3.org/2005/Atom'><atom:id>tag:www.monasterium.net,2011:/mycollection/newMyCollection</atom:id><atom:title /><atom:published>2015-07-04T12:57:37.577+02:00</atom:published><atom:updated>2015-07-04T12:57:37.577+02:00</atom:updated><atom:author><atom:email>user1.testuser@dev.monasterium.net</atom:email></atom:author><app:control xmlns:app='http://www.w3.org/2007/app'><app:draft>no</app:draft></app:control><xrx:sharing xmlns:xrx='http://www.monasterium.net/NS/xrx'><xrx:visibility>private</xrx:visibility><xrx:user /></xrx:sharing><atom:content type='application/xml'><cei:cei xmlns:cei='http://www.monasterium.net/NS/cei'><cei:teiHeader><cei:fileDesc><cei:titleStmt><cei:title>A new my collection</cei:title></cei:titleStmt><cei:publicationStmt /></cei:fileDesc></cei:teiHeader><cei:text type='collection'><cei:front><cei:div type='preface'><cei:head>headline</cei:head><cei:p>preface</cei:p></cei:div></cei:front><cei:group><cei:text sameAs='' type='collection' /><cei:text sameAs='' type='charter' /></cei:group><cei:back /></cei:text></cei:cei></atom:content></atom:entry>");

        MyCollection myCollection = new MyCollection(resource);

        assertEquals(myCollection.getId(), new IdMyCollection("newMyCollection"));
        assertTrue(myCollection.getCreator().isPresent());
        assertEquals(myCollection.getCreator().get(), new IdUser("user1.testuser@dev.monasterium.net"));
        assertEquals(myCollection.getIdentifier(), "newMyCollection");
        assertEquals(myCollection.getName(), "A new my collection");
        assertEquals(myCollection.getResourceName(), "newMyCollection.mycollection.xml");
        assertEquals(myCollection.getParentUri(), "/db/mom-data/xrx.user/admin/metadata.mycollection/newMyCollection");
        assertEquals(myCollection.getSharing().getContent(), "private");
        assertTrue(myCollection.getPreface().isPresent());
        assertEquals(myCollection.getPreface().get(), "<cei:head>headline</cei:head><cei:p>preface</cei:p>");

        myCollection.regenerateXmlContent();

        assertEquals(myCollection.getContent().toXML(), "<cei:cei xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:teiHeader><cei:fileDesc><cei:titleStmt><cei:title>A new my collection</cei:title></cei:titleStmt><cei:publicationStmt /></cei:fileDesc></cei:teiHeader><cei:text type=\"collection\"><cei:front><cei:div type=\"preface\"><cei:head>headline</cei:head><cei:p>preface</cei:p></cei:div></cei:front><cei:group><cei:text sameAs=\"\" type=\"collection\" /><cei:text sameAs=\"\" type=\"charter\" /></cei:group><cei:back /></cei:text></cei:cei>");

    }

    @Test
    public void testSetIdentifier() throws Exception {

        myCollection.setIdentifier("newId");

        assertEquals(myCollection.getId(), new IdMyCollection("newId"));
        assertEquals(myCollection.getIdentifier(), "newId");
        assertEquals(myCollection.getResourceName(), "newId.mycollection.xml");
        assertEquals(myCollection.getParentUri(), "/db/mom-data/xrx.user/admin/metadata.mycollection/newId");

    }

    @Test
    public void testSetName() throws Exception {

        myCollection.setName("This is the new name");

        assertEquals(myCollection.getName(), "This is the new name");
        assertEquals(myCollection.getContent().toXML(), "<cei:cei xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:teiHeader><cei:fileDesc><cei:titleStmt><cei:title>This is the new name</cei:title></cei:titleStmt><cei:publicationStmt /></cei:fileDesc></cei:teiHeader><cei:text type=\"collection\"><cei:front><cei:div type=\"preface\" /></cei:front><cei:group><cei:text sameAs=\"\" type=\"collection\" /><cei:text sameAs=\"\" type=\"charter\" /></cei:group><cei:back /></cei:text></cei:cei>");

    }

    @Test
    public void testSetPreface() throws Exception {

        myCollection.setPreface("<cei:head>A new preface</cei:head><cei:p>Yeah, fancy</cei:p>");

        assertTrue(myCollection.getPreface().isPresent());
        assertEquals(myCollection.getPreface().get(), "<cei:head>A new preface</cei:head><cei:p>Yeah, fancy</cei:p>");
        assertEquals(myCollection.getContent().toXML(), "<cei:cei xmlns:cei=\"http://www.monasterium.net/NS/cei\"><cei:teiHeader><cei:fileDesc><cei:titleStmt><cei:title>This is the new name</cei:title></cei:titleStmt><cei:publicationStmt /></cei:fileDesc></cei:teiHeader><cei:text type=\"collection\"><cei:front><cei:div type=\"preface\"><cei:head>A new preface</cei:head><cei:p>Yeah, fancy</cei:p></cei:div></cei:front><cei:group><cei:text sameAs=\"\" type=\"collection\" /><cei:text sameAs=\"\" type=\"charter\" /></cei:group><cei:back /></cei:text></cei:cei>");

        myCollection.setPreface("");
        assertFalse(myCollection.getPreface().isPresent());

    }

    @Test
    public void testSetSharing() throws Exception {

        myCollection.setSharing(new Sharing("public", "admin"));

        assertEquals(myCollection.getSharing().getContent(), "public");
        assertEquals(myCollection.getSharing().getUser(), "admin");

    }

    @Test
    public void testSetStatus() throws Exception {

        myCollection.setStatus(MyCollectionStatus.PUBLISHED);

        assertEquals(myCollection.getStatus(), MyCollectionStatus.PUBLISHED);
        assertEquals(myCollection.getParentUri(), "/db/mom-data/metadata.mycollection.public/newId");

    }


}