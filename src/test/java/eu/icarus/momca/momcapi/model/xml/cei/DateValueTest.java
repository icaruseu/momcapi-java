package eu.icarus.momca.momcapi.model.xml.cei;

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
    public void testGetDay() throws Exception {

        assertFalse(new DateValue("12130700").getDay().isPresent());
        assertFalse(new DateValue("12130799").getDay().isPresent());

        assertTrue(new DateValue("12130207").getDay().isPresent());
        assertEquals(new DateValue("12130207").getDay().get(), new Integer(7));

        assertTrue(new DateValue("9130207").getDay().isPresent());
        assertEquals(new DateValue("9130207").getDay().get(), new Integer(7));

    }

    @Test
    public void testGetMonth() throws Exception {

        assertFalse(new DateValue("12130001").getMonth().isPresent());
        assertFalse(new DateValue("12139901").getMonth().isPresent());

        assertTrue(new DateValue("12130207").getMonth().isPresent());
        assertEquals(new DateValue("12130207").getMonth().get(), new Integer(2));

        assertTrue(new DateValue("9130207").getMonth().isPresent());
        assertEquals(new DateValue("9130207").getMonth().get(), new Integer(2));


    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals(new DateValue("12471027").getValue(), "12471027");
    }

    @Test
    public void testGetYear() throws Exception {

        assertFalse(new DateValue("99999999").getYear().isPresent());

        assertTrue(new DateValue("12130101").getYear().isPresent());
        assertEquals(new DateValue("12130101").getYear().get(), new Integer(1213));

        assertTrue(new DateValue("9130101").getYear().isPresent());
        assertEquals(new DateValue("9130101").getYear().get(), new Integer(913));

    }

    @Test
    public void testIsValid() throws Exception {
        assertFalse(new DateValue("09471027").isValid());
        assertTrue(new DateValue("9471027").isValid());
        assertTrue(new DateValue("12471027").isValid());
    }

    @Test
    public void testValidateNumericDate() throws Exception {
        assertFalse(DateValue.validateNumericDate("09471027"));
        assertTrue(DateValue.validateNumericDate("9471027"));
        assertTrue(DateValue.validateNumericDate("12471027"));
    }
}