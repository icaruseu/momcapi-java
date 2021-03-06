package eu.icarus.momca.momcapi.model.resource;

import eu.icarus.momca.momcapi.TestUtils;
import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.model.id.IdCharter;
import eu.icarus.momca.momcapi.model.id.IdUser;
import eu.icarus.momca.momcapi.model.xml.xrx.Saved;
import nu.xom.Element;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 28.06.2015.
 */
public class UserTest {


    @Test
    public void testConstructor1() throws Exception {

        String correctXml = "<?xml version=\"1.0\"?>\n" +
                "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:username /><xrx:password /><xrx:firstname /><xrx:name /><xrx:email>user</xrx:email><xrx:moderator>moderator</xrx:moderator><xrx:street /><xrx:zip /><xrx:town /><xrx:phone /><xrx:institution /><xrx:info /><xrx:storage><xrx:saved_list><xrx:saved><xrx:id>tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/Urkunde1</xrx:id><xrx:start_time>2015-04-14T09:08:59.817+02:00</xrx:start_time><xrx:freigabe>no</xrx:freigabe></xrx:saved></xrx:saved_list><xrx:bookmark_list /></xrx:storage></xrx:user>\n";

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "Urkunde1");
        Saved saved = new Saved(id, "2015-04-14T09:08:59.817+02:00", "no");

        List<Saved> savedList = new ArrayList<>(0);
        savedList.add(saved);

        User user = new User("user", "moderator");
        user.setSavedCharters(savedList);

        assertEquals(user.getIdentifier(), "user");
        assertEquals(user.getIdModerator().getIdentifier(), "moderator");

        assertEquals(user.toXML(), correctXml);
        assertEquals(user.getSavedCharters(), savedList);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructor1WithoutModerator() throws Exception {
        new User("user", "");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructor1WithoutUser() throws Exception {
        new User("", "moderator");
    }

    @Test
    public void testConstructor2() throws Exception {

        ExistResource resource = new ExistResource("admin.xml", "/db/mom-data/xrx.user", TestUtils.getXmlFromResource("user.xml").toXML());
        User user = new User(resource);

        assertEquals(user.getIdentifier(), "admin");
        assertEquals(user.getId(), new IdUser("admin"));
        assertEquals(user.getIdModerator(), new IdUser("admin"));
        assertEquals(user.getFirstname(), "Admin");
        assertEquals(user.getName(), "MomcAPITest");
        assertEquals(user.getStreet(), "street");
        assertEquals(user.getZip(), "zip");
        assertEquals(user.getTown(), "town");
        assertEquals(user.getPhone(), "phone");
        assertEquals(user.getInstitution(), "institution");
        assertEquals(user.getInfo(), "info");

        assertEquals(user.getAskElements().size(), 1);
        assertEquals(user.getAskElements().get(0).toXML(), "<xrx:askelement date=\"2015-09-18+02:00\" id=\"12092151.622\" size=\"1209\" user=\"user1.testuser%40dev.monasterium.net\" x1=\"362\" x2=\"280\" y1=\"240\" y2=\"929\">\n" +
                "            <xrx:data archive=\"CH-KAE\" charter=\"KAE_Urkunde_Nr_1\" fond=\"Urkunden\">\n" +
                "                <xrx:text>Paragraph</xrx:text>\n" +
                "                <xrx:url>http://www.klosterarchiv.ch/archivalien/KAE_A_II_1-1440/KAE_A_II_1-0067.jpg</xrx:url>\n" +
                "            </xrx:data>\n" +
                "        </xrx:askelement>");

        assertEquals(user.getCroppings().size(), 1);
        assertEquals(user.getCroppings().get(0).toXML(), "<xrx:cropelement id=\"2104108\" name=\"Random Image\" type=\"Various\">\n" +
                "            <xrx:data id=\"RaVa2104108\" imagename=\"PageNr\" status=\"privat\">\n" +
                "                <xrx:img>\n" +
                "                    data:image/jpeg;base64,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\n" +
                "                </xrx:img>\n" +
                "                <xrx:url>/mom/CH-KAE/Urkunden/KAE_Urkunde_Nr_4/charter</xrx:url>\n" +
                "                <xrx:note>Page Number</xrx:note>\n" +
                "            </xrx:data>\n" +
                "        </xrx:cropelement>");

        assertEquals(user.getAnnotations().size(), 3);
        assertEquals(user.getAnnotations().get(1).toXML(), "<xrx:annotationelement id=\"11782114.995\" size=\"1178\" status=\"privat\" x1=\"333\" x2=\"445\" y1=\"245\" y2=\"733\">\n" +
                "            <xrx:data>\n" +
                "                <xrx:text>Paragraph</xrx:text>\n" +
                "                <xrx:url>http://www.klosterarchiv.ch/archivalien/KAE_A_II_1-1440/KAE_A_II_1-0058.jpg</xrx:url>\n" +
                "            </xrx:data>\n" +
                "        </xrx:annotationelement>");

        assertEquals(user.getSavedCharters().size(), 2);
        assertEquals(user.getSavedCharters().get(1).getId().getIdentifier(), "KAE_Urkunde_Nr_1");

        assertEquals(user.getBookmarkedCharters().size(), 1);
        assertEquals(user.getBookmarkedCharters().get(0).getContentAsElement().getText(), "tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/KAE_Urkunde_Nr_1");

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructor2WithoutModerator() throws Exception {

        String invalidUserXml = "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"> <xrx:username>momcapitest</xrx:username> <xrx:password>momcapitest</xrx:password> <xrx:firstname>User1</xrx:firstname> <xrx:name>Testuser</xrx:name> <xrx:email>user1.testuser@dev.monasterium.net.xml</xrx:email> <xrx:moderator></xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>\n";
        ExistResource resource = new ExistResource("user1.testuser@dev.monasterium.net.xml", "/db/mom-data/xrx.user", invalidUserXml);

        new User(resource);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructor2WithoutUser() throws Exception {

        String invalidUserXml = "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"> <xrx:username>momcapitest</xrx:username> <xrx:password>momcapitest</xrx:password> <xrx:firstname>User1</xrx:firstname> <xrx:name>Testuser</xrx:name> <xrx:email></xrx:email> <xrx:moderator>admin</xrx:moderator> <xrx:street /> <xrx:zip /> <xrx:town /> <xrx:phone /> <xrx:institution /> <xrx:info /> <xrx:storage> <xrx:saved_list /> <xrx:bookmark_list /> </xrx:storage> </xrx:user>\n";
        ExistResource resource = new ExistResource("user1.testuser@dev.monasterium.net.xml", "/db/mom-data/xrx.user", invalidUserXml);

        new User(resource);

    }

    @Test
    public void testSetAnnotations() throws Exception {

        String correctXml = "<?xml version=\"1.0\"?>\n" +
                "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:username /><xrx:password /><xrx:firstname /><xrx:name /><xrx:email>user</xrx:email><xrx:moderator>moderator</xrx:moderator><xrx:street /><xrx:zip /><xrx:town /><xrx:phone /><xrx:institution /><xrx:info /><xrx:annotations><xrx:annotationelement id=\"12012106.807\" size=\"1201\" status=\"privat\" x1=\"381\" x2=\"313\" y1=\"236\" y2=\"888\"> <xrx:data> <xrx:text>Paragraph</xrx:text> <xrx:url>http://www.klosterarchiv.ch/archivalien/KAE_A_II_1-1440/KAE_A_II_1-0061.jpg</xrx:url> </xrx:data> </xrx:annotationelement></xrx:annotations><xrx:storage><xrx:saved_list /><xrx:bookmark_list /></xrx:storage></xrx:user>\n";

        Element annotation = Util.parseToElement("<xrx:annotationelement xmlns:xrx=\"http://www.monasterium.net/NS/xrx\" id=\"12012106.807\" size=\"1201\" status=\"privat\" x1=\"381\" x2=\"313\" y1=\"236\" y2=\"888\"> <xrx:data> <xrx:text>Paragraph</xrx:text> <xrx:url>http://www.klosterarchiv.ch/archivalien/KAE_A_II_1-1440/KAE_A_II_1-0061.jpg</xrx:url> </xrx:data> </xrx:annotationelement>");

        List<Element> annotations = new ArrayList<>(0);
        annotations.add(annotation);

        User user = new User("user", "moderator");
        user.setAnnotations(annotations);

        assertEquals(user.getAnnotations().size(), 1);
        assertEquals(user.getAnnotations().get(0).toXML(), annotation.toXML());

        assertEquals(user.toXML(), correctXml);

    }

    @Test
    public void testSetAskElements() throws Exception {

        Element askElement = Util.parseToElement("<xrx:askelement xmlns:xrx=\"http://www.monasterium.net/NS/xrx\" date=\"2015-09-18+02:00\" id=\"12092151.622\" size=\"1209\" user=\"user1.testuser%40dev.monasterium.net\" x1=\"362\" x2=\"280\" y1=\"240\" y2=\"929\"><xrx:data archive=\"CH-KAE\" charter=\"KAE_Urkunde_Nr_1\" fond=\"Urkunden\"><xrx:text>Paragraph</xrx:text><xrx:url>http://www.klosterarchiv.ch/archivalien/KAE_A_II_1-1440/KAE_A_II_1-0067.jpg</xrx:url></xrx:data></xrx:askelement>");

        String correctXml = "<?xml version=\"1.0\"?>\n" +
                "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:username /><xrx:password /><xrx:firstname /><xrx:name /><xrx:email>user</xrx:email><xrx:moderator>moderator</xrx:moderator><xrx:street /><xrx:zip /><xrx:town /><xrx:phone /><xrx:institution /><xrx:info /><xrx:askelements><xrx:askelement date=\"2015-09-18+02:00\" id=\"12092151.622\" size=\"1209\" user=\"user1.testuser%40dev.monasterium.net\" x1=\"362\" x2=\"280\" y1=\"240\" y2=\"929\"><xrx:data archive=\"CH-KAE\" charter=\"KAE_Urkunde_Nr_1\" fond=\"Urkunden\"><xrx:text>Paragraph</xrx:text><xrx:url>http://www.klosterarchiv.ch/archivalien/KAE_A_II_1-1440/KAE_A_II_1-0067.jpg</xrx:url></xrx:data></xrx:askelement></xrx:askelements><xrx:storage><xrx:saved_list /><xrx:bookmark_list /></xrx:storage></xrx:user>\n";

        List<Element> askElements = new ArrayList<>(0);
        askElements.add(askElement);

        User user = new User("user", "moderator");
        user.setAskElements(askElements);

        assertEquals(user.getAskElements().size(), 1);
        assertEquals(user.getAskElements().get(0).toXML(), askElement.toXML());

        assertEquals(user.toXML(), correctXml);

    }

    @Test
    public void testSetBookmarkedCharters() throws Exception {

        String correctXml = "<?xml version=\"1.0\"?>\n" +
                "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:username /><xrx:password /><xrx:firstname /><xrx:name /><xrx:email>user</xrx:email><xrx:moderator>moderator</xrx:moderator><xrx:street /><xrx:zip /><xrx:town /><xrx:phone /><xrx:institution /><xrx:info /><xrx:storage><xrx:saved_list /><xrx:bookmark_list><xrx:bookmark>tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/Urkunde1</xrx:bookmark><xrx:bookmark>tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/Urkunde2</xrx:bookmark></xrx:bookmark_list></xrx:storage></xrx:user>\n";

        IdCharter id1 = new IdCharter("CH-KAE", "Urkunden", "Urkunde1");
        IdCharter id2 = new IdCharter("CH-KAE", "Urkunden", "Urkunde2");
        List<IdCharter> bookmarkedList = new ArrayList<>(0);
        bookmarkedList.add(id1);
        bookmarkedList.add(id2);

        User user = new User("user", "moderator");
        user.setBookmarkedCharters(bookmarkedList);

        assertEquals(user.getBookmarkedCharters().size(), 2);
        assertEquals(user.getBookmarkedCharters().get(1), id2);

        assertEquals(user.toXML(), correctXml);

    }

    @Test
    public void testSetCroppings() throws Exception {

        String correctXml = "<?xml version=\"1.0\"?>\n" +
                "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:username /><xrx:password /><xrx:firstname /><xrx:name /><xrx:email>user</xrx:email><xrx:moderator>moderator</xrx:moderator><xrx:street /><xrx:zip /><xrx:town /><xrx:phone /><xrx:institution /><xrx:info /><xrx:croppings><xrx:cropelement id=\"2104108\" name=\"Random Image\" type=\"Various\"> <xrx:data id=\"RaVa2104108\" imagename=\"PageNr\" status=\"privat\"><xrx:img>data:image/jpeg;base64,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</xrx:img> <xrx:url>/mom/CH-KAE/Urkunden/KAE_Urkunde_Nr_4/charter</xrx:url><xrx:note>Page Number</xrx:note></xrx:data></xrx:cropelement></xrx:croppings><xrx:storage><xrx:saved_list /><xrx:bookmark_list /></xrx:storage></xrx:user>\n";

        Element cropping = Util.parseToElement("<xrx:cropelement xmlns:xrx=\"http://www.monasterium.net/NS/xrx\" id=\"2104108\" name=\"Random Image\" type=\"Various\"> <xrx:data id=\"RaVa2104108\" imagename=\"PageNr\" status=\"privat\"><xrx:img>data:image/jpeg;base64,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</xrx:img> <xrx:url>/mom/CH-KAE/Urkunden/KAE_Urkunde_Nr_4/charter</xrx:url><xrx:note>Page Number</xrx:note></xrx:data></xrx:cropelement>");

        List<Element> croppings = new ArrayList<>(0);
        croppings.add(cropping);

        User user = new User("user", "moderator");
        user.setCroppings(croppings);

        assertEquals(user.getCroppings().size(), 1);
        assertEquals(user.getCroppings().get(0).toXML(), cropping.toXML());

        assertEquals(user.toXML(), correctXml);

    }

    @Test
    public void testSetIdentifier() throws Exception {

        String correctXml = "<?xml version=\"1.0\"?>\n" +
                "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:username /><xrx:password /><xrx:firstname /><xrx:name /><xrx:email>newUser</xrx:email><xrx:moderator>moderator</xrx:moderator><xrx:street /><xrx:zip /><xrx:town /><xrx:phone /><xrx:institution /><xrx:info /><xrx:storage><xrx:saved_list /><xrx:bookmark_list /></xrx:storage></xrx:user>\n";

        User user = new User("user", "moderator");
        user.setIdentifier("newUser");

        assertEquals(user.getIdentifier(), "newUser");
        assertEquals(user.getId(), new IdUser("newUser"));
        assertEquals(user.toXML(), correctXml);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetIdentifierError() throws Exception {
        User user = new User("user", "moderator");
        user.setIdentifier("");
    }

    @Test
    public void testSetModerator() throws Exception {

        String correctXml = "<?xml version=\"1.0\"?>\n" +
                "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:username /><xrx:password /><xrx:firstname /><xrx:name /><xrx:email>user</xrx:email><xrx:moderator>newModerator</xrx:moderator><xrx:street /><xrx:zip /><xrx:town /><xrx:phone /><xrx:institution /><xrx:info /><xrx:storage><xrx:saved_list /><xrx:bookmark_list /></xrx:storage></xrx:user>\n";

        User user = new User("user", "moderator");
        user.setModerator("newModerator");

        assertEquals(user.getIdModerator(), new IdUser("newModerator"));
        assertEquals(user.toXML(), correctXml);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetModeratorError() throws Exception {
        User user = new User("user", "moderator");
        user.setModerator("");
    }

    @Test
    public void testSetSavedCharters() throws Exception {

        String correctXml = "<?xml version=\"1.0\"?>\n" +
                "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:username /><xrx:password /><xrx:firstname /><xrx:name /><xrx:email>user</xrx:email><xrx:moderator>moderator</xrx:moderator><xrx:street /><xrx:zip /><xrx:town /><xrx:phone /><xrx:institution /><xrx:info /><xrx:storage><xrx:saved_list><xrx:saved><xrx:id>tag:www.monasterium.net,2011:/charter/CH-KAE/Urkunden/Urkunde1</xrx:id><xrx:start_time>2015-04-14T09:08:59.817+02:00</xrx:start_time><xrx:freigabe>no</xrx:freigabe></xrx:saved></xrx:saved_list><xrx:bookmark_list /></xrx:storage></xrx:user>\n";

        IdCharter id = new IdCharter("CH-KAE", "Urkunden", "Urkunde1");
        Saved saved = new Saved(id, "2015-04-14T09:08:59.817+02:00", "no");
        List<Saved> savedList = new ArrayList<>(0);
        savedList.add(saved);

        User user = new User("user", "moderator");
        user.setSavedCharters(savedList);

        assertEquals(user.getSavedCharters().size(), 1);
        assertEquals(user.getSavedCharters().get(0).toXML(), saved.toXML());

        assertEquals(user.toXML(), correctXml);

    }

    @Test
    public void testSetTown() throws Exception {

        String correctXml = "<?xml version=\"1.0\"?>\n" +
                "<xrx:user xmlns:xrx=\"http://www.monasterium.net/NS/xrx\"><xrx:username /><xrx:password /><xrx:firstname /><xrx:name /><xrx:email>user</xrx:email><xrx:moderator>moderator</xrx:moderator><xrx:street /><xrx:zip /><xrx:town>Vienna</xrx:town><xrx:phone /><xrx:institution /><xrx:info /><xrx:storage><xrx:saved_list /><xrx:bookmark_list /></xrx:storage></xrx:user>\n";

        User user = new User("user", "moderator");
        user.setTown("Vienna");

        assertEquals(user.getTown(), "Vienna");
        assertEquals(user.toXML(), correctXml);

    }
}