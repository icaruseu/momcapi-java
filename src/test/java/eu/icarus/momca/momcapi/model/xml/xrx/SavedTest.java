package eu.icarus.momca.momcapi.model.xml.xrx;

import eu.icarus.momca.momcapi.model.id.IdCharter;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 18/09/2015.
 */
public class SavedTest {

    @Test
    public void test1() throws Exception {

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "Urkunde1");
        Saved saved = new Saved(id, "2015-04-14T09:08:59.817+02:00", "no");

        assertEquals(saved.getId(), id);

        assertEquals(saved.toXML(), "<xrx:saved xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:id>tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/Urkunde1</xrx:id><xrx:start_time>2015-04-14T09:08:59.817+02:00</xrx:start_time><xrx:freigabe>no</xrx:freigabe></xrx:saved>");

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test2() throws Exception {
        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "Urkunde1");
        new Saved(id, "2015-04-14T09:08:59.817+02:00", "what");
    }
}