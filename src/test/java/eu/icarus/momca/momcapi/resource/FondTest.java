package eu.icarus.momca.momcapi.resource;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.testng.Assert.assertEquals;

/**
 * Created by djell on 09/08/2015.
 */
public class FondTest {

    @NotNull
    private static final String FOND_EAD_STRING = "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"> <atom:id>tag:www.monasterium.net,2011:/fond/CH-KASchwyz/Urkunden</atom:id> <atom:title /> <atom:published>2015-08-09T14:30:58.561+02:00</atom:published> <atom:updated>2015-08-09T14:31:10.947+02:00</atom:updated> <atom:author> <atom:email>admin</atom:email> </atom:author> <app:control xmlns:app=\"http://www.w3.org/2007/app\"> <app:draft>no</app:draft> </app:control> <atom:content type=\"application/xml\"> <ead:ead xmlns:ead=\"urn:isbn:1-931666-22-9\"> <ead:eadheader> <ead:eadid /> <ead:filedesc> <ead:titlestmt> <ead:titleproper /> <ead:author /> </ead:titlestmt> </ead:filedesc> </ead:eadheader> <ead:archdesc level=\"otherlevel\"> <ead:did> <ead:abstract /> </ead:did> <ead:dsc> <ead:c level=\"fonds\" xml:base=\"\"> <ead:did> <ead:unitid identifier=\"Urkunden\">Urkunden</ead:unitid> <ead:unittitle>Urkunden</ead:unittitle> </ead:did> <ead:bioghist> <ead:head /> <ead:p /> </ead:bioghist> <ead:custodhist> <ead:head /> <ead:p /> </ead:custodhist> <ead:bibliography> <ead:bibref /> </ead:bibliography> <ead:odd> <ead:head /> <ead:p /> </ead:odd> </ead:c> </ead:dsc> </ead:archdesc> </ead:ead> </atom:content> </atom:entry>";
    @NotNull
    private static final String FOND_PREFERENCES_STRING = "<xrx:preferences xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"> <xrx:param name=\"image-access\">restricted</xrx:param> <xrx:param name=\"dummy-image-url\">http://example.com/dummy.png</xrx:param> <xrx:param name=\"image-server-base-url\" >http://example.com/images</xrx:param> </xrx:preferences>";

    private Fond correctFond;

    @BeforeMethod
    public void setUp() throws Exception {

        MomcaResource fondEad =
                new MomcaResource("Urkunden.ead.xml", "/db/mom-data/metadata.fond.public/CH-KASchwyz", FOND_EAD_STRING);
        MomcaResource fondPreferences =
                new MomcaResource("Urkunden.preferences.xml", "/db/mom-data/metadata.fond.public/CH-KASchwyz", FOND_PREFERENCES_STRING);

        correctFond = new Fond(fondEad, Optional.of(fondPreferences));

    }

    @Test
    public void testGetArchiveId() throws Exception {
        assertEquals(correctFond.getArchiveId().getAtomId().getText(), "tag:www.monasterium.net,2011:/archive/CH-KASchwyz");
    }

    @Test
    public void testGetDummyImageUrl() throws Exception {
        assertEquals(correctFond.getDummyImageUrl().get().toExternalForm(), "http://example.com/dummy.png");
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(correctFond.getId().getAtomId().getText(), "tag:www.monasterium.net,2011:/fond/CH-KASchwyz/Urkunden");
    }

    @Test
    public void testGetIdentifier() throws Exception {
        assertEquals(correctFond.getIdentifier(), "Urkunden");
    }

    @Test
    public void testGetImageAccess() throws Exception {
        assertEquals(correctFond.getImageAccess().get(), ImageAccess.RESTRICTED);
    }

    @Test
    public void testGetImagesUrl() throws Exception {
        assertEquals(correctFond.getImagesUrl().get().toExternalForm(), "http://example.com/images");
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(correctFond.getName(), "Urkunden");
    }

}