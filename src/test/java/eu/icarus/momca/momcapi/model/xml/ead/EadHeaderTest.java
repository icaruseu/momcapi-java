package eu.icarus.momca.momcapi.model.xml.ead;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 06/09/2015.
 */
public class EadHeaderTest {

    @Test
    public void testConstructor() throws Exception {
        String xml = "<ead:eadheader xmlns:ead=\"urn:isbn:1-931666-22-9\"><ead:eadid /><ead:filedesc><ead:titlestmt><ead:titleproper /><ead:author /></ead:titlestmt></ead:filedesc></ead:eadheader>";
        assertEquals(new EadHeader().toXML(), xml);
    }

}