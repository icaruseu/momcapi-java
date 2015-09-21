package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.TestUtils;
import eu.icarus.momca.momcapi.model.CharterStatus;
import eu.icarus.momca.momcapi.model.Date;
import eu.icarus.momca.momcapi.model.id.IdCharter;
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
    private static ExistResource getExistResource(String fileName) throws ParsingException, IOException {
        Element element = (Element) TestUtils.getXmlFromResource(fileName).getRootElement().copy();
        String uri = "/db/mom-data/metadata.charter.public/collection";
        return new ExistResource(fileName, uri, element.toXML());
    }

    @NotNull
    private Charter createCharter(String fileName) throws ParsingException, IOException {
        ExistResource resource = getExistResource(fileName);
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
        assertEquals(charter.getResourceName(), "charter1.cei.xml");

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

        Date date = new Date();
        User user = new User("guest", "moderator");

        Charter charter = createCharter("empty_charter.xml");

        assertEquals(charter.getCharterStatus(), CharterStatus.PUBLIC);
        assertEquals(charter.getParentUri(), "/db/mom-data/metadata.charter.public/collection");
        assertEquals(charter.getResourceName(), "empty_charter.xml");

        assertEquals(charter.getId(), new IdCharter("collection", "empty_charter"));
        assertTrue(charter.getCreator().isPresent());
        assertEquals(charter.getCreator().get(), user.getId());
        assertEquals(charter.getIdno().getId(), "empty_charter");
        assertEquals(charter.getIdno().getText(), "New Charter\n" +
                "                ");
        assertEquals(charter.getDate(), date);

    }

    @Test
    public void testGetId() throws Exception {
        Charter charter = createCharter("empty_charter.xml");
        assertEquals(charter.getId(), new IdCharter("collection", "empty_charter"));
    }

    @Test
    public void testGetIdentifier() throws Exception {
        Charter charter = createCharter("empty_charter.xml");
        assertEquals(charter.getIdentifier(), "empty_charter");
    }

    @Test
    public void testGetValidationProblems() throws Exception {

        String validationErrorMessage = "cvc-complex-type.2.4.a: Invalid content was found starting with element 'cei:idno'. One of '{\"http://www.monasterium.net/NS/cei\":persName, \"http://www.monasterium.net/NS/cei\":placeName, \"http://www.monasterium.net/NS/cei\":geogName, \"http://www.monasterium.net/NS/cei\":index, \"http://www.monasterium.net/NS/cei\":testis, \"http://www.monasterium.net/NS/cei\":date, \"http://www.monasterium.net/NS/cei\":dateRange, \"http://www.monasterium.net/NS/cei\":num, \"http://www.monasterium.net/NS/cei\":measure, \"http://www.monasterium.net/NS/cei\":quote, \"http://www.monasterium.net/NS/cei\":cit, \"http://www.monasterium.net/NS/cei\":foreign, \"http://www.monasterium.net/NS/cei\":anchor, \"http://www.monasterium.net/NS/cei\":ref, \"http://www.monasterium.net/NS/cei\":hi, \"http://www.monasterium.net/NS/cei\":lb, \"http://www.monasterium.net/NS/cei\":pb, \"http://www.monasterium.net/NS/cei\":sup, \"http://www.monasterium.net/NS/cei\":c, \"http://www.monasterium.net/NS/cei\":recipient, \"http://www.monasterium.net/NS/cei\":issuer}' is expected.";

        Charter charter = createCharter("invalid_charter.xml");

        assertEquals(charter.getValidationProblems().size(), 1);
        assertEquals(charter.getValidationProblems().get(0).getMessage(), validationErrorMessage);

    }

    @Test
    public void testIsValidCei() throws Exception {
        Charter charter = createCharter("invalid_charter.xml");
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
        assertEquals(charter.getResourceName(), "charter1.cei.xml");

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

        Charter charter = createCharter("empty_charter.xml");
        String new_identifier = "new_identifier";
        charter.setIdentifier(new_identifier);

        assertEquals(charter.getIdentifier(), new_identifier);
        assertEquals(charter.getId(), new IdCharter("collection", new_identifier));

    }
}