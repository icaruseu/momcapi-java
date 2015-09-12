package eu.icarus.momca.momcapi.model;

import eu.icarus.momca.momcapi.exception.MomcaException;
import eu.icarus.momca.momcapi.model.xml.cei.DateAbstract;
import eu.icarus.momca.momcapi.model.xml.cei.DateExact;
import eu.icarus.momca.momcapi.model.xml.cei.DateRange;
import eu.icarus.momca.momcapi.model.xml.cei.DateValue;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

/**
 * Created by djell on 12/09/2015.
 */
public class Date {

    private long dayOffset;
    @NotNull
    private String literalDate;
    @NotNull
    private LocalDate sortingDate = LocalDate.now();

    public Date(@NotNull LocalDate sortingDate, int dayOffset, @NotNull String literalDate) {
        this.sortingDate = sortingDate;
        this.dayOffset = dayOffset;
        this.literalDate = literalDate;
    }

    public Date(@NotNull LocalDate sortingDate, int dayOffset) {

        this.sortingDate = sortingDate;
        this.dayOffset = dayOffset;
        this.literalDate =
                dayOffset == 0 ?
                        sortingDate.format(DateTimeFormatter.ISO_DATE) :
                        getOffsetDate().get().format(DateTimeFormatter.ISO_DATE) + " - " + sortingDate.format(DateTimeFormatter.ISO_DATE);

    }

    public Date(@NotNull DateAbstract ceiDate) {

        if (ceiDate.isUndated()) {
            String message = String.format("The provided date element, `%s`, is undated.", ceiDate);
            throw new IllegalArgumentException(message);
        }

        if (!ceiDate.isValid()) {
            String message = String.format("The date '%s' is invalid.", ceiDate);
            throw new MomcaException(message);
        }

        if (ceiDate instanceof DateExact) {
            initDateExact((DateExact) ceiDate);
        } else {
            initDateRange((DateRange) ceiDate);
        }

        this.literalDate = ceiDate.getLiteralDate();

    }

    public long getDayOffset() {
        return dayOffset;
    }

    @NotNull
    public String getLiteralDate() {
        return literalDate;
    }

    @NotNull
    public Optional<LocalDate> getOffsetDate() {

        Optional<LocalDate> offsetDate = Optional.empty();

        if (dayOffset != 0) {
            offsetDate = Optional.of(getSortingDate().minusDays(getDayOffset()));
        }

        return offsetDate;

    }

    @NotNull
    public LocalDate getSortingDate() {
        return sortingDate;
    }

    private void initDateExact(DateExact dateExact) {

        Optional<Integer> year = dateExact.getDateValue().getYear();
        Optional<Integer> month = dateExact.getDateValue().getMonth();
        Optional<Integer> day = dateExact.getDateValue().getDay();

        if (year.isPresent() && month.isPresent() && !day.isPresent()) {

            sortingDate = LocalDate.of(year.get(), month.get(), 1).with(TemporalAdjusters.lastDayOfMonth());
            dayOffset = this.sortingDate.getMonth().length(this.sortingDate.isLeapYear()) - 1;

        } else if (year.isPresent() && !month.isPresent() && day.isPresent()) {

            sortingDate = LocalDate.of(year.get(), Month.DECEMBER, day.get());
            LocalDate firstDay = LocalDate.of(year.get(), Month.JANUARY, day.get());
            dayOffset = Duration.between(firstDay.atStartOfDay(), sortingDate.atStartOfDay()).toDays();

        } else if (year.isPresent() && !month.isPresent() && !day.isPresent()) {

            sortingDate = LocalDate.of(year.get(), Month.DECEMBER, 31);
            LocalDate firstDay = LocalDate.of(year.get(), Month.JANUARY, 1);
            dayOffset = Duration.between(firstDay.atStartOfDay(), sortingDate.atStartOfDay()).toDays();

        } else {

            sortingDate = LocalDate.of(year.get(), month.get(), day.get());
            dayOffset = 0;

        }

    }

    private void initDateRange(DateRange dateRange) {

        if (!isUndated(dateRange.getFromDateValue()) && isUndated(dateRange.getToDateValue())) {

            DateExact fromDateExact = new DateExact(dateRange.getFromDateValue().getValue(), dateRange.getLiteralDate());
            Date fromDate = new Date(fromDateExact);
            sortingDate = fromDate.getSortingDate();
            dayOffset = fromDate.getDayOffset();

        } else if (isUndated(dateRange.getFromDateValue()) && !isUndated(dateRange.getToDateValue())) {

            DateExact toDateExact = new DateExact(dateRange.getToDateValue().getValue(), dateRange.getLiteralDate());
            Date toDate = new Date(toDateExact);
            sortingDate = toDate.getSortingDate();
            dayOffset = toDate.getDayOffset();

        } else {

            DateExact fromDateExact = new DateExact(dateRange.getFromDateValue().getValue(), dateRange.getLiteralDate());
            DateExact toDateExact = new DateExact(dateRange.getToDateValue().getValue(), dateRange.getLiteralDate());

            if (!fromDateExact.isUndated() && toDateExact.isUndated()) {

                Date tempDate = new Date(fromDateExact);
                sortingDate = tempDate.sortingDate;
                dayOffset = tempDate.getDayOffset();

            } else if (fromDateExact.isUndated() && !toDateExact.isUndated()) {

                Date tempDate = new Date(toDateExact);
                sortingDate = tempDate.sortingDate;
                dayOffset = tempDate.getDayOffset();

            } else {

                sortingDate = new Date(toDateExact).getSortingDate();

                Date startDate = new Date(fromDateExact);
                LocalDate earliestStartDate = startDate.getOffsetDate().orElse(startDate.getSortingDate());

                dayOffset = Duration.between(earliestStartDate.atStartOfDay(), sortingDate.atStartOfDay()).toDays();
            }
        }

    }

    private boolean isUndated(@NotNull DateValue value) {
        String valueString = value.getValue();
        return valueString.isEmpty() || valueString.equals("99999999");
    }


    public DateAbstract toCeiDate() {

        DateAbstract date;

        if (dayOffset == 0) {

            String value = String.valueOf(sortingDate.getYear()) +
                    String.format("%02d", sortingDate.getMonth().getValue()) +
                    String.format("%02d", sortingDate.getDayOfMonth());
            date = new DateExact(value, literalDate);

        } else {

            LocalDate offsetDate = getOffsetDate().get();

            String valueFrom = String.valueOf(offsetDate.getYear()) +
                    String.format("%02d", offsetDate.getMonth().getValue()) +
                    String.format("%02d", offsetDate.getDayOfMonth());

            String valueTo = String.valueOf(sortingDate.getYear()) +
                    String.format("%02d", sortingDate.getMonth().getValue()) +
                    String.format("%02d", sortingDate.getDayOfMonth());

            date = new DateRange(valueFrom, valueTo, literalDate);

        }

        return date;

    }

}
