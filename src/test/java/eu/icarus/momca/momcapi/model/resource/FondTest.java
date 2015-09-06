package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.id.IdArchive;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 09/08/2015.
 */
public class FondTest {

    @Test
    public void testConstructor1() throws Exception {

        String identifier = "000-Introduction";
        IdArchive idArchive = new IdArchive("IT-BSNSP");
        String name = "000 - Introduzione";
        Fond fond = new Fond(identifier, idArchive, name);

        assertEquals(fond.getIdentifier(), identifier);
        assertEquals(fond.getName(), name);
        assertEquals(fond.getArchiveId(), idArchive);

        // TODO https://trello.com/c/c5LWqgnu

    }


}