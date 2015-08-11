package eu.icarus.momca.momcapi.xml.atom;

import eu.icarus.momca.momcapi.xml.Namespace;
import nu.xom.Element;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 08/08/2015.
 */
public class EntryTest {

    @Test
    public void testConstructor() throws Exception {

        String correctXml = "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"><atom:id><atom:id>tag:www.monasterium.net,2011:/archive/DE-BayHStA</atom:id></atom:id><atom:title /><atom:published>2011-05-30T20:31:19.638+02:00</atom:published><atom:updated>2011-05-30T20:31:19.638+02:00</atom:updated><atom:author><atom:email>admin</atom:email></atom:author><app:control xmlns:app=\"http://www.w3.org/2007/app\"><app:draft>no</app:draft></app:control><atom:content type=\"application/xml\"><eag:eag xmlns:eag=\"http://www.archivgut-online.de/eag\" /></atom:content></atom:entry>";

        IdArchive id = new IdArchive("DE-BayHStA");
        Author author = new Author("admin");
        String currentDateTime = "2011-05-30T20:31:19.638+02:00";
        Element childContent = new Element("eag:eag", Namespace.EAG.getUri());

        Entry entry = new Entry(id, author, currentDateTime, childContent);

        assertEquals(entry.toXML(), correctXml);

    }
}