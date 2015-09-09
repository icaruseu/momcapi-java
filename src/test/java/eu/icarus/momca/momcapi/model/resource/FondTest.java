package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.model.ImageAccess;
import eu.icarus.momca.momcapi.model.id.IdArchive;
import eu.icarus.momca.momcapi.model.id.IdFond;
import eu.icarus.momca.momcapi.model.xml.ead.Bibliography;
import eu.icarus.momca.momcapi.model.xml.ead.BiogHist;
import eu.icarus.momca.momcapi.model.xml.ead.CustodHist;
import eu.icarus.momca.momcapi.model.xml.ead.Odd;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
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
        assertFalse(fond.getFondPreferences().isPresent());
        assertEquals(fond.getUri(), "/db/mom-data/metadata.fond.public/IT-BSNSP/000-Introduction/000-Introduction.ead.xml");

    }

    @Test
    public void testConstructor3() throws Exception {

        fond.setImageAccess(ImageAccess.RESTRICTED);

        URL dummyUrl = new URL("http://example.org/image.png");
        fond.setDummyImageUrl(dummyUrl);

        URL imagesUrl = new URL("http://example.org");
        fond.setImagesUrl(imagesUrl);

        List<Odd> oddList = new ArrayList<>();
        oddList.add(new Odd("First Odd:", "Paragraph 1", "Paragraph2"));
        oddList.add(new Odd("Second Odd:", "Paragraph 3", "Paragraph 4"));
        fond.setOddList(oddList);

        Fond newFond = new Fond(fond, fond.getFondPreferences());

        assertEquals(fond.getUri(), "/db/mom-data/metadata.fond.public/IT-BSNSP/000-Introduction/000-Introduction.ead.xml");

        assertEquals(newFond.getIdentifier(), identifier);
        assertEquals(newFond.getArchiveId(), idArchive);
        assertFalse(newFond.getCreator().isPresent());
        assertTrue(newFond.getImageAccess().isPresent());
        assertEquals(newFond.getImageAccess().get(), ImageAccess.RESTRICTED);
        assertTrue(newFond.getDummyImageUrl().isPresent());
        assertEquals(newFond.getDummyImageUrl().get(), dummyUrl);
        assertTrue(newFond.getImagesUrl().isPresent());
        assertEquals(newFond.getImagesUrl().get(), imagesUrl);

        assertEquals(newFond.getOddList().size(), 2);
        assertEquals(newFond.getOddList().get(0).getHeading().getText().get(), "First Odd:");
        assertEquals(newFond.getOddList().get(1).getParagraphs().get(1).getContent().get(), "Paragraph 4");

    }

    @Test
    public void testGetFondPreferences() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(fond.getId(), new IdFond(idArchive.getIdentifier(), identifier));
    }

    @Test
    public void testSetArchiveId() throws Exception {

        fond.setDummyImageUrl(new URL("http://example.org/img.png"));

        IdArchive newIdArchive = new IdArchive("NewArchive");
        fond.setArchiveId(newIdArchive);

        assertEquals(fond.getArchiveId(), newIdArchive);
        assertTrue(fond.toXML().contains("<atom:id>tag:www.monasterium.net,2011:/fond/NewArchive/000-Introduction</atom:id>"));
        assertEquals(fond.getUri(), "/db/mom-data/metadata.fond.public/NewArchive/000-Introduction/000-Introduction.ead.xml");
        assertEquals(fond.getFondPreferences().get().getUri(), "/db/mom-data/metadata.fond.public/NewArchive/000-Introduction/000-Introduction.preferences.xml");

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
    public void testSetBiogHist() throws Exception {

        assertFalse(fond.getBiogHist().getHeading().getText().isPresent());
        assertEquals(fond.getBiogHist().getParagraphs().size(), 1);
        assertFalse(fond.getBiogHist().getParagraphs().get(0).getContent().isPresent());

        BiogHist BiogHist = new BiogHist("BiogHist", "Paragraph 1", "Paragraph 2");
        fond.setBiogHist(BiogHist);

        assertTrue(fond.getBiogHist().getHeading().getText().isPresent());
        assertEquals(fond.getBiogHist().getHeading().getText().get(), "BiogHist");
        assertEquals(fond.getBiogHist().getParagraphs().size(), 2);
        assertEquals(fond.getBiogHist().getParagraphs().get(0).getContent().get(), "Paragraph 1");
        assertEquals(fond.getBiogHist().getParagraphs().get(1).getContent().get(), "Paragraph 2");
        assertTrue(fond.toXML().contains("<ead:head>BiogHist</ead:head>"));
        assertTrue(fond.toXML().contains("<ead:p>Paragraph 1</ead:p>"));
        assertTrue(fond.toXML().contains("<ead:p>Paragraph 2</ead:p>"));

        fond.setBiogHist(null);

        assertFalse(fond.getBiogHist().getHeading().getText().isPresent());
        assertEquals(fond.getBiogHist().getParagraphs().size(), 1);
        assertFalse(fond.getBiogHist().getParagraphs().get(0).getContent().isPresent());
        assertFalse(fond.toXML().contains("<ead:head>BiogHist</ead:head>"));
        assertFalse(fond.toXML().contains("<ead:p>Paragraph 1</ead:p>"));
        assertFalse(fond.toXML().contains("<ead:p>Paragraph 2</ead:p>"));

    }

    @Test
    public void testSetCustodHist() throws Exception {

        assertFalse(fond.getCustodHist().getHeading().getText().isPresent());
        assertEquals(fond.getCustodHist().getParagraphs().size(), 1);
        assertFalse(fond.getCustodHist().getParagraphs().get(0).getContent().isPresent());

        CustodHist custodHist = new CustodHist("CustodHist", "Paragraph 1", "Paragraph 2");
        fond.setCustodHist(custodHist);

        assertTrue(fond.getCustodHist().getHeading().getText().isPresent());
        assertEquals(fond.getCustodHist().getHeading().getText().get(), "CustodHist");
        assertEquals(fond.getCustodHist().getParagraphs().size(), 2);
        assertEquals(fond.getCustodHist().getParagraphs().get(0).getContent().get(), "Paragraph 1");
        assertEquals(fond.getCustodHist().getParagraphs().get(1).getContent().get(), "Paragraph 2");
        assertTrue(fond.toXML().contains("<ead:head>CustodHist</ead:head>"));
        assertTrue(fond.toXML().contains("<ead:p>Paragraph 1</ead:p>"));
        assertTrue(fond.toXML().contains("<ead:p>Paragraph 2</ead:p>"));

        fond.setCustodHist(null);

        assertFalse(fond.getCustodHist().getHeading().getText().isPresent());
        assertEquals(fond.getCustodHist().getParagraphs().size(), 1);
        assertFalse(fond.getCustodHist().getParagraphs().get(0).getContent().isPresent());
        assertFalse(fond.toXML().contains("<ead:head>CustodHist</ead:head>"));
        assertFalse(fond.toXML().contains("<ead:p>Paragraph 1</ead:p>"));
        assertFalse(fond.toXML().contains("<ead:p>Paragraph 2</ead:p>"));

    }

    @Test
    public void testSetDummyImageUrl() throws Exception {

        assertFalse(fond.getDummyImageUrl().isPresent());
        assertFalse(fond.getFondPreferences().isPresent());

        URL dummyImageUrl = new URL("http://example.org/img.png");
        fond.setDummyImageUrl(dummyImageUrl);

        assertTrue(fond.getDummyImageUrl().isPresent());
        assertEquals(fond.getDummyImageUrl().get(), dummyImageUrl);
        assertTrue(fond.getFondPreferences().isPresent());
        assertTrue(fond.getFondPreferences().get().toXML().contains("<xrx:param name=\"dummy-image-url\">http://example.org/img.png</xrx:param>"));

        fond.setDummyImageUrl(null);
        fond.setImageAccess(ImageAccess.RESTRICTED);

        assertFalse(fond.getDummyImageUrl().isPresent());
        assertFalse(fond.getFondPreferences().get().toXML().contains("<xrx:param name=\"dummy-image-url\"/>"));

    }

    @Test
    public void testSetIdentifier() throws Exception {

        fond.setDummyImageUrl(new URL("http://example.org/img.png"));

        String newIdentifier = "NewIdentifier";
        fond.setIdentifier(newIdentifier);

        assertEquals(fond.getIdentifier(), newIdentifier);
        assertTrue(fond.toXML().contains("<ead:unitid identifier=\"NewIdentifier\">NewIdentifier</ead:unitid>"));
        assertTrue(fond.toXML().contains("<atom:id>tag:www.monasterium.net,2011:/fond/IT-BSNSP/NewIdentifier</atom:id>"));
        assertEquals(fond.getUri(), "/db/mom-data/metadata.fond.public/IT-BSNSP/NewIdentifier/NewIdentifier.ead.xml");
        assertEquals(fond.getFondPreferences().get().getUri(), "/db/mom-data/metadata.fond.public/IT-BSNSP/NewIdentifier/NewIdentifier.preferences.xml");

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetIdentifierEmpty() throws Exception {
        fond.setIdentifier("");
    }

    @Test
    public void testSetImageAccess() throws Exception {

        assertFalse(fond.getImageAccess().isPresent());
        assertFalse(fond.getFondPreferences().isPresent());

        fond.setImageAccess(ImageAccess.RESTRICTED);

        assertTrue(fond.getImageAccess().isPresent());
        assertEquals(fond.getImageAccess().get(), ImageAccess.RESTRICTED);
        assertTrue(fond.getFondPreferences().get().toXML().contains("<xrx:param name=\"image-access\">restricted</xrx:param>"));

        fond.setImageAccess(null);
        fond.setDummyImageUrl(new URL("http://www.url.de/image.png"));

        assertFalse(fond.getImageAccess().isPresent());
        assertTrue(fond.getFondPreferences().get().toXML().contains("<xrx:param name=\"image-access\">free</xrx:param>"));

    }

    @Test
    public void testSetImagesUrl() throws Exception {

        assertFalse(fond.getImagesUrl().isPresent());
        assertFalse(fond.getFondPreferences().isPresent());

        URL ImagesUrl = new URL("http://example.org");
        fond.setImagesUrl(ImagesUrl);

        assertTrue(fond.getImagesUrl().isPresent());
        assertEquals(fond.getImagesUrl().get(), ImagesUrl);
        assertTrue(fond.getFondPreferences().isPresent());
        assertTrue(fond.getFondPreferences().get().toXML().contains("<xrx:param name=\"image-server-base-url\">http://example.org</xrx:param>"));

        fond.setImagesUrl(null);
        fond.setImageAccess(ImageAccess.RESTRICTED);

        assertFalse(fond.getImagesUrl().isPresent());
        assertFalse(fond.getFondPreferences().get().toXML().contains("<xrx:param name=\"image-server-base-url\"/>"));

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