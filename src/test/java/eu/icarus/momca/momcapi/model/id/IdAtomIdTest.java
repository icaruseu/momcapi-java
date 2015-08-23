package eu.icarus.momca.momcapi.model.id;

import eu.icarus.momca.momcapi.model.resource.ResourceType;
import eu.icarus.momca.momcapi.model.xml.atom.AtomId;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 23/08/2015.
 */
public class IdAtomIdTest {

    @Test
    public void testGetType() throws Exception {

        IdAtomId id1 = new IdAtomId(new AtomId("tag:www.monasterium.net,2011:/archive/CH-KASchwyz"));
        assertEquals(id1.getType(), ResourceType.ARCHIVE);
        IdAtomId id2 = new IdAtomId(new AtomId("tag:www.monasterium.net,2011:/charter/RS-IAGNS/Charters/AGNS_F.1_the_fascia_9_Sub_3499%7C1817"));
        assertEquals(id2.getType(), ResourceType.CHARTER);

    }

}