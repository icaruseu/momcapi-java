package eu.icarus.momca.momcapi.resource.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 10.07.2015.
 */
public class DateValueTest {

    @Test
    public void testEquals() throws Exception {
        assertTrue(new DateValue("12471027").equals(new DateValue("12471027")));
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals(new DateValue("12471027").getValue(), "12471027");
    }

    @Test
    public void testIsValid() throws Exception {
        assertFalse(new DateValue("09471027").isValid());
        assertTrue(new DateValue("12471027").isValid());
    }

}