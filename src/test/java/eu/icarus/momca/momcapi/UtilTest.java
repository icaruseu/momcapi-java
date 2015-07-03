package eu.icarus.momca.momcapi;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by daniel on 03.07.2015.
 */
public class UtilTest {

    @Test
    public void testDecode() throws Exception {

        String encodedString = "/db/mom-data/AZK%7CAmbroz";
        String decodedString = "/db/mom-data/AZK|Ambroz";
        assertEquals(Util.decode(encodedString), decodedString);

        String nonEncodedString = "User1@monasterium.net";
        assertEquals(Util.decode(nonEncodedString), nonEncodedString);

    }

    @Test
    public void testEncode() throws Exception {

        String nonEncodedString = "User1@monasterium.net";
        assertEquals(Util.encode(nonEncodedString), "User1%40monasterium.net");

        String alreadyEncodedString = "User1%40monasterium.net";
        assertEquals(Util.encode(alreadyEncodedString), "User1%40monasterium.net");

        String nonEncodedPath = "/db/mom-data/AZK|Ambroz";
        assertEquals(Util.encode(nonEncodedPath), "/db/mom-data/AZK%7CAmbroz");

        String alreadyEncodedPath = "/db/mom-data/AZK%7CAmbroz";
        assertEquals(Util.encode(alreadyEncodedPath), "/db/mom-data/AZK%7CAmbroz");

    }

}