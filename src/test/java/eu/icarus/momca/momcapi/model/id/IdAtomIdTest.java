package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.model.resource.ResourceType;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by djell on 23/08/2015.
 */
public class IdAtomIdTest {

    @Test
    public void testEquals() throws Exception {

        IdAtomId id1 = new IdAtomId(new AtomId("tag:www.monasterium.net,2011:/archive/CH-KASchwyz"));
        IdAtomId id2 = new IdAtomId(new AtomId("tag:www.monasterium.net,2011:/archive/CH-KASchwyz"));
        IdAtomId id3 = new IdAtomId(new AtomId("tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/AGNS_F.1_the_fascia_9_Sub_3499%7C1817"));

        assertTrue(id1.equals(id2));
        assertFalse(id1.equals(id3));

        IdAtomId id4 = new IdCharter("CH-KAE", "Urkunden", "Urkunde_1");
        IdAtomId id5 = new IdCharter("CH-KAE", "Urkunden", "Urkunde_1");
        IdAtomId id6 = new IdCharter("CH-KAE", "Urkunden", "Urkunde_3");

        assertTrue(id4.equals(id5));
        assertFalse(id4.equals(id6));

    }

    @Test
    public void testGetType() throws Exception {

        IdAtomId id1 = new IdAtomId(new AtomId("tag:www.monasterium.net,2011:/archive/CH-KASchwyz"));
        assertEquals(id1.getType(), ResourceType.ARCHIVE);
        IdAtomId id2 = new IdAtomId(new AtomId("tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/AGNS_F.1_the_fascia_9_Sub_3499%7C1817"));
        assertEquals(id2.getType(), ResourceType.CHARTER);

    }


}