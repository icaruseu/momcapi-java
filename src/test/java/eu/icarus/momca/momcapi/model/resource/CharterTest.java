package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.TestUtils;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

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
//        User user = new User();
//
//        Charter charter = new Charter(id, CharterStatus.PUBLIC, user, date);

    }

    @Test
    public void testEmptyCharter() throws Exception {

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
    public void testSetIdentifier() throws Exception {

        Charter charter = createCharter("empty_charter");
        String new_identifier = "new_identifier";
        charter.setIdentifier(new_identifier);

        assertEquals(charter.getIdentifier(), new_identifier);
        assertEquals(charter.getId(), new IdCharter("collection", new_identifier));

    }

    @Test
    public void testtestIsValidCei() throws Exception {
        Charter charter = createCharter("invalid_charter");
        assertFalse(charter.isValidCei());
    }

}