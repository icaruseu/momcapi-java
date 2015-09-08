package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.id.IdFond;
import eu.icarus.momca.momcapi.model.xml.ead.Bibliography;
import eu.icarus.momca.momcapi.model.xml.ead.Odd;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by djell on 09/08/2015.
 */
public class FondTest {

    IdArchive idArchive = new IdArchive("IT-BSNSP");
    String identifier = "000-Introduction";
    String name = "000 - Introduzione";
    private Fond fond;

    @BeforeMethod
    public void setUp() throws Exception {
        fond = new Fond(identifier, idArchive, name);
    }

    @Test
    public void testConstructor1() throws Exception {
        assertEquals(fond.getIdentifier(), identifier);
        assertEquals(fond.getArchiveId(), idArchive);
        assertFalse(fond.getCreator().isPresent());
    }

    @Test
    public void testGetArchiveId() throws Exception {

    }

    @Test
    public void testGetBiogHist() throws Exception {

    }

    @Test
    public void testGetCustodHist() throws Exception {

    }

    @Test
    public void testGetDummyImageUrl() throws Exception {

    }

    @Test
    public void testGetFondPreferences() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(fond.getId(), new IdFond(idArchive.getIdentifier(), identifier));
    }

    @Test
    public void testGetImageAccess() throws Exception {

    }

    @Test
    public void testGetImagesUrl() throws Exception {

    }

    @Test
    public void testSetArchiveId() throws Exception {

        IdArchive newIdArchive = new IdArchive("NewArchive");
        fond.setArchiveId(newIdArchive);

        assertEquals(fond.getArchiveId(), newIdArchive);
        assertTrue(fond.toXML().contains("<atom:id>tag:www.monasterium.net,2011:/fond/NewArchive/000-Introduction</atom:id>"));
        assertEquals(fond.getUri(), "/db/mom-data/metadata.fond.public/NewArchive/000-Introduction/000-Introduction.ead.xml");

    }

    @Test
    public void testSetBibliography() throws Exception {

        assertTrue(fond.getBibliography().getEntries().isEmpty());
        assertFalse(fond.getBibliography().getHeading().isPresent());

        Bibliography bibliography = new Bibliography("Heading", "First entry", "Second entry");
        fond.setBibliography(bibliography);

        assertEquals(fond.getBibliography().getEntries().size(), 2);
        assertTrue(fond.getBibliography().getHeading().isPresent());

        assertTrue(fond.toXML().contains("<ead:head>Heading</ead:head>"));
        assertTrue(fond.toXML().contains("<ead:bibref>First entry</ead:bibref>"));
        assertTrue(fond.toXML().contains("<ead:bibref>Second entry</ead:bibref>"));

        fond.setBibliography(null);

        assertTrue(fond.getBibliography().getEntries().isEmpty());

    }

    @Test
    public void testSetIdentifier() throws Exception {

        String newIdentifier = "NewIdentifier";
        fond.setIdentifier(newIdentifier);

        assertEquals(fond.getIdentifier(), newIdentifier);
        assertTrue(fond.toXML().contains("<ead:unitid identifier=\"NewIdentifier\">NewIdentifier</ead:unitid>"));
        assertTrue(fond.toXML().contains("<atom:id>tag:www.monasterium.net,2011:/fond/IT-BSNSP/NewIdentifier</atom:id>"));
        assertEquals(fond.getUri(), "/db/mom-data/metadata.fond.public/IT-BSNSP/NewIdentifier/NewIdentifier.ead.xml");

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetIdentifierEmpty() throws Exception {
        fond.setIdentifier("");
    }


    @Test
    public void testSetName() throws Exception {

        String newName = "New name";
        fond.setName(newName);

        assertEquals(fond.getName(), newName);
        assertTrue(fond.toXML().contains("<ead:unittitle>New name</ead:unittitle>"));

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetNameEmpty() throws Exception {
        fond.setName("");
    }

    @Test
    public void testSetOddList() throws Exception {

        assertEquals(fond.getOddList().size(), 1);

        List<Odd> oddList = new ArrayList<>();
        oddList.add(new Odd("First Odd:", "Paragraph 1", "Paragraph2"));
        oddList.add(new Odd("Second Odd:", "Paragraph 3", "Paragraph 4"));

        fond.setOddList(oddList);

        assertEquals(fond.getOddList().size(), 2);
        assertTrue(fond.toXML().contains("<ead:head>First Odd:</ead:head>"));
        assertTrue(fond.toXML().contains("<ead:p>Paragraph 3</ead:p>"));

        fond.setOddList(new ArrayList<>(0));

        assertEquals(fond.getOddList().size(), 1);

        assertFalse(fond.toXML().contains("<ead:head>First Odd:</ead:head>"));
        assertFalse(fond.toXML().contains("<ead:p>Paragraph 3</ead:p>"));

    }

}