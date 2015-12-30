package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.Util;
import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.xml.cei.DateAbstract;
import eu.icarus.momca.momcapi.model.xml.cei.DateExact;
import eu.icarus.momca.momcapi.model.xml.cei.DateRange;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.*;

/**
 * Created by djell on 12/09/2015.
 */
public class DateTest {

    @Test
    public void testCompareTo() throws Exception {

        Date empty = new Date();
        Date before = new Date(LocalDate.of(987, 2, 3));
        Date after = new Date(LocalDate.of(989, 2, 6));

        assertTrue(empty.compareTo(empty) == 0);
        assertTrue(empty.compareTo(before) >= 1);
        assertTrue(before.compareTo(empty) <= -1);
        assertTrue(before.compareTo(before) == 0);
        assertTrue(before.compareTo(after) <= -1);
        assertTrue(after.compareTo(before) >= 1);

    }

    @Test
    public void testDateExact() throws Exception {

        DateAbstract dateExact = new DateExact("14690203", "3rd February 1469");

        Date date = new Date(dateExact);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1469, 2, 3));
        assertFalse(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getDaysInRange(), 0);

    }

    @Test
    public void testDateExactBeforeYearThousand() throws Exception {

        DateAbstract dateExact = new DateExact("9870203", "3rd February 987");

        Date date = new Date(dateExact);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(987, 2, 3));
        assertFalse(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getDaysInRange(), 0);

    }

    @Test(expectedExceptions = MomcaException.class)
    public void testDateExactInvalidDate() throws Exception {
        DateAbstract dateExact = new DateExact("", "unknown");
        new Date(dateExact);
    }

    @Test
    public void testDateExactToCeiDate() throws Exception {

        DateAbstract dateExact = new DateExact("9870203", "3rd February 987");
        Date date = new Date(dateExact);

        assertEquals(date.toCeiDate().toXML(), dateExact.toXML());

        date = new Date(LocalDate.of(987, 2, 3), 0);

        assertEquals(date.toCeiDate().toXML(), "<cei:date xmlns:cei=\"http://www.monasterium.net/NS/cei\" value=\"9870203\">0987-02-03</cei:date>");

    }

    @Test
    public void testDateExactUnknownDay() throws Exception {

        DateAbstract dateExact = new DateExact("14690299", "February 1469");

        Date date = new Date(dateExact);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1469, 2, 28));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1469, 2, 1));
        assertEquals(date.getDaysInRange(), 27);

    }

    @Test
    public void testDateExactUnknownDayLeapYear() throws Exception {

        DateAbstract dateExact = new DateExact("14680299", "February 1468");

        Date date = new Date(dateExact);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1468, 2, 29));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1468, 2, 1));
        assertEquals(date.getDaysInRange(), 28);

    }

    @Test
    public void testDateExactUnknownMonthAndDay() throws Exception {

        DateAbstract dateExact = new DateExact("14699999", "1469");

        Date date = new Date(dateExact);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1469, 12, 31));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1469, 1, 1));
        assertEquals(date.getDaysInRange(), 364);

    }

    @Test
    public void testDateExactUnknownMonthAndDayLeapYear() throws Exception {

        DateAbstract dateExact = new DateExact("14689999", "1468");

        Date date = new Date(dateExact);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1468, 12, 31));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1468, 1, 1));
        assertEquals(date.getDaysInRange(), 365);

    }

    @Test
    public void testDateExactUnknownMonthKnownDay() throws Exception {

        DateAbstract dateExact = new DateExact("14399903", "1st on a month in 1439");

        Date date = new Date(dateExact);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1439, 12, 3));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1439, 1, 3));
        assertEquals(date.getDaysInRange(), 334);

    }

    @Test
    public void testDateExactUnknownYear() throws Exception {

        DateAbstract dateExact = new DateExact("99990203", "3rd February");

        Date date = new Date(dateExact);

        assertFalse(date.getSortingDate().isPresent());
        assertEquals(date.getLiteralDate(), "3rd February");

    }

    @Test
    public void testDateRange() throws Exception {

        DateAbstract dateRange = new DateRange("12170201", "12170228", "February 1217");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1217, 2, 28));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1217, 2, 1));
        assertEquals(date.getDaysInRange(), 27);

        date = new Date(LocalDate.of(1217, 2, 28), LocalDate.of(1217, 2, 1), "February 1217");

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1217, 2, 28));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1217, 2, 1));
        assertEquals(date.getDaysInRange(), 27);

    }

    @Test
    public void testDateRangeArbitraryRange() throws Exception {

        DateAbstract dateRange = new DateRange("12500101", "12991231", "Second half of 13th century");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1299, 12, 31));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1250, 1, 1));
        assertEquals(date.getDaysInRange(), 18261);

    }

    @Test(expectedExceptions = MomcaException.class)
    public void testDateRangeInvalidDate() throws Exception {
        DateAbstract dateRange = new DateRange("", "", "12th January 1217");
        new Date(dateRange);
    }

    @Test
    public void testDateRangeLeapYear() throws Exception {

        DateAbstract dateRange = new DateRange("14680201", "14680229", "February 1468");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1468, 2, 29));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1468, 2, 1));
        assertEquals(date.getDaysInRange(), 28);

    }

    @Test
    public void testDateRangeToCeiDate() throws Exception {

        DateAbstract dateRange = new DateRange("9870301", "9870331", "March 987");
        Date date = new Date(dateRange);

        assertEquals(date.toCeiDate().toXML(), dateRange.toXML());

        date = new Date(LocalDate.of(987, 3, 31), 30);

        assertEquals(date.toCeiDate().toXML(), "<cei:dateRange xmlns:cei=\"http://www.monasterium.net/NS/cei\" from=\"9870301\" to=\"9870331\">0987-03-01 - 0987-03-31</cei:dateRange>");

    }

    @Test
    public void testDateRangeUnclearFromDateDay() throws Exception {

        DateAbstract dateRange = new DateRange("12170799", "12170731", "Sometime in July 1217");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1217, 7, 31));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1217, 7, 1));
        assertEquals(date.getDaysInRange(), 30);

    }

    @Test
    public void testDateRangeUnclearFromDateMonth() throws Exception {

        DateAbstract dateRange = new DateRange("12179901", "12170228", "Sometime in 1217");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1217, 2, 28));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1217, 1, 1));
        assertEquals(date.getDaysInRange(), 58);

    }

    @Test
    public void testDateRangeUnclearFromYear() throws Exception {

        DateAbstract dateRange = new DateRange("99990301", "12170331", "Sometime");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1217, 3, 31));
        assertFalse(date.getEarliestPossibleDate().isPresent());

    }

    @Test
    public void testDateRangeUnclearToDateDay() throws Exception {

        DateAbstract dateRange = new DateRange("12170305", "12170399", "Sometime in March 1217");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1217, 3, 31));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1217, 3, 5));
        assertEquals(date.getDaysInRange(), 26);

    }

    @Test
    public void testDateRangeUnclearToDateMonth() throws Exception {

        DateAbstract dateRange = new DateRange("12171001", "12179928", "Sometime in 1217");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1217, 12, 28));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1217, 10, 1));
        assertEquals(date.getDaysInRange(), 88);

    }

    @Test
    public void testDateRangeUnclearToYear() throws Exception {

        DateAbstract dateRange = new DateRange("12170999", "99990112", "Sometime");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1217, 9, 30));
        assertTrue(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getEarliestPossibleDate().get(), LocalDate.of(1217, 9, 1));
        assertEquals(date.getDaysInRange(), 29);

    }

    @Test
    public void testDateRangeUnknownFromDate() throws Exception {

        DateAbstract dateRange = new DateRange("99999999", "12180112", "12th January 1218");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1218, 1, 12));
        assertFalse(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getDaysInRange(), 0);

        String element = "<cei:dateRange xmlns:cei=\"http://www.monasterium.net/NS/cei\" to=\"12129999\" from=\"\">[ca. 1212]</cei:dateRange>";
        dateRange = new DateRange(Util.parseToElement(element));

        date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1212, 12, 31));
        assertEquals(date.getDaysInRange(), 365);

    }

    @Test
    public void testDateRangeUnknownToDate() throws Exception {

        DateAbstract dateRange = new DateRange("12170322", "99999999", "12th January 1217");

        Date date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1217, 3, 22));
        assertFalse(date.getEarliestPossibleDate().isPresent());
        assertEquals(date.getDaysInRange(), 0);

        String element = "<cei:dateRange xmlns:cei=\"http://www.monasterium.net/NS/cei\" to=\"\" from=\"12129999\">[ca. 1212]</cei:dateRange>";
        dateRange = new DateRange(Util.parseToElement(element));

        date = new Date(dateRange);

        assertTrue(date.getSortingDate().isPresent());
        assertEquals(date.getSortingDate().get(), LocalDate.of(1212, 12, 31));
        assertEquals(date.getDaysInRange(), 365);

    }

    @Test
    public void testEquals() throws Exception {

        Date date1 = new Date(LocalDate.of(1217, 10, 31), 30);
        Date date2 = new Date(LocalDate.of(1217, 10, 31), 30);
        Date date3 = new Date(LocalDate.of(1217, 10, 31), 28);
        Date date4 = new Date(LocalDate.of(1217, 10, 31), 30, "October 1217");
        Date date5 = new Date(new DateRange("12171001", "12171031", "October 1217"));
        Date date6 = new Date(new DateRange("12171001", "99999999", "1st October 1217"));
        Date date7 = new Date(LocalDate.of(1217, 10, 1), "1st October 1217");

        assertTrue(date1.equals(date2));
        assertFalse(date1.equals(date3));
        assertFalse(date1.equals(date4));
        assertTrue(date4.equals(date5));
        assertTrue(date6.equals(date7));

    }

    @Test
    public void testGetLiteralDate1() throws Exception {
        Date date = new Date(LocalDate.of(1217, 10, 12), "12th January 1217");
        assertEquals(date.getLiteralDate(), "12th January 1217");

    }

    @Test
    public void testGetLiteralDate2() throws Exception {
        Date date = new Date(LocalDate.of(1217, 10, 31), 30);
        assertEquals(date.getLiteralDate(), "1217-10-01 - 1217-10-31");
    }
}