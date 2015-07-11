package eu.icarus.momca.momcapi.xml.cei;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by daniel on 10.07.2015.
 */
public class NumericDateTest {

    @Test
    public void testEquals() throws Exception {
        assertTrue(new NumericDate("12471027").equals(new NumericDate("12471027")));
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals(new NumericDate("12471027").getValue(), "12471027");
    }

    @Test
    public void testIsValid() throws Exception {
        assertFalse(new NumericDate("09471027").isValid());
        assertTrue(new NumericDate("9471027").isValid());
        assertTrue(new NumericDate("12471027").isValid());
    }

    @Test
    public void testValidateNumericDate() throws Exception {
        assertFalse(NumericDate.validateNumericDate("09471027"));
        assertTrue(NumericDate.validateNumericDate("9471027"));
        assertTrue(NumericDate.validateNumericDate("12471027"));
    }

}