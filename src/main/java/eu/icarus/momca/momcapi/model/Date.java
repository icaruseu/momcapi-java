package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.xml.cei.DateAbstract;
import eu.icarus.momca.momcapi.model.xml.cei.DateExact;
import eu.icarus.momca.momcapi.model.xml.cei.DateRange;
import eu.icarus.momca.momcapi.model.xml.cei.DateValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

/**
 * Created by djell on 12/09/2015.
 */
public class Date implements Comparable<Date> {

    private long daysInRange = 0;
    @NotNull
    private String literalDate = "Sine dato";
    @NotNull
    private Optional<LocalDate> sortingDate = Optional.empty();

    public Date() {
    }

    public Date(@NotNull String literalDate) {
        this.literalDate = literalDate;
    }

    public Date(@NotNull LocalDate sortingDate, long daysInRange, @NotNull String literalDate) {
        this.sortingDate = Optional.of(sortingDate);
        this.daysInRange = daysInRange;
        this.literalDate = literalDate;
    }

    public Date(@NotNull LocalDate sortingDate, long daysInRange) {
        this(sortingDate, daysInRange, createLiteralDate(sortingDate, daysInRange));
    }

    public Date(@NotNull LocalDate sortingDate) {
        this(sortingDate, 0);
    }

    public Date(@NotNull LocalDate sortingDate, @NotNull String literalDate) {
        this(sortingDate, 0, literalDate);
    }

    public Date(@NotNull LocalDate sortingDate, @NotNull LocalDate earliestPossibleDate, @NotNull String literalDate) {
        this(sortingDate, calculateDaysInRange(earliestPossibleDate, sortingDate), literalDate);
    }

    public Date(@NotNull LocalDate sortingDate, @NotNull LocalDate earliestPossibleDate) {
        this(sortingDate, calculateDaysInRange(earliestPossibleDate, sortingDate));
    }

    public Date(@NotNull DateAbstract ceiDate) {

        if (!ceiDate.isValid()) {
            String message = String.format("The date '%s' is invalid.", ceiDate);
            throw new MomcaException(message);
        }

        if (ceiDate.isUndated()) {

            this.literalDate = ceiDate.getLiteralDate();

        } else {

            if (ceiDate instanceof DateExact) {

                initDateExact((DateExact) ceiDate);

            } else {

                initFromDateRange((DateRange) ceiDate);

            }

            this.literalDate = ceiDate.getLiteralDate();

        }

    }

    private static long calculateDaysInRange(@NotNull LocalDate startDate, @NotNull LocalDate endDate) {
        return Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
    }

    private static String createLiteralDate(@NotNull LocalDate sortingDate, long daysInRange) {
        return daysInRange == 0 ?
                sortingDate.format(DateTimeFormatter.ISO_DATE) :
                String.format("%s - %s",
                        subtractDays(sortingDate, daysInRange).format(DateTimeFormatter.ISO_DATE),
                        sortingDate.format(DateTimeFormatter.ISO_DATE));
    }

    public static LocalDate subtractDays(@NotNull LocalDate date, long daysToSubtract) {
        return date.minusDays(daysToSubtract);
    }

    @Override
    public int compareTo(@NotNull Date otherDate) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (!sortingDate.isPresent() && !otherDate.getSortingDate().isPresent()) {
            return EQUAL;
        }

        if (sortingDate.isPresent() && !otherDate.getSortingDate().isPresent()) {
            return BEFORE;
        }

        if (!sortingDate.isPresent() && otherDate.getSortingDate().isPresent()) {
            return AFTER;
        }

        return sortingDate.get().compareTo(otherDate.getSortingDate().get());

    }

    @Override
    public boolean equals(@Nullable Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Date date = (Date) o;

        if (daysInRange != date.daysInRange) return false;
        if (!literalDate.equals(date.literalDate)) return false;
        return sortingDate.equals(date.sortingDate);

    }

    public long getDaysInRange() {
        return daysInRange;
    }

    @NotNull
    public Optional<LocalDate> getEarliestPossibleDate() {

        Optional<LocalDate> offsetDate = Optional.empty();

        if (sortingDate.isPresent() && daysInRange != 0) {
            offsetDate = Optional.of(subtractDays(sortingDate.get(), daysInRange));
        }

        return offsetDate;

    }

    @NotNull
    public String getLiteralDate() {
        return literalDate;
    }

    @NotNull
    public Optional<LocalDate> getSortingDate() {
        return sortingDate;
    }

    @Override
    public int hashCode() {
        int result = (int) (daysInRange ^ (daysInRange >>> 32));
        result = 31 * result + literalDate.hashCode();
        result = 31 * result + sortingDate.hashCode();
        return result;
    }

    private void initDateExact(DateExact dateExact) {

        Optional<Integer> year = dateExact.getDateValue().getYear();
        Optional<Integer> month = dateExact.getDateValue().getMonth();
        Optional<Integer> day = dateExact.getDateValue().getDay();

        if (year.isPresent() && month.isPresent() && !day.isPresent()) {

            sortingDate = Optional.of(LocalDate.of(year.get(), month.get(), 1).with(TemporalAdjusters.lastDayOfMonth()));
            daysInRange = sortingDate.get().getMonth().length(sortingDate.get().isLeapYear()) - 1;

        } else if (year.isPresent() && !month.isPresent() && day.isPresent()) {

            sortingDate = Optional.of(LocalDate.of(year.get(), Month.DECEMBER, day.get()));
            LocalDate firstDay = LocalDate.of(year.get(), Month.JANUARY, day.get());
            daysInRange = calculateDaysInRange(firstDay, sortingDate.get());

        } else if (year.isPresent() && !month.isPresent() && !day.isPresent()) {

            sortingDate = Optional.of(LocalDate.of(year.get(), Month.DECEMBER, 31));
            LocalDate firstDay = LocalDate.of(year.get(), Month.JANUARY, 1);
            daysInRange = calculateDaysInRange(firstDay, sortingDate.get());

        } else {

            sortingDate = Optional.of(LocalDate.of(year.get(), month.get(), day.get()));
            daysInRange = 0;

        }

    }

    private void initFromDateRange(DateRange dateRange) {

        if (!isUndated(dateRange.getFromDateValue()) && isUndated(dateRange.getToDateValue())) {

            DateExact fromDateExact = new DateExact(dateRange.getFromDateValue().getValue(), dateRange.getLiteralDate());
            Date fromDate = new Date(fromDateExact);
            sortingDate = fromDate.getSortingDate();
            daysInRange = fromDate.getDaysInRange();

        } else if (isUndated(dateRange.getFromDateValue()) && !isUndated(dateRange.getToDateValue())) {

            DateExact toDateExact = new DateExact(dateRange.getToDateValue().getValue(), dateRange.getLiteralDate());
            Date toDate = new Date(toDateExact);
            sortingDate = toDate.getSortingDate();
            daysInRange = toDate.getDaysInRange();

        } else {

            DateExact fromDateExact = new DateExact(dateRange.getFromDateValue().getValue(), dateRange.getLiteralDate());
            DateExact toDateExact = new DateExact(dateRange.getToDateValue().getValue(), dateRange.getLiteralDate());

            if (!fromDateExact.isUndated() && toDateExact.isUndated()) {

                Date tempDate = new Date(fromDateExact);
                sortingDate = tempDate.sortingDate;
                daysInRange = tempDate.getDaysInRange();

            } else if (fromDateExact.isUndated() && !toDateExact.isUndated()) {

                Date tempDate = new Date(toDateExact);
                sortingDate = tempDate.sortingDate;
                daysInRange = tempDate.getDaysInRange();

            } else {

                sortingDate = new Date(toDateExact).getSortingDate();

                Date startDate = new Date(fromDateExact);
                LocalDate earliestStartDate = startDate.getEarliestPossibleDate().orElse(startDate.getSortingDate().get());
                daysInRange = calculateDaysInRange(earliestStartDate, sortingDate.get());

            }
        }

    }

    private boolean isUndated(@NotNull DateValue value) {
        String valueString = value.getValue();
        return valueString.isEmpty() || valueString.equals("99999999");
    }

    @NotNull
    public DateAbstract toCeiDate() {


        return sortingDate.map(date -> {

            DateAbstract dateAbstract;

            if (daysInRange == 0) {

                String value = String.valueOf(date.getYear()) +
                        String.format("%02d", date.getMonth().getValue()) +
                        String.format("%02d", date.getDayOfMonth());
                dateAbstract = new DateExact(value, literalDate);

            } else {

                LocalDate offsetDate = getEarliestPossibleDate().get();

                String valueFrom = String.valueOf(offsetDate.getYear()) +
                        String.format("%02d", offsetDate.getMonth().getValue()) +
                        String.format("%02d", offsetDate.getDayOfMonth());

                String valueTo = String.valueOf(date.getYear()) +
                        String.format("%02d", date.getMonth().getValue()) +
                        String.format("%02d", date.getDayOfMonth());

                dateAbstract = new DateRange(valueFrom, valueTo, literalDate);

            }

            return dateAbstract;

        }).orElse(
                new DateExact("99999999", literalDate)
        );


    }

}
