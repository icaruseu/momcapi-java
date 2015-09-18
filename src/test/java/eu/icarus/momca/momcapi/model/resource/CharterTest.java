package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.TestUtils;
import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.Date;
import eu.icarus.momca.momcapi.model.id.IdCharter;
import eu.icarus.momca.momcapi.model.xml.atom.AtomAuthor;
import eu.icarus.momca.momcapi.model.xml.atom.AtomEntry;
import eu.icarus.momca.momcapi.model.xml.cei.DateExact;
import nu.xom.Element;
import nu.xom.ParsingException;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Created by daniel on 27.06.2015.
 */
public class CharterTest {

    @NotNull
    private static ExistResource getExistResource(String identifier) throws ParsingException, IOException {

        Element ceiElement = (Element) TestUtils.getXmlFromResource(identifier + ".xml").getRootElement().copy();

        IdCharter id = new IdCharter("collection", identifier);

        String time = "2015-09-17T16:53:22.343+02:00";
        AtomAuthor author = new AtomAuthor("author");

        AtomEntry entry = new AtomEntry(id.getContentXml(), author, time, ceiElement);

        String resourceName = identifier + "cei.xml";
        String uri = "/db/mom-data/metadata.charter.public/collection/" + identifier;
        return new ExistResource(resourceName, uri, entry.toXML());

    }

    @NotNull
    private Charter createCharter(String identifier) throws ParsingException, IOException {
        ExistResource resource = getExistResource(identifier);

        return new Charter(resource);
    }

    @Test
    public void testConstructor1() throws Exception {

        IdCharter id = new IdCharter("collection", "charter1");
        Date date = new Date(new DateExact("14180201", "February 1st, 1418"));
        User user = new User("user", "moderator");

        Charter charter = new Charter(id, CharterStatus.PUBLIC, user, date);

        assertEquals(charter.getCharterStatus(), CharterStatus.PUBLIC);
        assertEquals(charter.getParentUri(), "/db/mom-data/metadata.charter.public/collection");
        assertEquals(charter.getResourceName(), "charter1.charter.xml");

        assertEquals(charter.getId(), id);
        assertTrue(charter.getCreator().isPresent());
        assertEquals(charter.getCreator().get(), user.getId());
        assertEquals(charter.getIdno().getId(), "charter1");
        assertEquals(charter.getIdno().getText(), "charter1");
        assertEquals(charter.getDate(), date);

        String correctXml = "";
        assertEquals(charter.toXML(), correctXml);

    }

    @Test
    public void testConstructor2WithEmptyCharter() throws Exception {

        Charter charter = createCharter("empty_charter");

    }

    @Test
    public void testGetId() throws Exception {
        Charter charter = createCharter("empty_charter");
        assertEquals(charter.getId(), new IdCharter("collection", "empty_charter"));
    }

    @Test
    public void testGetIdentifier() throws Exception {
        Charter charter = createCharter("empty_charter");
        assertEquals(charter.getIdentifier(), "empty_charter");
    }

    @Test
    public void testGetValidationProblems() throws Exception {

        String validationErrorMessage = "cvc-complex-type.2.4.a: Invalid content was found starting with element 'cei:dateRange'. One of '{\"http://www.monasterium.net/NS/cei\":traditioForm, \"http://www.monasterium.net/NS/cei\":archIdentifier, \"http://www.monasterium.net/NS/cei\":msIdentifier, \"http://www.monasterium.net/NS/cei\":auth, \"http://www.monasterium.net/NS/cei\":physicalDesc, \"http://www.monasterium.net/NS/cei\":nota, \"http://www.monasterium.net/NS/cei\":figure}' is expected.";

        Charter charter = createCharter("invalid_charter");

        assertEquals(charter.getValidationProblems().size(), 1);
        assertEquals(charter.getValidationProblems().get(0).getMessage(), validationErrorMessage);

    }

    @Test
    public void testIsValidCei() throws Exception {
        Charter charter = createCharter("invalid_charter");
        assertFalse(charter.isValidCei());
    }

    @Test
    public void testSetCharterStatus() throws Exception {

        IdCharter id = new IdCharter("collection", "charter1");
        Date date = new Date(new DateExact("14180201", "February 1st, 1418"));
        User user = new User("user", "moderator");

        Charter charter = new Charter(id, CharterStatus.PUBLIC, user, date);

        charter.setCharterStatus(CharterStatus.IMPORTED);
        assertEquals(charter.getCharterStatus(), CharterStatus.IMPORTED);
        assertEquals(charter.getParentUri(), "/db/mom-data/metadata.charter.import/collection");
        assertEquals(charter.getResourceName(), "charter1.charter.xml");

        charter.setCharterStatus(CharterStatus.PRIVATE);
        assertEquals(charter.getCharterStatus(), CharterStatus.PRIVATE);
        assertEquals(charter.getParentUri(), "/db/mom-data/xrx.user/user/metadata.charter/collection");
        assertEquals(charter.getResourceName(), "charter1.charter.xml");

        charter.setCharterStatus(CharterStatus.SAVED);
        assertEquals(charter.getCharterStatus(), CharterStatus.SAVED);
        assertEquals(charter.getParentUri(), "/db/mom-data/metadata.charter.saved");
        assertEquals(charter.getResourceName(), "tag%3Awww.monasterium.net%2C2011%3A%23charter%23collection%23charter1.xml");

    }

    @Test
    public void testSetIdentifier() throws Exception {

        Charter charter = createCharter("empty_charter");
        String new_identifier = "new_identifier";
        charter.setIdentifier(new_identifier);

        assertEquals(charter.getIdentifier(), new_identifier);
        assertEquals(charter.getId(), new IdCharter("collection", new_identifier));

    }
}